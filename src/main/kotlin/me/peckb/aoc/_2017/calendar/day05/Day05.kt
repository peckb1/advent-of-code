package me.peckb.aoc._2017.calendar.day05

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day05 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::jump) { input ->
    val jumps = input.toMutableList()
    var index = 0
    var steps = 0
    val validRanges = (0 until jumps.size)
    while (index in validRanges) {
      val newIndex = index + jumps[index]
      jumps[index] = jumps[index] + 1
      index = newIndex
      steps++
    }
    steps
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::jump) { input ->
    val jumps = input.toMutableList()
    var index = 0
    var steps = 0L
    val validRanges = (0 until jumps.size)
    while (index in validRanges) {
      val newIndex = index + jumps[index]
      val oldValue = jumps[index]
      if (oldValue >= 3) {
        jumps[index] = jumps[index] - 1
      } else {
        jumps[index] = jumps[index] + 1
      }
      index = newIndex
      steps++
    }
    steps
  }

  private fun jump(line: String) = line.toInt()
}
