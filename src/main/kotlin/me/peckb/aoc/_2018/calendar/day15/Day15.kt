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
    var turns = 0
    println(turns)
    gameMap.forEach { println(it.joinToString("")) }
    while(!oneTeamLeft) {
      // grab the players in "reading order"
      val players = gameMap.findPlayers()
      val (goblins, elves) = players.partition { it is Goblin }

      players.forEach playerLoop@ { player ->
        if (oneTeamLeft) return@playerLoop
        if (player.hitPoints < 0) return@playerLoop

        oneTeamLeft = when (player) {
          is Goblin -> {
            player.takeTurn(gameMap, elves) { maybeElves ->
              maybeElves.filterIsInstance<Elf>().minByOrNull { elf -> elf.hitPoints }.also { maybeElf ->
                if (maybeElf != null && maybeElves.count { (it as? Elf)?.hitPoints == maybeElf.hitPoints } > 1) {
                  -1
                }
              }
            }
          }
          is Elf -> {
            player.takeTurn(gameMap, goblins) { maybeGoblins ->
              maybeGoblins.filterIsInstance<Goblin>().minByOrNull { goblin -> goblin.hitPoints }.also { maybeGoblin ->
                if (maybeGoblin != null && maybeGoblins.count { (it as? Goblin)?.hitPoints == maybeGoblin.hitPoints } > 1) {
                  -1
                }
              }
            }
          }
        }
      }

      turns ++
      println(turns)
      gameMap.forEach { println(it.joinToString("")) }
    }

    println(turns)
    gameMap.forEach { println(it.joinToString("")) }
    println(gameMap.findPlayers().map {
      "$it (${it.x}, ${it.y}) ${it.hitPoints}"
    })

    // 232680 too high
    // 225746 too high
    val remainingHP = gameMap.findPlayers().sumOf { it.hitPoints }
    val completedTurns = turns - 1
    remainingHP * completedTurns
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    -1
  }

  sealed class Space(var x: Int, var y: Int) {
    class Wall(_x: Int, _y: Int) : Space(_x, _y) {
      override fun toString() = "#"
    }
    class Empty(_x: Int, _y: Int): Space(_x, _y) {
      override fun toString() = "."
    }
    sealed class Player(_x: Int, _y: Int, var attackPower: Int = 3, var hitPoints: Int = 200): Space(_x, _y) {
      class Elf(_x: Int, _y: Int) : Player(_x, _y) {
        override fun toString() = "E"
      }
      class Goblin(_x: Int, _y: Int) : Player(_x, _y) {
        override fun toString() = "G"
      }

      fun takeTurn(gameMap: List<MutableList<Space>>, enemies: List<Player>, findEnemy: (List<Space>) -> Player?) : Boolean {
        val remainingEnemies = enemies.filter { it.hitPoints > 0 }
        if (remainingEnemies.isEmpty()) return true

        val myUp = gameMap[y - 1][x]
        val myLeft = gameMap[y][x - 1]
        val myRight = gameMap[y][x + 1]
        val myDown = gameMap[y + 1][x]
        var adjacentEnemy = findEnemy(listOf(myUp, myLeft, myRight, myDown))

        if (adjacentEnemy == null) {
          val paths = GameDijkstra(gameMap).solve(this)
          val closestPaths = remainingEnemies.flatMap { elf ->
            val u = gameMap[elf.y - 1][elf.x]
            val l = gameMap[elf.y][elf.x - 1]
            val r = gameMap[elf.y][elf.x + 1]
            val d = gameMap[elf.y + 1][elf.x]

            listOf(u, l, r, d).filterIsInstance<Empty>().map { paths[it] }
          }.filterNotNull().sortedBy { it.cost }

          if (closestPaths.isNotEmpty()) {
            val options = closestPaths.takeWhile { it.cost == closestPaths.first().cost }

            val (newX, newY) = options.sortedBy { it.steps.first().first }.minByOrNull { it.steps.first().second }!!.steps.first()
            // println("Moving from $x, $y to $newX, $newY")
            gameMap[y][x] = Empty(x, y)
            gameMap[newY][newX] = this.also { x = newX; y = newY; }
          } else {
            // println("$this ($x, $y) Unable to Move")
          }
        }

        adjacentEnemy = adjacentEnemy ?: run {
          val u = gameMap[y - 1][x]
          val l = gameMap[y][x - 1]
          val r = gameMap[y][x + 1]
          val d = gameMap[y + 1][x]
          findEnemy(listOf(u, l, r, d))
        }

        adjacentEnemy?.let {
          // println("$this ($x, $y) Attacking $it (${it.x}, ${it.y}) ${it.hitPoints} -> ${it.hitPoints - attackPower}")
          it.hitPoints -= attackPower
          if (it.hitPoints <= 0) {
            println("DEATH")
            gameMap[it.y][it.x] = Empty(it.x, it.y)
          }
        }

        return false
      }
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