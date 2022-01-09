package me.peckb.aoc._2017.calendar.day02

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.Int.Companion.MAX_VALUE
import kotlin.Int.Companion.MIN_VALUE
import kotlin.math.max
import kotlin.math.min

class Day02 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    input.sumOf { line ->
      var max = MIN_VALUE
      var min = MAX_VALUE
      line.split(WHITESPACE_REGEX).map { it.toInt() }.forEach {
        max = max(it, max)
        min = min(it, min)
      }
      max - min
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    input.sumOf { line ->
      val numbers = line.split(WHITESPACE_REGEX).map { it.toInt() }
      var value = 0

      numbers.indices.forEach { aIndex ->
        (aIndex + 1 until numbers.size).forEach { bIndex ->
          val a = numbers[aIndex]
          val b = numbers[bIndex]

          if (a % b == 0) {
            value = a / b
          } else if (b % a == 0) {
            value = b / a
          }
        }
      }

      value
    }
  }

  companion object {
    private val WHITESPACE_REGEX = "\\s+".toRegex()
  }
}
