package me.peckb.aoc._2017.calendar.day21

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day21 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory
) {
  fun partOne(filename: String) =
      generatorFactory.forFile(filename).readAs(::day21) { input ->
    -1
  }

  fun partTwo(filename: String) =
      generatorFactory.forFile(filename).readAs(::day21) { input ->
    -1
  }

  private fun day21(line: String) = 4
}
