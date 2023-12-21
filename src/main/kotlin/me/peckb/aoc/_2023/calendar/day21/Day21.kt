package me.peckb.aoc._2023.calendar.day21

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day21 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val map = mutableListOf<List<Char>>()

    lateinit var start: Point

    input.forEachIndexed { rowIndex, row ->
      val r = row.toCharArray().mapIndexed { colIndex, c ->
        when (c) {
          'S' -> '.'.also { start = Point(rowIndex, colIndex) }
          else -> c
        }
      }
      map.add(r)
    }

    val stepsToTake = 64

    val spacesICanStandOn = mutableSetOf<Point>().apply { add(start) }

    repeat(stepsToTake) {
      val newSteps = mutableSetOf<Point>()
      spacesICanStandOn.toList().forEach { step ->
        val (row, col) = step
        val stepDeltas = DIRECTIONS.mapNotNull {
          it.takeIf { (dr, dc) ->
            (0..map.lastIndex).contains(row + dr) && (0..map[row + dr].lastIndex).contains(col + dc)
          }
        }.filter { (dr, dc) -> map[row + dr][col + dc] != '#' }

        stepDeltas.forEach { (dr, dc) -> newSteps.add(Point(row + dr, col + dc)) }
      }
      spacesICanStandOn.clear()
      spacesICanStandOn.addAll(newSteps)
    }

    spacesICanStandOn.size
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val map = mutableListOf<List<Char>>()
    lateinit var start: Location

    input.forEachIndexed { rowIndex, row ->
      val r = row.toCharArray().mapIndexed { colIndex, c ->
        when (c) {
          'S' -> '.'.also { start = Location(Point(rowIndex, colIndex), 0, 0) }
          else -> c
        }
      }
      map.add(r)
    }

    val height: Int = map.size
    val width: Int  = map[0].size
    val iterations  = 26501365

    val iterationsUntilFirstEdge = iterations % width
    val cornerToCornerIterations = width * 2
    val steps = iterationsUntilFirstEdge + cornerToCornerIterations

    val locations = createLocationsFromMap(map, width, height, start, steps)
    val counts = countPositions(locations)

    val tips: List<Long>       = TIPS.mapNotNull { counts[it]?.toLong() }
    val smallEdges: List<Long> = SMALL_EDGES.mapNotNull { counts[it]?.toLong() }
    val bigEdges: List<Long>   = BIG_EDGES.mapNotNull { counts[it]?.toLong() }
    val evenCenter: Long       = counts[CENTER_IF_EVEN]?.toLong() ?: 0
    val oddCenter: Long        = counts[CENTER_IF_ODD]?.toLong() ?: 0

    val edgeCount = iterations / width

    tips.sum() +
      (smallEdges.sum() * (edgeCount)) +
      (bigEdges.sum() * (edgeCount - 1)) +
      (evenCenter * (edgeCount) * (edgeCount)) +
      (oddCenter * (edgeCount - 1) * (edgeCount - 1))
  }

  private fun countPositions(locations: Set<Location>): Map<Pair<Int, Int>, Int> {
    return mutableMapOf<Pair<Int, Int>, Int>().apply {
      locations.forEach { location -> merge(location.x to location.y, 1, Int::plus) }
    }
  }

  private fun createLocationsFromMap(
    map: MutableList<List<Char>>,
    width: Int,
    height: Int,
    start: Location,
    steps: Int
  ): Set<Location> {
    val locations = mutableSetOf<Location>().apply { add(start) }

    repeat(steps) {
      val nextLocations = mutableSetOf<Location>().apply {
        locations.forEach { loc ->
          val (row, col) = loc.point

          loc.point.neighbors(map).forEach { add(Location(it, loc.x, loc.y)) }

          if (col == 0)          add(Location(Point(row = row, col = width - 1), x = loc.x - 1, y = loc.y))
          if (col == width - 1)  add(Location(Point(row = row, col = 0), x = loc.x + 1, y = loc.y))
          if (row == 0)          add(Location(Point(row = height - 1, col = col), x = loc.x, y = loc.y - 1))
          if (row == height - 1) add(Location(Point(row = 0, col = col), x = loc.x, y = loc.y + 1))
        }
      }
      locations.clear()
      locations.addAll(nextLocations)
    }

    return locations
  }

  data class Point(val row: Int, val col: Int) {
    fun neighbors(map: List<List<Char>>): List<Point> {
      return if (map[row][col] == '#') { emptyList() } else {
        DIRECTIONS.map { (dr, dc) -> row + dr to col + dc }
          .mapNotNull { (r, c) ->
            Point(r, c).takeIf {  (0 <= r && r < map.size && 0 <= c && c < map[r].size && map[r][c] == '.') }
          }
      }
    }
  }

  data class Location(val point: Point, val x: Int, val y: Int)

  companion object {
    val NORTH = (-1 to  0)
    val SOUTH = ( 1 to  0)
    val EAST  = ( 0 to  1)
    val WEST  = ( 0 to -1)

    val DIRECTIONS = listOf(NORTH, SOUTH, EAST, WEST)

    val CENTER_IF_EVEN = (0 to 1)
    val CENTER_IF_ODD  = (0 to 0)

    val SMALL_EDGES = listOf(-2 to -1, -2 to 1, 2 to -1, 2 to 1)
    val BIG_EDGES   = listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1)
    val TIPS        = listOf(-2 to  0,  2 to 0, 0 to -2, 0 to 2)
  }
}
