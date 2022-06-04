package me.peckb.aoc._2019.calendar.day03

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.abs

class Day03 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::directions) { input ->
    val directionsList = input.toList()

    val coveredPaths = mutableMapOf<Point, Set<Int>>()

    directionsList.forEachIndexed { index, directions ->
      var x = 0
      var y = 0

      directions.forEach {
        val direction = it[0]
        val length = it.substring(1).toInt()
        repeat(length) { zeroIndexedMove ->
          val move = zeroIndexedMove + 1
          when (direction) {
            'R' -> coveredPaths.merge(Point(x + move, y), setOf(index)) { a, b -> a + b }
            'L' -> coveredPaths.merge(Point(x - move, y), setOf(index)) { a, b -> a + b }
            'U' -> coveredPaths.merge(Point(x, y - move), setOf(index)) { a, b -> a + b }
            'D' -> coveredPaths.merge(Point(x, y + move), setOf(index)) { a, b -> a + b }
          }
        }
        when (direction) {
          'R' -> x += length
          'L' -> x -= length
          'U' -> y -= length
          'D' -> y += length
        }
      }
    }

    val closestConnectionPoint = coveredPaths
      .filter { it.value.size == 2 }
      .filterNot { it.key.x == 0 && it.key.y == 0 }
      .map { abs(it.key.x) + abs(it.key.y) }
      .minOrNull()

    closestConnectionPoint
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::directions) { input ->
    val directionsList = input.toList()

    val coveredPaths = mutableMapOf<Point, Set<Owner>>()

    directionsList.forEachIndexed { index, directions ->
      var x = 0
      var y = 0
      var stepsTaken = 0

      directions.forEach {
        val direction = it[0]
        val length = it.substring(1).toInt()
        repeat(length) { zeroIndexedMove ->
          val move = zeroIndexedMove + 1
          stepsTaken += 1
          val owner = Owner(index).withSteps(stepsTaken)
          when (direction) {
            'R' -> coveredPaths.merge(Point(x + move, y), setOf(owner)) { a, b -> a + b }
            'L' -> coveredPaths.merge(Point(x - move, y), setOf(owner)) { a, b -> a + b }
            'U' -> coveredPaths.merge(Point(x, y - move), setOf(owner)) { a, b -> a + b }
            'D' -> coveredPaths.merge(Point(x, y + move), setOf(owner)) { a, b -> a + b }
          }
        }
        when (direction) {
          'R' -> x += length
          'L' -> x -= length
          'U' -> y -= length
          'D' -> y += length
        }
      }
    }

    val firstConnectionPoint = coveredPaths
      .filter { it.value.size == 2 }
      .filterNot { it.key.x == 0 && it.key.y == 0 }
      .map { it.value.sumOf { owner -> owner.stepsTaken } }
      .minOrNull()

    firstConnectionPoint
  }

  private fun directions(line: String) = line.split(",")

  data class Point(val x: Int, val y: Int)

  data class Owner(val id: Int) {
    var stepsTaken: Int = -1
      private set

    fun withSteps(stepsTaken: Int) = apply { this.stepsTaken = stepsTaken }
  }
}
