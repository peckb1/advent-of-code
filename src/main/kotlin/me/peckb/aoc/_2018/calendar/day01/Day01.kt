package me.peckb.aoc._2018.calendar.day01

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day01 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::frequencyChange) { input ->
    input.sum()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::frequencyChange) { input ->
    val frequencyChanges = input.toList()
    val frequencies = mutableSetOf<Int>()

    var noDuplicates = true
    var index = 0
    var frequency = 0

    while(noDuplicates) {
      frequency += frequencyChanges[index]
      noDuplicates = frequencies.add(frequency)
      index++
      index %= frequencyChanges.size
    }
    frequency
  }

  private fun frequencyChange(line: String) = line.toInt()
}
