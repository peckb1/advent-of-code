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
    if (numToEnable == 0) { return 0 }

    val subListIndices = batteryArray.batteries.drop(numToEnable - 1).indices

    val indexOfLargestStart = subListIndices.maxBy { i -> batteryArray.batteries[i] }
    val largestStartValue = batteryArray.batteries[indexOfLargestStart]

    val possibleStartIndices = subListIndices.filter { i -> batteryArray.batteries[i] == largestStartValue }

    val subJoltages = possibleStartIndices.maxOf { i -> maxJoltage(batteryArray.dropAfter(i), numToEnable - 1) }

    return if (subJoltages > 0) {
      "$largestStartValue$subJoltages".toLong()
    } else {
      largestStartValue.toLong()
    }
  }

  private fun battery(line: String) = line.map { it.digitToInt() }.let(::BatteryArray)
}

data class BatteryArray(val batteries: List<Int>) {
  fun dropAfter(index: Int) : BatteryArray {
    return BatteryArray(batteries.drop(index + 1))
  }
}
