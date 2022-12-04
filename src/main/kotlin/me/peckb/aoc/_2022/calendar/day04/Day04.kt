package me.peckb.aoc._2022.calendar.day04

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day04 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::ranges) { input ->
    input.count { (rangeA, rangeB) ->
      rangeA.all { rangeB.contains(it) } || rangeB.all { rangeA.contains(it) }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::ranges) { input ->
    input.count { (rangeA, rangeB) ->
      rangeA.any { rangeB.contains(it) } || rangeB.any { rangeA.contains(it) }
    }
  }

  private fun ranges(line: String) = line
    .split(",")
    .map { pairOfRanges -> pairOfRanges.split("-") }
    .map { (firstElfRange, secondElfRange) -> firstElfRange.toInt()..secondElfRange.toInt() }
}
