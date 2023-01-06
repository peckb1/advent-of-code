package me.peckb.aoc._2020.calendar.day01

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day01 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::expense) { input ->
    val sortedExpenses = input.sorted().toList()
    val size = sortedExpenses.size

    var start = 0
    var end = 1

    while(sortedExpenses[start] + sortedExpenses[end] != 2020) {
      end++
      if (end == size || sortedExpenses[start] + sortedExpenses[end] > 2020) {
        start++
        end = start + 1
      }
    }

    sortedExpenses[start] * sortedExpenses[end]
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::expense) { input ->
    val sortedExpenses = input.sorted().toList()
    val size = sortedExpenses.size

    var start = 0
    var mid = 1
    var end = 2

    var movingMid = false
    fun endCheck() {
      mid = start + 1
      if (end == size) {
        start++
        end = mid + 1
      } else {
        movingMid = true
      }
    }
    fun midCheck() {
      if (mid == end) {
        movingMid = false
        mid = start + 1
      }
    }

    while (sortedExpenses[start] + sortedExpenses[mid] + sortedExpenses[end] != 2020) {
      if (movingMid) {
        mid++.also { midCheck() }
      } else {
        end++.also { endCheck() }
      }
    }

    sortedExpenses[start] * sortedExpenses[mid] * sortedExpenses[end]
  }

  private fun expense(line: String) = line.toInt()
}
