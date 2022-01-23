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
              maybeElves.filterIsInstance<Elf>().sortedWith { p1, p2 ->
                when (val hitPointComp = p1.hitPoints.compareTo(p2.hitPoints)) {
                  0 -> {
                    when (val yComp = p1.y.compareTo(p2.y)) {
                      0 -> p1.x.compareTo(p2.x)
                      else -> yComp
                    }
                  }
                  else -> hitPointComp
                }
              }.firstOrNull()
            }
          }
          is Elf -> {
            player.takeTurn(gameMap, goblins) { maybeGoblins ->
              maybeGoblins.filterIsInstance<Goblin>().sortedWith { p1, p2 ->
                when (val hitPointComp = p1.hitPoints.compareTo(p2.hitPoints)) {
                  0 -> {
                    when (val yComp = p1.y.compareTo(p2.y)) {
                      0 -> p1.x.compareTo(p2.x)
                      else -> yComp
                    }
                  }
                  else -> hitPointComp
                }
              }.firstOrNull()
            }
          }
        }
      }

      turns ++
    }

    val remainingHP = gameMap.findPlayers().sumOf { it.hitPoints }
    val completedTurns = turns - 1
    remainingHP * completedTurns
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val inputList = input.toMutableList()
    var someElvesDied = true
    var result = 0
    var power = 4
    while(someElvesDied) {
      val gameMap = inputList.mapIndexed { y, row ->
        row.mapIndexed { x, spaceChar ->
          when (spaceChar) {
            '#' -> Wall(x, y)
            '.' -> Empty(x, y)
            'E' -> Elf(x, y, power)
            'G' -> Goblin(x, y)
            else -> throw IllegalStateException("Invalid space $spaceChar")
          }
        }.toMutableList()
      }
      val originalElfCount = gameMap.findPlayers().filterIsInstance<Elf>().size

      var oneTeamLeft = false
      var turns = 0
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
                maybeElves.filterIsInstance<Elf>().sortedWith { p1, p2 ->
                  when (val hitPointComp = p1.hitPoints.compareTo(p2.hitPoints)) {
                    0 -> {
                      when (val yComp = p1.y.compareTo(p2.y)) {
                        0 -> p1.x.compareTo(p2.x)
                        else -> yComp
                      }
                    }
                    else -> hitPointComp
                  }
                }.firstOrNull()
              }
            }
            is Elf -> {
              player.takeTurn(gameMap, goblins) { maybeGoblins ->
                maybeGoblins.filterIsInstance<Goblin>().sortedWith { p1, p2 ->
                  when (val hitPointComp = p1.hitPoints.compareTo(p2.hitPoints)) {
                    0 -> {
                      when (val yComp = p1.y.compareTo(p2.y)) {
                        0 -> p1.x.compareTo(p2.x)
                        else -> yComp
                      }
                    }
                    else -> hitPointComp
                  }
                }.firstOrNull()
              }
            }
          }
        }

        turns ++
      }


      val remainingHP = gameMap.findPlayers().sumOf { it.hitPoints }
      val completedTurns = turns - 1
      result = remainingHP * completedTurns
      val elfCount = gameMap.findPlayers().filterIsInstance<Elf>().size
      someElvesDied = elfCount != originalElfCount
      if (someElvesDied) power++
    }

    result
  }

  sealed class Space(var x: Int, var y: Int) {
    class Wall(_x: Int, _y: Int) : Space(_x, _y) {
      override fun toString() = "#"
    }
    class Empty(_x: Int, _y: Int): Space(_x, _y) {
      override fun toString() = "."
    }
    sealed class Player(_x: Int, _y: Int, var attackPower: Int = 3, var hitPoints: Int = 200): Space(_x, _y) {
      class Elf(_x: Int, _y: Int, _attackPower: Int = 3) : Player(_x, _y, _attackPower) {
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
          val closestPaths = remainingEnemies.flatMap enemyMap@ { enemy ->
            if (enemy.hitPoints <= 0) throw IllegalStateException("Why am I looking at an alive enemy?")

            val u = gameMap[enemy.y - 1][enemy.x]
            val l = gameMap[enemy.y][enemy.x - 1]
            val r = gameMap[enemy.y][enemy.x + 1]
            val d = gameMap[enemy.y + 1][enemy.x]

            listOf(u, l, r, d).filterIsInstance<Empty>().map { paths[it] }
          }.filterNotNull().sortedBy { it.cost }

          if (closestPaths.isNotEmpty()) {
            val options = closestPaths.takeWhile { it.cost == closestPaths.first().cost }

            val (newX, newY) = options.sortedWith { p1, p2 ->
              val p1Step = p1.steps.last()
              val p2Step = p2.steps.last()
              when (val yComparison = p1Step.y.compareTo(p2Step.y)) {
                0 -> p1Step.x.compareTo(p2Step.x)
                else -> yComparison
              }
            }.first().steps.first()

            gameMap[y][x] = Empty(x, y)
            gameMap[newY][newX] = this.also { x = newX; y = newY; }
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
          it.hitPoints -= attackPower
          if (it.hitPoints <= 0) {
            gameMap[it.y][it.x] = Empty(it.x, it.y)
          }
        }

        return false
      }
    }
  }

  data class Path(val steps: List<Point>, val cost: Int = steps.size) : Comparable<Path> {
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
          SpaceWithPath(emptySpace, Path(path.steps.plus(Point(x, y))))
        }
      }

      override fun node() = space

      override fun cost() = path

      override fun compareTo(other: DijkstraNodeWithCost<Space, Path>): Int {
        return when (val pathComp = path.compareTo(other.cost())) {
          0 -> {
            when (val yComp = path.steps.first().y.compareTo(other.cost().steps.first().y)) {
              0 -> path.steps.first().x.compareTo(other.cost().steps.first().x)
              else -> yComp
            }
          }
          else -> pathComp
        }
      }
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

  data class Point(val x: Int, val y: Int)
}