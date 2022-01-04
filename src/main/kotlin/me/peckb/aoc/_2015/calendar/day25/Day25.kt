package me.peckb.aoc._2015.calendar.day25

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day25 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val targetY = input.substringAfter("row ").substringBefore(",").toInt()
    val targetX = input.substringAfter("column ").dropLast(1).toInt()

    var y = 1
    var x = 1
    var result = 20151125L

    val multiplier = 252533L
    val divisor = 33554393L

    while(!(y == targetY && x == targetX)) {
      if (y == 1) {
        y = x + 1
        x = 1
      } else {
        x++
        y--
      }

      result = result.times(multiplier).mod(divisor)
    }

    result
  }
}
