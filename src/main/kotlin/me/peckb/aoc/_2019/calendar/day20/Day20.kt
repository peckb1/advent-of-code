package me.peckb.aoc._2019.calendar.day20

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day20 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) =
      generatorFactory.forFile(filename).readAs(::day20) { input ->
    -1
  }

  fun partTwo(filename: String) =
      generatorFactory.forFile(filename).readAs(::day20) { input ->
    -1
  }

  private fun day20(line: String) = 4
}
