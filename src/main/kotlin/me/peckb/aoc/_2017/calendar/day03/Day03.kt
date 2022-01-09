package me.peckb.aoc._2017.calendar.day03

import me.peckb.aoc._2017.calendar.day03.Day03.Direction.DOWN
import me.peckb.aoc._2017.calendar.day03.Day03.Direction.LEFT
import me.peckb.aoc._2017.calendar.day03.Day03.Direction.RIGHT
import me.peckb.aoc._2017.calendar.day03.Day03.Direction.UP
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.abs

class Day03 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    var lowerRight = 1

    var ringsToValue = 0
    val destination = input.toInt()
    while (lowerRight <= destination) {
      ringsToValue++
      lowerRight += (ringsToValue * 8)
    }
    val cornerDelta = 2 * ringsToValue

    val lowerLeft = lowerRight - cornerDelta
    val upperLeft = lowerLeft - cornerDelta
    val upperRight = upperLeft - cornerDelta

    val x: Int
    val y: Int

    when {
      destination >= lowerLeft -> { // on the bottom edge
        y = ringsToValue
        x = abs(ringsToValue - (lowerRight - destination))
      }
      destination >= upperLeft -> { // on the left edge
        x = ringsToValue
        y = abs(ringsToValue - (lowerLeft - destination))
      }
      destination >= upperRight -> { // on the top edge
        y = ringsToValue
        x = abs(ringsToValue - (upperLeft - destination))
      }
      else -> { // on the right edge
        x = ringsToValue
        y = abs(ringsToValue - (upperRight - destination))
      }
    }

    x + y
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    // due to doubling every turn, our max array size doesn't need to be that big
    val memory = Array(11) { Array(11) { 0 } }
    val destination = input.toInt()
    var number = 1
    var x = memory.size / 2
    var y = memory.size / 2
    memory[y][x] = 1

    var direction = RIGHT

    fun calculateNumber() = (y - 1..y + 1).sumOf { yy -> (x - 1..x + 1).sumOf { xx -> memory[yy][xx] } }

    while (number < destination) {
      when (direction) {
        RIGHT -> {
          x++
          number = calculateNumber().also { memory[y][x] = it }
          if (memory[y - 1][x] == 0) direction = UP
        }
        UP -> {
          y--
          number = calculateNumber().also { memory[y][x] = it }
          if (memory[y][x - 1] == 0) direction = LEFT
        }
        LEFT -> {
          x--
          number = calculateNumber().also { memory[y][x] = it }
          if (memory[y + 1][x] == 0) direction = DOWN
        }
        DOWN -> {
          y++
          number = calculateNumber().also { memory[y][x] = it }
          if (memory[y][x + 1] == 0) direction = RIGHT
        }
      }
    }

    number
  }

  enum class Direction {
    RIGHT,
    UP,
    LEFT,
    DOWN
  }
}
