package me.peckb.aoc._2019.calendar.day03

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day03 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) =
      generatorFactory.forFile(filename).readAs(::day03) { input ->
    -1
  }

  fun partTwo(filename: String) =
      generatorFactory.forFile(filename).readAs(::day03) { input ->
    -1
  }

  private fun day03(line: String) = 4
}
