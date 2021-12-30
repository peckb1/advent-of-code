package me.peckb.aoc._2015.calendar.day11

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day11 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    var nextPassword = nextPassword(input)
    while(nextPassword.isNotValid()) {
      nextPassword = nextPassword(nextPassword)
    }
    nextPassword
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    var nextPassword = nextPassword(input)
    while(nextPassword.isNotValid()) {
      nextPassword = nextPassword(nextPassword)
    }
    nextPassword = nextPassword(nextPassword)
    while(nextPassword.isNotValid()) {
      nextPassword = nextPassword(nextPassword)
    }
    nextPassword
  }

  private fun nextPassword(previousPassword: String): String {
    val charArray = previousPassword.toCharArray()
    var index = previousPassword.length - 1
    var done = false
    while(!done && index >= 0) {
      val nextLetter = charArray[index] + 1
      if (nextLetter in ('a'..'z')) {
        charArray[index] = nextLetter
        done = true
      } else {
        charArray[index] = 'a'
        index--
      }
    }
    return charArray.joinToString("")
  }

  private fun String.isNotValid(): Boolean {
    if (this.contains("[ilo]".toRegex())) return true
    this.windowed(3).find {
      val (a, b, c) = it.toCharArray()
      ((b - a) == 1 && (c - b) == 1)
    } ?: return true

    return !hasAtLeastTwoPairs(this)
  }

  // "borrowed" from 2015 Day05
  private fun hasAtLeastTwoPairs(word: String): Boolean {
    val pairCounts = mutableMapOf<String, Int>()

    var threeBack = ' '
    var twoBack = ' '
    var oneBack = ' '
    word.forEach { me ->
      if (me == oneBack && me == twoBack) {
        // we may have a triple
        if (me == threeBack) {
          // we're a quad
          if (me == oneBack) {
            pairCounts.merge("$oneBack$me", 1, Int::plus)
          }
          twoBack = ' ' // in case we have a set of five or more, just reset it at a quad
        }
      } else {
        if(me == oneBack) {
          pairCounts.merge("$oneBack$me", 1, Int::plus)
        }
      }

      threeBack = twoBack
      twoBack = oneBack
      oneBack = me
    }

    return pairCounts.size >= 2
  }
}
