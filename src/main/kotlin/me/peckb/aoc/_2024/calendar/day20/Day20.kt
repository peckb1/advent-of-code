package me.peckb.aoc._2024.calendar.day20

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.GenericIntDijkstra
import java.util.LinkedList
import java.util.PriorityQueue
import java.util.Queue
import kotlin.math.abs

class Day20 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    lateinit var start: Location
    lateinit var end: Location

    val walls = mutableListOf<Location>()

    val maze = mutableListOf<MutableList<Space>>()
    input.forEachIndexed { y, line ->
      val row = mutableListOf<Space>()
      line.forEachIndexed { x, c ->
        when (c) {
          '#' -> row.add(Space.FULL).also { walls.add(Location(y, x)) }
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

    val maxCheat = 2

    data class CheatData(val location: Location, val cheatTimeUsed: Int)

    costs.entries.sumOf { (cheatStart, _) ->
      // map of location to a list of how much cheat cost you spent getting there.
      val exploreSpots = mutableMapOf<Location, List<Int>>()

      val toVisit = PriorityQueue<CheatData> { p1, p2 -> p1.cheatTimeUsed.compareTo(p2.cheatTimeUsed) }

      toVisit.add(CheatData(cheatStart, 0))
      while(toVisit.isNotEmpty()) {
        val (loc, cheatTimeUsed) = toVisit.poll()

        exploreSpots.merge(loc, listOf(cheatTimeUsed)) { a, b -> a + b }

        if (cheatTimeUsed < maxCheat) {
          Direction.entries.forEach {
            val step = it.newLocation(loc.y, loc.x)
            val (y, x) = step

            if (y in maze.indices && x in maze[y].indices) {
              toVisit.add(CheatData(step, cheatTimeUsed + 1))
            }
          }
        }
      }

      val actualSpots = exploreSpots
        .filter { costs[it.key] != null }

      val savings = actualSpots.entries.sumOf { (loc, cheatCosts) ->
        val costAtCheatStart = costs[cheatStart]!!
        val costAtCheatEnd = noCheatCost - costs[loc]!!

        cheatCosts.count { cheatCost ->
          val myCost = costAtCheatStart + cheatCost + costAtCheatEnd
          myCost + 100 <= noCheatCost
        }
      }

      savings
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    lateinit var start: Location
    lateinit var end: Location

    val walls = mutableListOf<Location>()

    val maze = mutableListOf<MutableList<Space>>()
    input.forEachIndexed { y, line ->
      val row = mutableListOf<Space>()
      line.forEachIndexed { x, c ->
        when (c) {
          '#' -> row.add(Space.FULL).also { walls.add(Location(y, x)) }
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

    val maxCheat = 20

    data class CheatData(val location: Location, val cheatTimeUsed: Int)

//    var cheatsThatWork = 0L
    val cheats = mutableMapOf<Int, Int>()

    costs.entries.forEach { (cheatStart, _) ->
      val reachableEmptySpaces = mutableSetOf<Location>()

      val curY = cheatStart.y
      val curX = cheatStart.x

      (-maxCheat .. maxCheat).forEach { yStep ->
        val y = curY + yStep
        (-maxCheat .. maxCheat).forEach { xStep ->
          val x = curX + xStep
          val stepCount = abs(yStep) + abs(xStep)

          if (stepCount <= maxCheat && y in maze.indices && x in maze[y].indices && maze[y][x] == Space.EMPTY) {
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

    cheats.values.sum()
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

  fun newLocation(y: Int, x: Int) = Location(y + yDelta, x + xDelta)
}
