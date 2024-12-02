package me.peckb.aoc._2024.calendar.day02


import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.sign

class Day02 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::report) { reports ->
    reports.count { it.isSafe }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::report) { reports ->
    reports.count { it.isActuallySafe() }
  }

  private fun report(line: String) = line
    .split(" ")
    .map { it.toInt() }
    .let(::Report)
}
