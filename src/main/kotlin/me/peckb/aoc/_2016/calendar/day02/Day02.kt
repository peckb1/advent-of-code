package me.peckb.aoc._2016.calendar.day02

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max
import kotlin.math.min

class Day02 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val keypadLocation = Location(1, 1)
    val min = 0
    val max = KEY_PAD.size - 1

    val code = input.map { line ->
      line.forEach { direction ->
        when (direction) {
          'U' -> keypadLocation.y = max(min, keypadLocation.y - 1)
          'L' -> keypadLocation.x = max(min, keypadLocation.x - 1)
          'D' -> keypadLocation.y = min(max, keypadLocation.y + 1)
          'R' -> keypadLocation.x = min(max, keypadLocation.x + 1)
        }
      }
      KEY_PAD[keypadLocation.y][keypadLocation.x]
    }

    code.joinToString("")
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val keypadLocation = Location(2, 0)
    val min = 0
    val max = FANCY_KEY_PAD.size - 1

    val code = input.map { line ->
      line.forEach { direction ->
        var newY = keypadLocation.y
        var newX = keypadLocation.x
        when (direction) {
          'U' -> newY = max(min, keypadLocation.y - 1)
          'L' -> newX = max(min, keypadLocation.x - 1)
          'D' -> newY = min(max, keypadLocation.y + 1)
          'R' -> newX = min(max, keypadLocation.x + 1)
        }
        if ((FANCY_KEY_PAD[newY][newX]) != ' ') {
          keypadLocation.y = newY
          keypadLocation.x = newX
        }
      }
      FANCY_KEY_PAD[keypadLocation.y][keypadLocation.x]
    }

    code.joinToString("")
  }

  data class Location(var y: Int, var x: Int)

  companion object {
    val KEY_PAD = listOf(
      listOf(1, 2, 3),
      listOf(4, 5, 6),
      listOf(7, 8, 9)
    )
    val FANCY_KEY_PAD = listOf(
      listOf(' ', ' ', '1', ' ', ' '),
      listOf(' ', '2', '3', '4', ' '),
      listOf('5', '6', '7', '8', '9'),
      listOf(' ', 'A', 'B', 'C', ' '),
      listOf(' ', ' ', 'D', ' ', ' '),
    )
  }
}
