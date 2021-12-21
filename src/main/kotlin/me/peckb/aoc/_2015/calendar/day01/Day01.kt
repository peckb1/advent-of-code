package me.peckb.aoc._2015.calendar.day01

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day01 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val counts = mutableMapOf<Char, Long>('(' to 0, ')' to 0)

    input.forEach { counts.merge(it, 1, Long::plus) }

    counts['(']!! - counts[')']!!
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val counts = mutableMapOf<Char, Long>('(' to 0, ')' to 0)

    val noBasement = input.takeWhile { c ->
      counts.merge(c, 1, Long::plus)
      (counts['(']!! - counts[')']!! >= 0).also {
        -1
      }
    }

    noBasement.length + 1
  }
}
