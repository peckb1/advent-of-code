package me.peckb.aoc._2018.calendar.day15

import me.peckb.aoc._2018.calendar.day15.Day15.GameDijkstra.SpaceWithPath
import me.peckb.aoc._2018.calendar.day15.Day15.Space.Player.Elf
import me.peckb.aoc._2018.calendar.day15.Day15.Space.Player.Goblin
import me.peckb.aoc._2018.calendar.day15.Day15.Space.Empty
import me.peckb.aoc._2018.calendar.day15.Day15.Space.Player
import me.peckb.aoc._2018.calendar.day15.Day15.Space.Wall
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.Dijkstra
import me.peckb.aoc.pathing.DijkstraNodeWithCost
import kotlin.Int.Companion.MAX_VALUE
import kotlin.Int.Companion.MIN_VALUE

class Day15 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val gameMap = input.toMutableList().mapIndexed { y, row ->
      row.mapIndexed { x, spaceChar ->
        when (spaceChar) {
          '#' -> Wall(x, y)
          '.' -> Empty(x, y)
          'E' -> Elf(x, y)
          'G' -> Goblin(x, y)
          else -> throw IllegalStateException("Invalid space $spaceChar")
        }
      }.toMutableList()
    }

    var oneTeamLeft = false
    while(!oneTeamLeft) {
      // grab the players in "reading order"
      val players = gameMap.findPlayers()
      val (goblins, elves) = players.partition { it is Goblin }

      // each player needs to find the "reading order" path to all enemies still alive
      players.forEach playerLoop@ { player ->
        if (oneTeamLeft) return@playerLoop
        when (player) {
          is Goblin -> {
            val remainingElves = elves.filter { it.hitPoints > 0 }
            if (remainingElves.isEmpty()) { oneTeamLeft = true; return@playerLoop }
            val myUp = gameMap[player.y - 1][player.x]
            val myLeft = gameMap[player.y][player.x - 1]
            val myRight = gameMap[player.y][player.x + 1]
            val myDown = gameMap[player.y + 1][player.x]
            var adjacentElf = listOf(myUp, myLeft, myRight, myDown).filterIsInstance<Elf>().firstOrNull()

            if (adjacentElf == null) {
              val paths = GameDijkstra(gameMap).solve(player)
              val closestPaths = remainingElves.flatMap { elf ->
                val u = gameMap[elf.y - 1][elf.x]
                val l = gameMap[elf.y][elf.x - 1]
                val r = gameMap[elf.y][elf.x + 1]
                val d = gameMap[elf.y + 1][elf.x]

                listOf(u, l, r, d).filterIsInstance<Empty>().map { paths[it] }
              }.filterNotNull().sortedBy { it.cost }

              if (closestPaths.isNotEmpty()) {
                val options = closestPaths.takeWhile { it.cost == closestPaths.first().cost }

                val (x, y) = options.sortedBy { it.steps.first().first }.sortedBy { it.steps.first().second }.first().steps.first()
                gameMap[y][x] = player
                gameMap[player.y][player.x] = Empty(player.x, player.y)
              }
            }

            adjacentElf = adjacentElf ?: run {
              val u = gameMap[player.y - 1][player.x]
              val l = gameMap[player.y][player.x - 1]
              val r = gameMap[player.y][player.x + 1]
              val D = gameMap[player.y + 1][player.x]
              listOf(u, l, r, D).filterIsInstance<Elf>().firstOrNull()
            }

            adjacentElf?.let { it.hitPoints -= player.attackPower }
          }
          is Elf -> {  }
        }
      }

      // take the shorted path to the closest space next to an enemy

      // if we are close to an enemy attack them
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    -1
  }

  sealed class Space(var x: Int, var y: Int) {
    class Wall(_x: Int, _y: Int) : Space(_x, _y)
    class Empty(_x: Int, _y: Int): Space(_x, _y)
    sealed class Player(_x: Int, _y: Int, var attackPower: Int = 3, var hitPoints: Int = 200): Space(_x, _y) {
      class Elf(_x: Int, _y: Int) : Player(_x, _y)
      class Goblin(_x: Int, _y: Int) : Player(_x, _y)
    }
  }

  data class Path(val steps: List<Pair<Int, Int>>, val cost: Int = steps.size) : Comparable<Path> {
    override fun compareTo(other: Path): Int {
      return cost.compareTo(other.cost)
    }
  }

  class GameDijkstra(val gameMap: List<List<Space>>) : Dijkstra<Space, Path, SpaceWithPath> {
    override fun Space.withCost(cost: Path) = SpaceWithPath(this, cost)
    override fun minCost() = Path(emptyList(), MIN_VALUE)
    override fun maxCost() = Path(emptyList(), MAX_VALUE)
    override fun Path.plus(cost: Path): Path {
      return cost
    }

    inner class SpaceWithPath(private val space: Space, private val path: Path) : DijkstraNodeWithCost<Space, Path> {
      override fun neighbors(): List<DijkstraNodeWithCost<Space, Path>> {
        val u = gameMap[space.y - 1][space.x]
        val l = gameMap[space.y][space.x - 1]
        val r = gameMap[space.y][space.x + 1]
        val d = gameMap[space.y + 1][space.x]

        val emptySpaces = listOf(u, l, r, d).filterIsInstance<Empty>()
        return emptySpaces.map { emptySpace ->
          val (x, y) = emptySpace.x to emptySpace.y
          SpaceWithPath(emptySpace, Path(path.steps.plus(x to y)))
        }
      }

      override fun node() = space

      override fun cost() = path

      override fun compareTo(other: DijkstraNodeWithCost<Space, Path>) = path.compareTo(other.cost())
    }
  }

  private fun List<List<Space>>.findPlayers(): List<Player> {
    val map = this
    return buildList {
      map.forEach { row ->
        row.forEach { space ->
          if (space is Player) add(space)
        }
      }
    }
  }
}