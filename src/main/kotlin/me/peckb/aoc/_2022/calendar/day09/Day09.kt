package me.peckb.aoc._2022.calendar.day09

import me.peckb.aoc._2022.calendar.day09.Day09.Direction.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalArgumentException
import kotlin.math.abs

class Day09 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::movement) { input ->
    findUniqueTailLocations(input, children = 1)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::movement) { input ->
    findUniqueTailLocations(input, children = 9)
  }

  private fun findUniqueTailLocations(input: Sequence<Movement>, children: Int): Int {
    val rope = (0..children).map { Location(0, 0) }.toMutableList()
    val lastTailVisitedLocation = mutableSetOf<Location>().also { it.add(rope.last()) }

    input.forEach { movement ->
      repeat(movement.steps) {
        rope[0] = rope[0].move(movement.direction) // move the head

        var parentIndex = 0
        var shouldCheckChild = true
        while (shouldCheckChild) { // move all the children!
          val parent = rope[parentIndex]
          val child = rope[parentIndex + 1]

          if (abs(parent.x - child.x) > 1 || abs(parent.y - child.y) > 1) {
            rope[parentIndex + 1] = Location(
              x = child.x + parent.x.compareTo(child.x),
              y = child.y + parent.y.compareTo(child.y),
            ).also {
              if (parentIndex + 1 == children) {
                lastTailVisitedLocation.add(it)
                shouldCheckChild = false
              }
            }
          } else {
            shouldCheckChild = false
          }

          parentIndex++
        }
      }
    }

    return lastTailVisitedLocation.size
  }

  private fun movement(line: String) = line.split(" ").let { (d, s) ->
    val direction = when (d) {
      "R" -> RIGHT
      "L" -> LEFT
      "U" -> UP
      "D" -> DOWN
      else -> throw IllegalArgumentException("Invalid Direction $d")
    }
    val steps = s.toInt()

    Movement(direction, steps)
  }

  data class Movement(val direction: Direction, val steps: Int)

  enum class Direction { LEFT, RIGHT, DOWN, UP }

  data class Location(val x: Int, val y: Int) {
    fun move(direction: Direction): Location {
      return when (direction) {
        LEFT -> Location(x - 1, y)
        RIGHT -> Location(x + 1, y)
        DOWN -> Location(x, y + 1)
        UP -> Location(x, y - 1)
      }
    }
  }
}
