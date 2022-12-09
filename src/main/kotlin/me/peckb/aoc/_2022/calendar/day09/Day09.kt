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
        val head = rope[0]
        rope[0] = when (movement.direction) {
          LEFT -> Location(head.x - 1, head.y)
          RIGHT -> Location(head.x + 1, head.y)
          DOWN -> Location(head.x, head.y + 1)
          UP -> Location(head.x, head.y - 1)
        }

        var parentIndex = 0
        var shouldCheckChild = true
        while (shouldCheckChild && parentIndex < rope.size - 1) {
          val parent = rope[parentIndex]
          val child = rope[parentIndex + 1]

          if (abs(parent.x - child.x) > 1 || abs(parent.y - child.y) > 1) {
            rope[parentIndex + 1] = Location(
              x = child.x + parent.x.compareTo(child.x),
              y = child.y + parent.y.compareTo(child.y),
            ).also {
              if (parentIndex + 1 == children) lastTailVisitedLocation.add(it)
            }
            parentIndex++
          } else {
            shouldCheckChild = false
          }
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

  data class Location(val x: Int, val y: Int)
}
