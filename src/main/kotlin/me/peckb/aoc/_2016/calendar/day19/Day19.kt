package me.peckb.aoc._2016.calendar.day19

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day19 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    var elfs = (1..input.toInt()).map { it }
    while(elfs.size != 1) {
      elfs = if (elfs.size % 2 == 0) {
        elfs.filterIndexed { i, _ -> i % 2 == 0 }
      } else {
        elfs.filterIndexed { i, _ -> i % 2 == 0 }.drop(1)
      }
    }
    elfs.first()
  }

  data class Item(val i: Int, var active: Boolean)

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val elfs = (1..input.toInt()).map { Item(it, true) }

    var currentIndex = elfs.size / 2
    var remaining = elfs.size
    var counter = 0

    while (remaining > 1) {
      if (elfs[currentIndex].active) {
        if (counter != 2) {
          elfs[currentIndex].active = false
          remaining--
        }
        counter++
        counter %= 3
      }
      currentIndex++
      currentIndex %= elfs.size
    }

    elfs.first { it.active }.i
  }
}
