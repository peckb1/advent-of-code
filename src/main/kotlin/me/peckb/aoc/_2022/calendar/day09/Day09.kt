package me.peckb.aoc._2022.calendar.day09

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalArgumentException
import kotlin.math.abs

class Day09 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::movement) { input ->
    var headLocation = Location(0, 0)
    var tailLocation = Location(0, 0)

    val visitedLocation = mutableListOf<Location>().also { it.add(tailLocation) }

    input.forEach { movement ->
      repeat(movement.steps) {
        when (movement.direction) {
          Direction.LEFT -> {
            headLocation = Location(headLocation.x - 1, headLocation.y)
          }

          Direction.RIGHT -> {
            headLocation = Location(headLocation.x + 1, headLocation.y)
          }

          Direction.DOWN -> {
            headLocation = Location(headLocation.x, headLocation.y + 1)
          }

          Direction.UP -> {
            headLocation = Location(headLocation.x, headLocation.y - 1)
          }
        }
        if (abs(headLocation.x - tailLocation.x) > 1 || abs(headLocation.y - tailLocation.y) > 1) {
          tailLocation = Location(tailLocation.x + headLocation.x.compareTo(tailLocation.x), tailLocation.y + headLocation.y.compareTo(tailLocation.y))
          visitedLocation.add(tailLocation)
        }
      }
    }

    visitedLocation.toSet().size
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::movement) { input ->
    var headLocation = Location(0, 0)
    var c1 = Location(0, 0)
    var c2 = Location(0, 0)
    var c3 = Location(0, 0)
    var c4 = Location(0, 0)
    var c5 = Location(0, 0)
    var c6 = Location(0, 0)
    var c7 = Location(0, 0)
    var c8 = Location(0, 0)
    var c9 = Location(0, 0)

    val visitedLocation = mutableListOf<Location>().also { it.add(c9) }

    input.forEach { movement ->
      repeat(movement.steps) {
        when (movement.direction) {
          Direction.LEFT -> {
            headLocation = Location(headLocation.x - 1, headLocation.y)
          }

          Direction.RIGHT -> {
            headLocation = Location(headLocation.x + 1, headLocation.y)
          }

          Direction.DOWN -> {
            headLocation = Location(headLocation.x, headLocation.y + 1)
          }

          Direction.UP -> {
            headLocation = Location(headLocation.x, headLocation.y - 1)
          }
        }
        // c1
        if (abs(headLocation.x - c1.x) > 1 || abs(headLocation.y - c1.y) > 1) {
          c1 = Location(c1.x + headLocation.x.compareTo(c1.x), c1.y + headLocation.y.compareTo(c1.y))
        }
        // c2
        if (abs(c1.x - c2.x) > 1 || abs(c1.y - c2.y) > 1) {
          c2 = Location(c2.x + c1.x.compareTo(c2.x), c2.y + c1.y.compareTo(c2.y))
        }
        // c3
        if (abs(c2.x - c3.x) > 1 || abs(c2.y - c3.y) > 1) {
          c3 = Location(c3.x + c2.x.compareTo(c3.x), c3.y + c2.y.compareTo(c3.y))
        }
        // c4
        if (abs(c3.x - c4.x) > 1 || abs(c3.y - c4.y) > 1) {
          c4 = Location(c4.x + c3.x.compareTo(c4.x), c4.y + c3.y.compareTo(c4.y))
        }
        // c5
        if (abs(c4.x - c5.x) > 1 || abs(c4.y - c5.y) > 1) {
          c5 = Location(c5.x + c4.x.compareTo(c5.x), c5.y + c4.y.compareTo(c5.y))
        }
        // c6
        if (abs(c5.x - c6.x) > 1 || abs(c5.y - c6.y) > 1) {
          c6 = Location(c6.x + c5.x.compareTo(c6.x), c6.y + c5.y.compareTo(c6.y))
        }
        // c7
        if (abs(c6.x - c7.x) > 1 || abs(c6.y - c7.y) > 1) {
          c7 = Location(c7.x + c6.x.compareTo(c7.x), c7.y + c6.y.compareTo(c7.y))
        }
        // c8
        if (abs(c7.x - c8.x) > 1 || abs(c7.y - c8.y) > 1) {
          c8 = Location(c8.x + c7.x.compareTo(c8.x), c8.y + c7.y.compareTo(c8.y))
        }
        // c9
        if (abs(c8.x - c9.x) > 1 || abs(c8.y - c9.y) > 1) {
          c9 = Location(c9.x + c8.x.compareTo(c9.x), c9.y + c8.y.compareTo(c9.y))
          visitedLocation.add(c9)
        }
      }
    }

    visitedLocation.toSet().size
  }

  private fun movement(line: String) = line.split(" ").let { (d, s) ->
    val direction = when (d) {
      "R" -> Direction.RIGHT
      "L" -> Direction.LEFT
      "U" -> Direction.UP
      "D" -> Direction.DOWN
      else -> throw IllegalArgumentException("Invalid Direction $d")
    }
    val steps = s.toInt()

    Movement(direction, steps)
  }

  data class Movement(val direction: Direction, val steps: Int)

  enum class Direction { LEFT, RIGHT, DOWN, UP }

  data class Location(val x: Int, val y: Int)
}
