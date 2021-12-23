package me.peckb.aoc._2021.calendar.day23

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day23 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::day23) { input ->
    -1
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day23) { input ->
    -1
  }

  private fun day23(line: String) = 4
}
