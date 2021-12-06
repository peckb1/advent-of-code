package me.peckb.aoc._2021.calendar.day07

import me.peckb.aoc._2021.generators.InputGenerator
import javax.inject.Inject

class Day07 @Inject constructor(private val generatorFactory: InputGenerator.InputGeneratorFactory) {
  companion object {

  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::day07) { input ->

  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day07) { input ->

  }

  private fun day07(line: String) = line
}
