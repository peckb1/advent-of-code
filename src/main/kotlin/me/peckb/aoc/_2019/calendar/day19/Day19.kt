package me.peckb.aoc._2019.calendar.day19

import kotlinx.coroutines.runBlocking
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer.Companion.asMutableMap
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.StringBuilder

class Day19 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val operations = IntcodeComputer.operations(input)
    val computer = IntcodeComputer()

    val maxX = 50L
    val maxY = 50L

    var inputMode = InputMode.X
    var positionsUnderControl = 0

    var lastFirstFoundX = 0L
    (0L until maxY).forEach yLoop@ { y ->
      var newLine = true
      var foundBeam = false
      var foundEnd = false
      (0L until maxX).forEach xLoop@ { x ->
        if (foundEnd || x < lastFirstFoundX) return@xLoop

        fun userInput(): Long {
          return when (inputMode) {
            InputMode.X -> x
            InputMode.Y -> y
          }.also { inputMode = inputMode.nextMode() }
        }

        fun handleOutput(output: Long) {
          when (output) {
            0L -> {
              if (foundBeam) { foundEnd = true }
            }
            1L -> {
              positionsUnderControl++
              if (newLine) {
                foundBeam = true
                newLine = false
                lastFirstFoundX = x
              }
            }
          }
        }

        runBlocking { computer.runProgram(operations.asMutableMap(), ::userInput, ::handleOutput) }
      }
    }

    positionsUnderControl
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val operations = IntcodeComputer.operations(input)
    val computer = IntcodeComputer()

    val maxX = 1000L
    val maxY = 1200L
    val last100Rows = ArrayDeque<String>(initialCapacity = 100)
    var startBoxX = -1L
    var startBoxY = -1L

    var inputMode = InputMode.X
    var lastFirstFoundX = 0L
    var foundBox = false
    (900L until maxY).forEach yLoop@ { y ->
      if (foundBox) return@yLoop

      var newLine = true
      var foundBeam = false
      var foundEnd = false
      val lineAsStringBuilder = StringBuilder(100)
      (0L until maxX).forEach xLoop@ { x ->
        if (foundEnd) return@xLoop
        if (x < lastFirstFoundX) {
          lineAsStringBuilder.append(".")
          return@xLoop
        }

        fun userInput(): Long {
          return when (inputMode) {
            InputMode.X -> x
            InputMode.Y -> y
          }.also { inputMode = inputMode.nextMode() }
        }

        fun handleOutput(output: Long) {
          when (output) {
            0L -> {
              lineAsStringBuilder.append(".")
              if (foundBeam) {
                foundEnd = true
              }
            }
            1L -> {
              lineAsStringBuilder.append("#")
              if (newLine) {
                foundBeam = true
                newLine = false
                lastFirstFoundX = x
              }
            }
          }
        }

        runBlocking {
          computer.runProgram(operations.asMutableMap(), ::userInput, ::handleOutput)
        }
      }
      last100Rows.add(lineAsStringBuilder.toString())

      if (last100Rows.size == 100) {
        val beamLengthFromMyStarting = last100Rows.removeFirst().substring(lastFirstFoundX.toInt()).count { it == '#' }
        if (beamLengthFromMyStarting == 100) {
          startBoxX = lastFirstFoundX
          startBoxY = y - 99
          foundBox = true
        }
      }
    }

    (startBoxX * 10_000) + startBoxY
  }

  enum class InputMode {
    X { override fun nextMode() = Y },
    Y { override fun nextMode() = X };

    abstract fun nextMode(): InputMode
  }
}
