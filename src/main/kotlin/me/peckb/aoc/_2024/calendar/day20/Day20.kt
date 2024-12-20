package me.peckb.aoc._2024.calendar.day20

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.GenericIntDijkstra
import java.util.PriorityQueue

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

    data class CheatData(val location: Location, val wallsPhased: List<Location>, val cheatTimeUsed: Int)

    costs.entries.sumOf { (cheatStart, _) ->
      // map of location to a list of how much cheat cost you spent getting there.
      val exploreSpots = mutableMapOf<Pair<Location, List<Location>>, List<Int>>()

      val toVisit = PriorityQueue<CheatData> { p1, p2 -> p1.cheatTimeUsed.compareTo(p2.cheatTimeUsed) }

      toVisit.add(CheatData(cheatStart, emptyList(), 0))
      while(toVisit.isNotEmpty()) {
        val (loc, wallsPhased, cheatTimeUsed) = toVisit.poll()

        exploreSpots.merge(loc to wallsPhased, listOf(cheatTimeUsed)) { a, b -> a + b }

        if (cheatTimeUsed < maxCheat) {
          Direction.entries.forEach {
            val step = it.newLocation(loc.y, loc.x)
            val (y, x) = step

            if (y in maze.indices && x in maze[y].indices) {
              val newWalls = if (maze[y][x] == Space.FULL) {
                wallsPhased + Location(y, x)
              } else {
                wallsPhased
              }
              toVisit.add(CheatData(step, newWalls, cheatTimeUsed + 1))
            }
          }
        }
      }

      val actualSpots = exploreSpots
        .filter { costs[it.key.first] != null }

      val savings = actualSpots.entries.sumOf { (pair, cheatCosts) ->
        val costAtCheatStart = costs[cheatStart]!!
        val costAtCheatEnd = noCheatCost - costs[pair.first]!!

        cheatCosts.count { cheatCost ->
          val myCost = costAtCheatStart + cheatCost + costAtCheatEnd
          myCost + 100 <= noCheatCost
        }
      }

      savings
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->

  }
}

data class Location(val y: Int, val x: Int) : GenericIntDijkstra.DijkstraNode<Location> {
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
