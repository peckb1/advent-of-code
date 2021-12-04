package me.peckb.aoc._2021.calendar.day04

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject


class Day04 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::day04) {
    -1
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day04) {
    -1
  }

  private fun day04(line: String) = 4
}
