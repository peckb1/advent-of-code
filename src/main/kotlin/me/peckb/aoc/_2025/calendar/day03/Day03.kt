package me.peckb.aoc._2025.calendar.day03

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day03 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::battery) { batteryArrays ->
    batteryArrays.sumOf { batteryArray -> maxJoltage(batteryArray, 2) }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::battery) { batteryArrays ->
    batteryArrays.sumOf { batteryArray -> maxJoltage(batteryArray, 12) }
  }

  private fun maxJoltage(batteryArray: BatteryArray, numToEnable: Int) : Long {
    val subListIndices = batteryArray.batteries.drop(numToEnable - 1).indices
    val indexOfLargestStart = subListIndices.maxBy { i -> batteryArray.batteries[i] }
    val largestStartValue = batteryArray.batteries[indexOfLargestStart]

    // we can bottom out if our next recurse would enable zero batteries
    if (numToEnable == 1) { return largestStartValue }

    // if we have more batteries to flip - find the indices for those batteries and send down the new arrays
    val possibleStartIndices = subListIndices.filter { i -> batteryArray.batteries[i] == largestStartValue }
    val maxSubJoltage = possibleStartIndices.maxOf { i -> maxJoltage(batteryArray.dropAfter(i), numToEnable - 1) }

    // then add it to our value and return!
    return "$largestStartValue$maxSubJoltage".toLong()
  }

  private fun battery(line: String) = line.map { it.digitToInt().toLong() }.let(::BatteryArray)
}

data class BatteryArray(val batteries: List<Long>) {
  fun dropAfter(index: Int) : BatteryArray {
    return BatteryArray(batteries.drop(index + 1))
  }
}
