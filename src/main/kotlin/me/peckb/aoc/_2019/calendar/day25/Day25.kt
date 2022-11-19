package me.peckb.aoc._2019.calendar.day25

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer.Companion.asMutableMap
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.StringBuilder
import java.util.concurrent.LinkedBlockingDeque

class Day25 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val operations = IntcodeComputer.operations(input).asMutableMap()
    val computer = IntcodeComputer()

    val commands = listOf(
      "west",
      "west",
      "west",
      "take coin",
      "east",
      "east",
      "east",
      "north",
      "north",
      "east",
      "take antenna",
      "west",
      "south",
      "east",
      "take cake",
      "west",
      "south",
      "east",
      "east",
      "east",
      "east",
      "take boulder",
      "north",
      "east",
    ).flatMap { line ->
      line.map { it.code.toLong() }.plus('\n'.code.toLong())
    }

    var commandIndex = 0

    fun userInput(): Long {
      return commands[commandIndex].also { commandIndex ++ }
    }

    var lastLine = StringBuilder()
    var newLine = false
    fun handleOutput(data: Long) {
      if (newLine) {
        lastLine = StringBuilder()
        newLine = false
      }
      data.toInt().toChar().also {
        lastLine.append(it)
        if (it == '\n') newLine = true
      }
    }

    runBlocking {
      computer.runProgram(
        operations = operations,
        userInput = ::userInput,
        handleOutput = ::handleOutput,
      )
    }

    lastLine.toString().split(" ")[11].toLong()
  }
}
