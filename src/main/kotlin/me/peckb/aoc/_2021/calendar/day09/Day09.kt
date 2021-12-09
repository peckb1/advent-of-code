package me.peckb.aoc._2021.calendar.day09

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day09 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {

  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::day09) { input ->
    -1
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day09) { input ->
    -1
  }

  fun day09(line: String) = 4

}
