package me.peckb.aoc._2018.calendar.day06

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.Int.Companion.MAX_VALUE
import kotlin.Int.Companion.MIN_VALUE
import kotlin.math.abs

class Day06 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::point) { input ->
    val points = input.toList()

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

    val pointsToIgnore = points.filter { (x, y) -> x == left || x == right || y == top || y == bottom }.toSet()

    val counts = mutableMapOf<Point, Int>()
    (top .. bottom).forEach { y ->
      (left..right).forEach { x ->
        val distances: List<Pair<Point, Int>> = points.map { point ->
          point to abs(x - point.x) + abs(y - point.y)
        }.sortedBy { it.second }
        val (closest, secondClosest) = distances.take(2)
        if (closest.second < secondClosest.second) counts.merge(closest.first, 1, Int::plus)
      }
    }

    counts.filterNot { pointsToIgnore.contains(it.key) }.maxOf { it.value }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::point) { input ->
    val points = input.toList()

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

    var pointsWithinRegion = 0

    (top .. bottom).forEach { y ->
      (left..right).forEach { x ->
        val distanceSum = points.sumOf { point -> abs(x - point.x) + abs(y - point.y) }
        if (distanceSum < DISTANCE) pointsWithinRegion++
      }
    }

    pointsWithinRegion
  }

  private fun point(line: String) = line.split(", ").let { (x, y) -> Point(x.toInt(), y.toInt()) }

  data class Point(val x: Int, val y: Int)

  companion object {
    const val DISTANCE = 10_000
  }
}
