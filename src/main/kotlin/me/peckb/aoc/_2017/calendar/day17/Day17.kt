package me.peckb.aoc._2017.calendar.day17

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day17 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val stepSize = input.toInt()
    val data = mutableListOf(0)

    var currentIndex = 0
    (1 .. 2017).forEach { n ->
      val indexToInsertAfter = (currentIndex + stepSize) % data.size
      currentIndex = indexToInsertAfter + 1
      data.add(currentIndex, n)
    }

    data[data.indexOf(2017) + 1]
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val stepSize = input.toInt()

    var currentIndex = 0
    var secondIndex = -1

    (1 .. 50_000_000).forEach { n ->
      val indexToInsertAfter = (currentIndex + stepSize) % n
      currentIndex = indexToInsertAfter + 1
      if (currentIndex == 1) secondIndex = n
    }

    secondIndex
  }
}
