package me.peckb.aoc._2021.calendar.day08

import me.peckb.aoc._2021.generators.InputGenerator
import javax.inject.Inject

class Day08 @Inject constructor(private val generatorFactory: InputGenerator.InputGeneratorFactory) {
  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::day08) { input ->
    -1
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day08) { input ->
    -1
  }

  private fun day08(line: String) = 4
}
