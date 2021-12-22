package me.peckb.aoc._2015.calendar.day05

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day05 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    val VOWELS = setOf('a', 'e', 'i', 'o', 'u')
    val BAD_PAIRS = setOf("ab", "cd", "pq", "xy")
  }

  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    input.count { isNicePartOne(it) }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    input.count { isNicePartTwo(it) }
  }

  private fun isNicePartOne(word: String): Boolean {
    var threeVowels = false
    var doubleLetter = false
    var numVowels = if (word[0] in VOWELS) 1 else 0

    word.windowed(2).forEach { window ->
      val first = window[0]
      val second = window[1]

      if (!threeVowels) {
        if (second in VOWELS && ++numVowels == 3) threeVowels = true
      }
      if (!doubleLetter) {
        if (first == second) doubleLetter = true
      }
      if (window in BAD_PAIRS) return false
    }

    return threeVowels && doubleLetter
  }

  private fun isNicePartTwo(word: String): Boolean {
    val pairCounts = mutableMapOf<String, Int>()
    var hopLetter = false

    var threeBack = ' '
    var twoBack = ' '
    var oneBack = ' '
    word.forEach { me ->
      if (!hopLetter && me == twoBack) hopLetter = true

      if (me == oneBack && me == twoBack) {
        // we may have a triple
        if (me == threeBack) {
          // we're a quad
          pairCounts.merge("$oneBack$me", 1, Int::plus)
          twoBack = ' ' // in case we have a set of five or more, just reset it at a quad
        }
      } else {
        pairCounts.merge("$oneBack$me", 1, Int::plus)
      }

      threeBack = twoBack
      twoBack = oneBack
      oneBack = me
    }

    return hopLetter && pairCounts.values.any { it >= 2 }
  }
}
