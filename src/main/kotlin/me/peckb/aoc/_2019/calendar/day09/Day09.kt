package me.peckb.aoc._2019.calendar.day09

import kotlinx.coroutines.runBlocking
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer.Companion.asMutableMap
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day09 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    run(operations(input).asMutableMap(), 1)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    run(operations(input).asMutableMap(), 2)
  }

  private fun run(boostOperations: MutableMap<Long, String>, input: Long): List<Long> {
    val computer = IntcodeComputer()

    val output = mutableListOf<Long>()
    runBlocking {
      computer.runProgram(
        operations = boostOperations,
        userInput = { input },
        handleOutput = { output.add(it) }
      )
    }

    return output
  }

  private fun operations(line: String) = line.split(",")
}
