package me.peckb.aoc._2018.calendar.day05

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.abs

class Day05 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    react(input)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    ('a'..'z').minOf { letter ->
      react(input.replace("$letter", "").replace(letter.uppercase(), ""))
    }
  }

  private fun react(input: String): Int {
    var polymer = input
    var removedItem = true
    while(removedItem) {
      removedItem = false
      var index = 0
      val newPolymer = StringBuilder()
      while(index < polymer.length - 1) {
        val a = polymer[index]
        val b = polymer[index + 1]
        if (abs(a - b) == 32) {
          removedItem = true
          index += 2
        } else {
          newPolymer.append(a)
          index++
        }
      }
      if (index == polymer.length - 1) {
        newPolymer.append(polymer.last())
      }
      polymer = newPolymer.toString()
    }

    return polymer.length
  }
}
