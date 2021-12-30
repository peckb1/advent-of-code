package me.peckb.aoc._2015.calendar.day10

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day10 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    var data = input
    repeat(PART_ONE_REPETITIONS) {
      val counts = generateCounts(data)
      data = asVerbal(counts)
    }

    data.length
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    var data = input
    repeat(PART_TWO_REPETITIONS) {
      val counts = generateCounts(data)
      data = asVerbal(counts)
    }

    data.length
  }

  private fun generateCounts(input: String): MutableList<Pair<Int, Char>> {
    val counts = mutableListOf<Pair<Int, Char>>()
    var lastChar: Char = input.first()
    var charCount = 1

    (1 until input.length).forEach { index ->
      val char = input[index]
      if (char == lastChar) {
        charCount++
      } else {
        counts.add(charCount to lastChar)
        charCount = 1
        lastChar = char
      }
    }
    counts.add(charCount to lastChar)
    return counts
  }

  private fun asVerbal(counts: MutableList<Pair<Int, Char>>): String {
    return counts.joinToString("") { (count, char) -> "$count$char" }
  }

  companion object {
    const val PART_ONE_REPETITIONS = 40
    const val PART_TWO_REPETITIONS = 50
  }
}
