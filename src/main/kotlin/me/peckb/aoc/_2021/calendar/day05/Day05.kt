package me.peckb.aoc._2021.calendar.day05

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.ranges.IntProgression.Companion.fromClosedRange

data class Point(val x : Int, val y: Int)

typealias Line = Pair<Point, Point>
typealias Map = Array<Array<Int>>

class Day05 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    private const val MAP_SIZE = 1000
    private fun createEmptyMap() = (0 until MAP_SIZE).map { Array(MAP_SIZE) { 0 } }.toTypedArray()
  }

  fun nonDiagonalOverlapCount(fileName: String) = generatorFactory.forFile(fileName).readAs(::line) { input ->
    val map = createEmptyMap()

    input.filter { it.isVerticalOrHorizontal() }
      .forEach { line -> adjustAreaWithNonDiagonal(map, line) }

    map.overlap()
  }

  fun fullOverlapCount(fileName: String) = generatorFactory.forFile(fileName).readAs(::line) { input ->
    val map = createEmptyMap()

    input.forEach { line ->
      if (line.isVerticalOrHorizontal()) {
        adjustAreaWithNonDiagonal(map, line)
      } else {
        adjustAreaWithDiagonal(map, line)
      }
    }

    map.overlap()
  }

  private fun adjustAreaWithNonDiagonal(area: Map, line: Pair<Point, Point>) {
    val sortedX = line.sortedX()
    val sortedY = line.sortedY()

    sortedX.forEach { x ->
      sortedY.forEach { y ->
        area[y][x]++
      }
    }
  }

  private fun adjustAreaWithDiagonal(area: Map, line: Line) {
    val xStep = line.second.x.compareTo(line.first.x)
    val xProgression = fromClosedRange(line.first.x, line.second.x, xStep)

    val yStep = line.second.y.compareTo(line.first.y)
    val yProgression = fromClosedRange(line.first.y, line.second.y, yStep)

    xProgression.zip(yProgression).forEach { (x, y) ->
      area[y][x]++
    }
  }

  private fun line(line: String): Line = line.split("->").let { coordinates ->
    val points = coordinates.flatMap { coordinateString ->
      coordinateString.trim()
        .split(",")
        .map { it.toInt() }
        .zipWithNext { x, y -> Point(x, y) }
    }
    points.first() to points.last()
  }

  private fun Line.isVerticalOrHorizontal() = first.x == second.x || first.y == second.y

  private fun Line.sortedX() = listOf(first.x, second.x).sorted().let { it.first() .. it.last() }

  private fun Line.sortedY() = listOf(first.y, second.y).sorted().let { it.first() .. it.last() }

  private fun Map.overlap() = sumOf { row -> row.count { it >= 2 } }
}
