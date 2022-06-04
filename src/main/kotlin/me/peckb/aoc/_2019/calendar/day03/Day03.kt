package me.peckb.aoc._2019.calendar.day03

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.abs

class Day03 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::directions) { input ->
    val coveredPaths = getConnectionPoints(input)

    coveredPaths
      .map { abs(it.key.x) + abs(it.key.y) }
      .minOrNull()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::directions) { input ->
    val coveredPaths = getConnectionPoints(input)

    coveredPaths
      .map { it.value.sumOf { owner -> owner.stepsTaken } }
      .minOrNull()
  }

  private fun directions(line: String) = line.split(",")

  data class Point(val x: Int, val y: Int)

  data class Owner(val id: Int) {
    var stepsTaken: Int = -1
      private set

    fun withSteps(stepsTaken: Int) = apply { this.stepsTaken = stepsTaken }
  }

  private fun getConnectionPoints(directionsList: Sequence<List<String>>): Map<Point, Set<Owner>> {
    val coveredPaths = mutableMapOf<Point, Set<Owner>>().apply {}

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

          val point = when (direction) {
            'R' -> Point(x + move, y)
            'L' -> Point(x - move, y)
            'U' -> Point(x, y - move)
            'D' -> Point(x, y + move)
            else -> Point(x, y)
          }

          val ownerSet = setOf(Owner(index).withSteps(stepsTaken))
          coveredPaths.merge(point, ownerSet) { a, b -> a + b }
        }
        when (direction) {
          'R' -> x += length
          'L' -> x -= length
          'U' -> y -= length
          'D' -> y += length
        }
      }
    }

    return coveredPaths
      .filter { it.value.size == 2 }
      .filterNot { it.key.x == 0 && it.key.y == 0 }
  }
}
