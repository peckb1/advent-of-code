package me.peckb.aoc._2015.calendar.day09

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day09 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::day09) { input ->
    -1
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::day09) { input ->
    -1
  }

  private fun day09(line: String) = 4
}
