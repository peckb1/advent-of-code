package me.peckb.aoc._2022.calendar.day03

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day03 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::characters) { input ->
    input.map { it.chunked(it.size / 2) }.map(::commonItem).sumOf(::priority)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::characters) { input ->
    input.chunked(3).map(::commonItem).sumOf(::priority)
  }

  private fun characters(line: String) = line.toList()

  private fun commonItem(data: List<List<Char>>): Char = data
    .map { it.toSet() }
    .reduce { a, b -> a.intersect(b) }
    .first()

  private fun priority(c: Char) = when (c) {
    in 'a'..'z' -> c.code - LOWER_CASE_TO_PRIORITY
    in 'A'..'Z' -> c.code - UPPER_CASE_TO_PRIORITY
    else -> 0
  }

  companion object {
    // 'a' on the ascii table is 97, so this gives us 1
    private const val LOWER_CASE_TO_PRIORITY = 96
    // 'A' on the ascii table is 65, so this gives us 27
    private const val UPPER_CASE_TO_PRIORITY = 64 - 26
  }
}
