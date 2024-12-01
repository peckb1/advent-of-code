package me.peckb.aoc._2020.calendar.day09

import me.peckb.aoc.generators.CombinationsGenerator.findCombinations
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.min
import kotlin.math.max

class Day09 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::day09) { input ->
    invalidNumber(input.toList())
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::day09) { input ->
    val numbers = input.toList()
    val invalidNumber = invalidNumber(numbers)

    var minIndex = 0
    var maxIndex = 1

    var sum = numbers[minIndex] + numbers[maxIndex]
    while (sum != invalidNumber) {
      if (sum < invalidNumber) {
        maxIndex += 1
        sum += numbers[maxIndex]
      }
      if (sum > invalidNumber) {
        sum -= numbers[minIndex]
        minIndex += 1
      }
    }

    var min = Long.MAX_VALUE
    var max = Long.MIN_VALUE
    (minIndex .. maxIndex).forEach { index ->
      min = min(min, numbers[index])
      max = max(max, numbers[index])
    }

    min + max
  }

  private fun day09(line: String) = line.toLong()

  private fun invalidNumber(input: List<Long>): Long {
    val preambleLength = 25

    val window = input.windowed(preambleLength + 1).first { window ->
      val number = window.last()
      val items = window.take(preambleLength)
      val options = findCombinations(items.toTypedArray(), 2)

      options.none { it.sum() == number }
    }

    return window.last()
  }
}
