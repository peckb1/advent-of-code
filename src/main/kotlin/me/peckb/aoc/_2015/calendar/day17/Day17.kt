package me.peckb.aoc._2015.calendar.day17

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day17 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::int) { input ->
    val sortedData = input.sorted().toList()
    subSetSum(sortedData, SUM).size
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::int) { input ->
    val sortedData = input.sorted().toList()
    subSetSum(sortedData, SUM)
      .groupBy { it.size }
      .minByOrNull { it.key }
      ?.value
      ?.size
  }

  private fun int(line: String) = line.toInt()

  private fun subSetSum(containers: List<Int>, sum: Int): List<MutableList<Int>> {
    if (sum < 0) return emptyList()
    val sums = mutableListOf<MutableList<Int>>()
    containers.mapIndexed { index, container ->
      if (container == sum) {
        sums.add(mutableListOf(container))
      }
      subSetSum(containers.subList(index + 1, containers.size), sum - container).forEach {
        if (container + it.sum() == sum) {
          sums.add(mutableListOf(container).apply { addAll(it) })
        }
      }
    }
    return sums
  }

  companion object {
    const val SUM = 150
  }
}
