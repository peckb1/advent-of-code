package me.peckb.aoc._2015.calendar.day12

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day12 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    -1
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    -1
  }
}
