package me.peckb.aoc._2024.calendar.day10

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day10 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { lines ->
    val (topoMap, trailHeads) = setup(lines)

    fun dfs(location: Location) : Set<Location> {
      val myElevation = topoMap[location.y][location.x]
      if (myElevation == 9) return setOf(location)

      return getNeighbors(location, topoMap).flatMap { dfs(it) }.toSet()
    }

    trailHeads.sumOf { dfs(it).size }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { lines ->
    val (topoMap, trailHeads) = setup(lines)

    fun dfs(location: Location) : Int {
      val myElevation = topoMap[location.y][location.x]
      if (myElevation == 9) return 1

      return getNeighbors(location, topoMap).sumOf { dfs(it) }
    }

    trailHeads.sumOf { dfs(it) }
  }

  private fun getNeighbors(location: Location, topoMap: MutableList<MutableList<Int>>) : List<Location> {
    val (y, x) = location

    val n = Location(y-1, x)
    val e = Location(y, x+1)
    val s = Location(y+1, x)
    val w = Location(y, x-1)

    val myElevation = topoMap[y][x]

    return listOf(n, e, s, w)
      .filter { it.y in topoMap.indices && it.x in topoMap[it.y].indices }
      .filter { topoMap[it.y][it.x] == myElevation + 1 }
  }

  private fun setup(lines: Sequence<String>): Pair<MutableList<MutableList<Int>>, MutableList<Location>> {
    val topoMap = mutableListOf<MutableList<Int>>()
    val trailHeads = mutableListOf<Location>()

    lines.forEachIndexed { yIndex, line ->
      val row = mutableListOf<Int>()
      line.forEachIndexed { xIndex, c ->
        val elevation = c.digitToIntOrNull() ?: -1

        if (elevation == 0) { trailHeads.add(Location(yIndex, xIndex)) }
        row.add(elevation)
      }
      topoMap.add(row)
    }

    return topoMap to trailHeads
  }
}

data class Location(val y: Int, val x: Int)
