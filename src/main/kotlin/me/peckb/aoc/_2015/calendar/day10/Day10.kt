package me.peckb.aoc._2015.calendar.day10

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.StringBuilder

class Day10 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    var data = input
    repeat(PART_ONE_REPETITIONS) { data = generateNextWord(data) }
    data.length
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    var data = input
    repeat(PART_TWO_REPETITIONS) { data = generateNextWord(data) }
    data.length
  }

  private fun generateNextWord(input: String): String {
    val sb = StringBuilder()
    var lastChar: Char = input.first()
    var charCount = 1

    (1 until input.length).forEach { index ->
      val char = input[index]
      if (char == lastChar) {
        charCount++
      } else {
        sb.append("$charCount$lastChar")
        charCount = 1
        lastChar = char
      }
    }
    sb.append("$charCount$lastChar")
    return sb.toString()
  }

  companion object {
    const val PART_ONE_REPETITIONS = 40
    const val PART_TWO_REPETITIONS = 50
  }
}
