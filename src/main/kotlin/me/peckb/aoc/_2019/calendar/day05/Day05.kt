package me.peckb.aoc._2019.calendar.day05

import kotlinx.coroutines.runBlocking
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer.Companion.asMutableMap
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day05 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String, userInput: () -> Long, outputHandler: (Long) -> Unit) = generatorFactory.forFile(filename).readOne { input ->
    val operations = operations(input).asMutableMap()

    runBlocking { IntcodeComputer().runProgram(operations, userInput, outputHandler) }
  }

  fun partTwo(filename: String, userInput: () -> Long, handleOutput: (Long) -> Unit) = generatorFactory.forFile(filename).readOne { input ->
    val operations = operations(input).asMutableMap()

    runBlocking { IntcodeComputer().runProgram(operations, userInput, handleOutput) }
  }

  private fun operations(line: String) = line.split(",")
}
