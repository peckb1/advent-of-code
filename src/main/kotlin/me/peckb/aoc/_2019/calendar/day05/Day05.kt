package me.peckb.aoc._2019.calendar.day05

import kotlinx.coroutines.runBlocking
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day05 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String, userInput: () -> Long, outputHandler: (Long) -> Unit) = generatorFactory.forFile(filename).readOne { input ->
    val operations = operations(input).toMutableList()

    runBlocking { IntcodeComputer().runProgram(operations, userInput, outputHandler) }
  }

  fun partTwo(filename: String, userInput: () -> Long, handleOutput: (Long) -> Unit) = generatorFactory.forFile(filename).readOne { input ->
    val operations = operations(input).toMutableList()

    runBlocking { IntcodeComputer().runProgram(operations, userInput, handleOutput) }
  }

  private fun operations(line: String) = line.split(",")

  class IntcodeComputer {
    suspend fun runProgram(
      operations: MutableList<String>,
      userInput: suspend () -> Long,
      handleOutput: suspend (Long) -> Unit
    ) {
      var pointerIndex = 0
      while(operations[pointerIndex].toInt() != HALT_PROGRAM) {
        val codeString = operations[pointerIndex].padStart(5, '0')

        @Suppress("UNUSED_VARIABLE") val thirdParameterMode = codeString[0].digitToInt()
        val secondParameterMode by lazy { codeString[1].digitToInt() }
        val firstParameterMode by lazy { codeString[2].digitToInt() }

        val firstParameterValue by lazy { operations[pointerIndex + 1].toInt() }
        val secondParameterValue by lazy { operations[pointerIndex + 2].toInt() }
        val thirdParameterValue by lazy { operations[pointerIndex + 3].toInt() }

        val aValue by lazy {
          when (firstParameterMode) {
            IMMEDIATE_MODE -> firstParameterValue
            POSITION_MODE -> operations[firstParameterValue].toInt()
            else -> throw IllegalArgumentException("Invalid Mode")
          }.toLong()
        }
        val bValue by lazy {
          when (secondParameterMode) {
            IMMEDIATE_MODE -> secondParameterValue
            POSITION_MODE -> operations[secondParameterValue].toInt()
            else -> throw IllegalArgumentException("Invalid Mode")
          }.toLong()
        }

        when (codeString.takeLast(2).toInt()) {
          ADD -> {
            operations[thirdParameterValue] = (aValue + bValue).toString()
            pointerIndex += 4
          }
          MUL -> {
            operations[thirdParameterValue] = (aValue * bValue).toString()
            pointerIndex += 4
          }
          IN -> {
            val input = userInput().toString()
            operations[firstParameterValue] = input
            pointerIndex += 2
          }
          OUT -> {
            when (firstParameterMode) {
              IMMEDIATE_MODE -> handleOutput(firstParameterValue.toLong())
              POSITION_MODE -> handleOutput(operations[firstParameterValue].toLong())
            }
            pointerIndex += 2
          }
          JUMP_IF_TRUE -> {
            if (aValue != 0L) {
              pointerIndex = bValue.toInt()
            } else {
              pointerIndex += 3
            }
          }
          JUMP_IF_FALSE -> {
            if (aValue == 0L) {
              pointerIndex = bValue.toInt()
            } else {
              pointerIndex += 3
            }
          }
          LESS_THAN -> {
            val valueToSet = if (aValue < bValue) { 1 } else { 0 }
            operations[thirdParameterValue] = valueToSet.toString()
            pointerIndex += 4
          }
          EQUALS -> {
            val valueToSet = if (aValue == bValue) { 1 } else { 0 }
            operations[thirdParameterValue] = valueToSet.toString()
            pointerIndex += 4
          }
        }
      }
    }

    companion object {
      const val ADD  = 1
      const val MUL = 2
      const val IN = 3
      const val OUT = 4
      const val JUMP_IF_TRUE = 5
      const val JUMP_IF_FALSE = 6
      const val LESS_THAN = 7
      const val EQUALS = 8

      const val HALT_PROGRAM = 99

      const val POSITION_MODE = 0
      const val IMMEDIATE_MODE = 1
    }
  }
}
