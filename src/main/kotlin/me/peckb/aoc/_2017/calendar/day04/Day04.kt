package me.peckb.aoc._2017.calendar.day04

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day04 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    // count up the number of words in the phrase, and check if any have more than one count
    input.map { phrase -> phrase.split(" ").groupBy { it }.any { it.value.size > 1 } }
      // and then count the ones which did not have any multiples
      .count { !it }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    // count up the number of words (sorted by char) in the phrase, and check if any have more than one count
    input.map { phrase -> phrase.split(" ").groupBy { it.toSortedSet() }.any { it.value.size > 1 } }
      // and then count the ones which did not have any multiples
      .count { !it }
  }
}
