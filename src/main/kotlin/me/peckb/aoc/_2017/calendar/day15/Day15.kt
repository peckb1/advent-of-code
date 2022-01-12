package me.peckb.aoc._2017.calendar.day15

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day15 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory
) {
  fun partOne(filename: String) =
      generatorFactory.forFile(filename).readAs(::day15) { input ->
    -1
  }

  fun partTwo(filename: String) =
      generatorFactory.forFile(filename).readAs(::day15) { input ->
    -1
  }

  private fun day15(line: String) = 4
}
