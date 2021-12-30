package me.peckb.aoc._2015.calendar.day11

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day11 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::day11) { input ->
    -1
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::day11) { input ->
    -1
  }

  private fun day11(line: String) = 4
}
