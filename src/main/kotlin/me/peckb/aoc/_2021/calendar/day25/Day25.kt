package me.peckb.aoc._2021.calendar.day25

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day25 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    -1
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    -1
  }
}
