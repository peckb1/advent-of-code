package me.peckb.aoc._2021.calendar.day19

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day19 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::day19) { input ->
    -1
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day19) { input ->
    -1
  }

  fun day19(line: String) = -4
}
