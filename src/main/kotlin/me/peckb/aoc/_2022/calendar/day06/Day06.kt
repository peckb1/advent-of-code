package me.peckb.aoc._2022.calendar.day06

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day06 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    findMarker(input, 4)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    findMarker(input, 14)
  }

  private fun findMarker(input: String, size: Int) = input
    .toList()
    .windowed(size)
    .indexOfFirst { it.toSet().size == size } + size
}
