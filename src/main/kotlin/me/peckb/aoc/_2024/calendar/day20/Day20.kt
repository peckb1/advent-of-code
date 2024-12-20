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
    lateinit var end: Location

    val maze = mutableListOf<MutableList<Space>>()
    input.forEachIndexed { y, line ->
      val row = mutableListOf<Space>()
      line.forEachIndexed { x, c ->
        when (c) {
          '#' -> row.add(Space.FULL)
          '.' -> row.add(Space.EMPTY)
          'S' -> row.add(Space.EMPTY)
          'E' -> row.add(Space.EMPTY.also { end = Location(y, x) } )
        }
      }
      maze.add(row)
    }

    val solver = object : GenericIntDijkstra<Location>() {}

    val costs = solver.solve(end.withArea(maze))
    var cheats = 0

    val explorationRange = (-maxCheatTime .. maxCheatTime)
    val explorationDeltas = explorationRange.flatMapIndexed { y, yStep ->
      explorationRange.mapIndexedNotNull { x, xStep ->
        val stepCount = abs(yStep) + abs(xStep)
        if (stepCount <= maxCheatTime) {
          yStep to xStep
        } else {
          null
        }
      }
    }

    costs.entries.forEach { (cheatStart, _) ->
      val reachableEmptySpaces = mutableSetOf<Location>()

      val curY = cheatStart.y
      val curX = cheatStart.x

      explorationDeltas.forEach { (yStep, xStep) ->
        val y = curY + yStep
        val x = curX + xStep

        if (y in maze.indices && x in maze[y].indices && maze[y][x] == Space.EMPTY) {
          reachableEmptySpaces.add(Location(y, x))
        }
      }

      reachableEmptySpaces.forEach { endSpace ->
        val costAtCheatStart = costs[cheatStart]!!
        val costAtCheatEnd = costs[endSpace]!!
        val distanceTravelled = cheatStart.distanceFrom(endSpace)

        val timeSaved = costAtCheatStart - costAtCheatEnd - distanceTravelled
        if (timeSaved >= 100) { cheats++ }
      }
    }

    return cheats
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
