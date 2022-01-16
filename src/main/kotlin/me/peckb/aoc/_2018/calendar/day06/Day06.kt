package me.peckb.aoc._2018.calendar.day06

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.Int.Companion.MAX_VALUE
import kotlin.Int.Companion.MIN_VALUE
import kotlin.math.abs

class Day06 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::point) { input ->
    val points = input.toList()
    val area = generateArea(points)

    val pointsToIgnore = points.filter { (x, y) ->
      x == area.left || x == area.right || y == area.top || y == area.bottom
    }.toSet()

    val counts = mutableMapOf<Point, Int>()

    forPoints(area) { x, y ->
      val distances = points.map { it to abs(x - it.x) + abs(y - it.y) }.sortedBy { it.second }
      val (closest, secondClosest) = distances.take(2)
      if (closest.second < secondClosest.second) counts.merge(closest.first, 1, Int::plus)
    }

    counts.filterNot { pointsToIgnore.contains(it.key) }.maxOf { it.value }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::point) { input ->
    val points = input.toList()
    val area = generateArea(points)

    var pointsWithinRegion = 0
    forPoints(area) { x, y ->
      val distanceSum = points.sumOf { point -> abs(x - point.x) + abs(y - point.y) }
      if (distanceSum < DISTANCE) pointsWithinRegion++
    }

    pointsWithinRegion
  }

  private fun generateArea(points: List<Point>): Area {
    var top = MAX_VALUE
    var bottom = MIN_VALUE
    var left = MAX_VALUE
    var right = MIN_VALUE

    points.forEach { (x, y) ->
      top = minOf(y, top)
      bottom = maxOf(y, bottom)
      left = minOf(x, left)
      right = maxOf(x, right)
    }

    return Area(top, bottom, left, right)
  }

  private fun forPoints(area: Area, handler: (x: Int, y: Int) -> Unit) {
    (area.top..area.bottom).forEach { y ->
      (area.left..area.right).forEach { x ->
        handler(x, y)
      }
    }
  }

  private fun point(line: String) = line.split(", ").let { (x, y) -> Point(x.toInt(), y.toInt()) }

  private data class Point(val x: Int, val y: Int)

  private data class Area(val top: Int, val bottom: Int, val left: Int, val right: Int)

  companion object {
    private const val DISTANCE = 10_000
  }
}
