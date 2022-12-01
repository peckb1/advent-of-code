package me.peckb.aoc._2022.calendar.day01

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day01 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::calorieCount) { input ->
    findCalories(input).maxOf { it }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::calorieCount) { input ->
    findCalories(input).apply { sortDescending() }.take(3).sum()
  }

  private fun findCalories(calories: Sequence<Int?>): MutableList<Int> {
    return calories.fold(mutableListOf(NEW_ELF)) { elves, nextCalorie ->
      elves.apply {
        val lastElf = size - 1
        nextCalorie?.let { this[lastElf] = this[lastElf] + it } ?: add(NEW_ELF)
      }
    }
  }

  private fun calorieCount(line: String): Int? = line.toIntOrNull()

  companion object {
    private const val NEW_ELF = 0
  }
}
