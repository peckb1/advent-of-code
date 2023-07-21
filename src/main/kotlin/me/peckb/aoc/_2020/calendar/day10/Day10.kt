package me.peckb.aoc._2020.calendar.day10

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.min

class Day10 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::joltage) { input ->
    val joltDifferences = mutableMapOf<Int, Int>()
    val sortedJoltages = input.toList().sorted()

    sortedJoltages.windowed(2).forEach { (a, b) ->
      val diff = b - a
      joltDifferences[diff] = (joltDifferences[diff] ?: 0) + 1
    }

    // handle 0 -> first adapter
    val firstDiff = sortedJoltages.first()
    joltDifferences[firstDiff] = (joltDifferences[firstDiff] ?: 0) + 1
    // handler last adapter + 3
    joltDifferences[3] = (joltDifferences[3] ?: 0) + 1

    joltDifferences[1]!! * joltDifferences[3]!!
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::joltage) { input ->
    val sortedJoltages = input.sorted().toMutableList().apply {
      add(0, 0)
      add(last() + 3)
    }
    val joltagesMap = sortedJoltages.associateWith { 0L }.toMutableMap()
    joltagesMap[0] = 1

    var minIndex = 0
    var maxIndex = 1

    while(maxIndex < sortedJoltages.size) {
      var tooFar = false
      while (minIndex >= 0 && !tooFar) {
        val maxJoltage = sortedJoltages[maxIndex]
        val minJoltage = sortedJoltages[minIndex]
        val canReachMe = maxJoltage - minJoltage <= 3
        if (canReachMe) {
          joltagesMap[maxJoltage] = joltagesMap[maxJoltage]!! + joltagesMap[minJoltage]!!
          minIndex--
        } else {
          tooFar = true
        }
      }
      maxIndex += 1
      minIndex = maxIndex - 1
    }

    joltagesMap[sortedJoltages.last()]
  }

  private fun joltage(line: String) = line.toInt()
}
