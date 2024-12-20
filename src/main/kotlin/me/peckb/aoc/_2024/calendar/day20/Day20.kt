package me.peckb.aoc._2024.calendar.day20

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.GenericIntDijkstra
import kotlin.math.abs

class Day20 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    findCheats(input, 2)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    findCheats(input, 20)
  }

  private fun findCheats(input: Sequence<String>, maxCheatTime: Int): Int {
    lateinit var start: Location
    lateinit var end: Location

    val maze = mutableListOf<MutableList<Space>>()
    input.forEachIndexed { y, line ->
      val row = mutableListOf<Space>()
      line.forEachIndexed { x, c ->
        when (c) {
          '#' -> row.add(Space.FULL)
          '.' -> row.add(Space.EMPTY)
          'S' -> row.add(Space.EMPTY.also { start = Location(y, x) } )
          'E' -> row.add(Space.EMPTY.also { end = Location(y, x) } )
        }
      }
      maze.add(row)
    }

    val solver = object : GenericIntDijkstra<Location>() {}

    val costs = solver.solve(start.withArea(maze))
    val noCheatCost = costs[end]!!
    val cheats = mutableMapOf<Int, Int>()

    costs.entries.forEach { (cheatStart, _) ->
      val reachableEmptySpaces = mutableSetOf<Location>()

      val curY = cheatStart.y
      val curX = cheatStart.x

      val explorationRange = (-maxCheatTime .. maxCheatTime)

      explorationRange.forEach { yStep ->
        val y = curY + yStep
        explorationRange.forEach { xStep ->
          val x = curX + xStep
          val stepCount = abs(yStep) + abs(xStep)

          if (stepCount <= maxCheatTime && y in maze.indices && x in maze[y].indices && maze[y][x] == Space.EMPTY) {
            val step = Location(y, x)
            reachableEmptySpaces.add(step)
          }
        }
      }

      reachableEmptySpaces.forEach { endSpace ->
        val costAtCheatStart = noCheatCost - costs[cheatStart]!!
        val costAtCheatEnd = noCheatCost - costs[endSpace]!!
        val distanceTravelled = cheatStart.distanceFrom(endSpace)

        val timeSaved = (costAtCheatStart - costAtCheatEnd) - distanceTravelled
        if (timeSaved >= 100) {
          cheats.merge(timeSaved, 1, Int::plus)
        }
      }
    }

    return cheats.values.sum()
  }
}

data class Location(val y: Int, val x: Int) : GenericIntDijkstra.DijkstraNode<Location> {
  fun distanceFrom(other: Location): Int {
    return abs(other.y - y) + abs(other.x - x)
  }

  lateinit var area: MutableList<MutableList<Space>>

  fun withArea(area: MutableList<MutableList<Space>>)   = apply { this.area = area }

  override fun neighbors(): Map<Location, Int> {
    return Direction.entries.mapNotNull { d ->
      val (newY, newX) = d.yDelta + y to d.xDelta + x
      if (newY in area.indices && newX in area[newY].indices && area[newY][newX] == Space.EMPTY) {
        Location(newY, newX).withArea(area)
      } else { null }
    }.associateWith { 1 }
  }
}

enum class Space { FULL, EMPTY }

enum class Direction(val yDelta: Int, val xDelta: Int) {
  N(-1, 0),
  E(0, 1),
  S(1, 0),
  W(0, -1);
}
