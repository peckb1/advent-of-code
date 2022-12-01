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

  private fun findCalories(calories: Sequence<Int?>) = mutableListOf<Int>()
    .apply { add(0) }
    .also { elfCalories ->
      calories.forEach { calorieCount ->
        val lastIndex = elfCalories.size - 1

        calorieCount
          ?.let { elfCalories[lastIndex] = elfCalories[lastIndex] + it }
          ?: run { elfCalories.add(0) }
      }
    }

  private fun calorieCount(line: String): Int? = line.toIntOrNull()
}
