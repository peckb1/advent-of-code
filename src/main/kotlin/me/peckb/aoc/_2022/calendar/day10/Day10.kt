package me.peckb.aoc._2022.calendar.day10

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day10 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) =
      generatorFactory.forFile(filename).readAs(::day10) { input ->
    -1
  }

  fun partTwo(filename: String) =
      generatorFactory.forFile(filename).readAs(::day10) { input ->
    -1
  }

  private fun day10(line: String) = 4
}
