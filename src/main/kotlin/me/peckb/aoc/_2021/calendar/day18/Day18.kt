package me.peckb.aoc._2021.calendar.day18

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day18 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::day18) { input ->
    -1
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day18) { input ->
    -1
  }

  private fun day18(line: String) = 4
}
