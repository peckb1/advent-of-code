package me.peckb.aoc._2024.calendar.day03

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day03 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { lines ->
    lines.sumOf { line ->
      val matches = "mul\\(\\d+,\\d+\\)".toRegex().findAll(line)

      matches.sumOf { match -> match.value.getMultiplicationResult() }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { lines ->
    var enabled = true

    lines.sumOf { line ->
      val mulMatches = "mul\\(\\d+,\\d+\\)".toRegex().findAll(line)
      val doMatches = "do\\(\\)".toRegex().findAll(line)
      val dontMatches = "don\'t\\(\\)".toRegex().findAll(line)

      val sortedMatches = (mulMatches + doMatches + dontMatches).sortedBy { it.range.first }

      var count = 0L

      sortedMatches.forEach { match ->
        val value = match.value

        when (value.take(3)) {
          "do(" -> enabled = true
          "don" -> enabled = false
          "mul" -> if (enabled) { count += value.getMultiplicationResult() }
        }
      }

      count
    }
  }

  private fun String.getMultiplicationResult() =
    this.drop(4).dropLast(1).split(",").map { it.toInt() }.reduce(Int::times)
}
