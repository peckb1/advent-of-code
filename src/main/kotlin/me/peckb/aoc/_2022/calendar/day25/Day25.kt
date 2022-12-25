package me.peckb.aoc._2022.calendar.day25

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day25 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    input.map(Snafu::fromString)
      .reduce(Snafu::plus)
      .toString()
  }
}
