package me.peckb.aoc._2021.calendar.day21

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day21 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {

  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::day21) { input ->
    -1
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day21) { input ->
    -1
  }

  private fun day21(line: String) = 4
}
