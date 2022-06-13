package me.peckb.aoc._2019.calendar.day04

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day04 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { rangeString ->
    rangeString.countMatches { digits ->
      // non-decreasing            // double number
      digits.sorted() == digits && digits.windowed(2).any { (a, b) -> a == b }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { rangeString ->
    rangeString.countMatches { digits ->
      // non-decreasing            // tighter double number
      digits.sorted() == digits && hasDouble(digits)
    }
  }

  private fun String.countMatches(digitChecking: (List<Int>) -> Boolean): Int {
    val (start, end) = split("-").map { it.toInt() }

    return (start..end).filter { candidate ->
      digitChecking(candidate.toString().map { it.code })
    }.size
  }

  private fun hasDouble(digits: List<Int>): Boolean {
    var foundOnlyPair = false

    var twoBack = -1
    var oneBack = -1
    digits.forEach { digit ->
      when {
        digit == oneBack && digit == twoBack -> foundOnlyPair = false
        digit == oneBack -> foundOnlyPair = true
        foundOnlyPair -> return true
      }

      twoBack = oneBack
      oneBack = digit
    }

    return foundOnlyPair
  }
}
