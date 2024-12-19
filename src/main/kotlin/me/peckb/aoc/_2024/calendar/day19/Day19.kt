package me.peckb.aoc._2024.calendar.day19

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day19 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val data = input.toList()
    val availableTowelTypes = data.first().split(", ")

    data.drop(2).count { pattern -> isPossible(pattern, availableTowelTypes) }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val data = input.toList()
    val availableTowelTypes = data.first().split(", ")

    data.drop(2).sumOf { pattern -> isPossibleCount(pattern, availableTowelTypes) }
  }

  private fun isPossible(pattern: String, availableTowelTypes: List<String>): Boolean {
    if (pattern.isEmpty()) return true
    if (found[pattern] == false) return false

    return availableTowelTypes.any { towel ->
      pattern.startsWith(towel) && isPossible(pattern.substring(towel.length), availableTowelTypes)
    }.also { found[pattern] = it }
  }

  private fun isPossibleCount(pattern: String, availableTowelTypes: List<String>): Long {
    if (pattern.isEmpty()) return 1
    foundCount[pattern]?.let { return it }

    return availableTowelTypes.sumOf { towel ->
      if (!pattern.startsWith(towel)) { 0L } else {
        isPossibleCount(pattern.substring(towel.length), availableTowelTypes)
      }
    }.also { foundCount[pattern] = it }
  }

  companion object {
    val found = mutableMapOf<String, Boolean>()
    val foundCount = mutableMapOf<String, Long>()
  }
}
