package me.peckb.aoc._2017.calendar.day01

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day01 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    input.plus(input[0]).windowed(2).sumOf {
      val a = Character.getNumericValue(it[0])
      val b = Character.getNumericValue(it[1])
      if (a == b) a else 0
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val half = input.length / 2
    (0 until half).sumOf {
      val a = Character.getNumericValue(input[it])
      val b = Character.getNumericValue(input[it + half])
      if (a == b) (2 * a) else 0
    }
  }
}
