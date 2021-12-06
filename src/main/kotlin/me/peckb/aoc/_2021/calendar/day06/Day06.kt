package me.peckb.aoc._2021.calendar.day06

import me.peckb.aoc._2021.generators.InputGenerator
import javax.inject.Inject

class Day06 @Inject constructor(private val generatorFactory: InputGenerator.InputGeneratorFactory) {
  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::day06) { input ->
    -1
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day06) { input ->
    -1
  }

  private fun day06(line: String) = 4
}
