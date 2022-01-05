package me.peckb.aoc._2016.calendar.day09

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day09 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    expand(input, false)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    expand(input, true)
  }

  private fun expand(input: String, fully: Boolean): Long {
    var index = 0
    var size = 0L
    while(index < input.length) {
      val startMarker = input.indexOf('(', index)
      index = if (startMarker == -1) {
        size += input.length - index
        input.length
      } else {
        val endMarker = input.indexOf(')', startMarker)
        val (numCharacters, numRepeats) = input.substring(startMarker + 1, endMarker).split("x").map { it.toInt() }
        val endOfSection = endMarker + 1 + numCharacters

        val childrenExpansion = if (fully) {
          expand(input.substring(endMarker + 1, endOfSection), fully)
        } else {
          numCharacters.toLong()
        }

        size += input.substring(index, startMarker).length
        size += childrenExpansion * numRepeats
        endOfSection
      }
    }
    return size
  }
}
