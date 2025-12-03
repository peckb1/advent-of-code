package me.peckb.aoc._2025.calendar.day03

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day03 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::batteryArray) { batteryArrays ->
    batteryArrays.sumOf { batteryArray -> maxJoltage(batteryArray, numToEnable = 2) }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::batteryArray) { batteryArrays ->
    batteryArrays.sumOf { batteryArray -> maxJoltage(batteryArray, numToEnable = 12) }
  }

  private fun maxJoltage(
    batteryArray: List<Long>,
    startIndex: Int = 0,
    numToEnable: Int,
  ) : Long {
    val subListIndices = startIndex .. ((batteryArray.size - 1) - (numToEnable - 1))

    val indexOfLargest = subListIndices.maxBy { i -> batteryArray[i] }
    val largestStartValue = batteryArray[indexOfLargest]

    // we can bottom out if our next recurse would enable zero batteries
    if (numToEnable == 1) { return largestStartValue }

    // if we have more batteries to flip - find the indices for those batteries and send down the new arrays
    val possibleStartIndices = subListIndices.filter { i -> batteryArray[i] == largestStartValue }
    val maxSubJoltage = possibleStartIndices.maxOf { i -> maxJoltage(batteryArray, startIndex = i + 1, numToEnable = numToEnable - 1) }

    // then add it to our value and return!
    return "$largestStartValue$maxSubJoltage".toLong()
  }

  private fun batteryArray(line: String) = line.map { it.digitToInt().toLong() }
}
