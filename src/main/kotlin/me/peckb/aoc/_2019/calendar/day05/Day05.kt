package me.peckb.aoc._2019.calendar.day05

import kotlinx.coroutines.runBlocking
import me.peckb.aoc._2019.calendar.day05.Day05.IntcodeComputer.Mode.*
import me.peckb.aoc._2019.calendar.day05.Day05.IntcodeComputer.Operations.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalStateException

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

  class IntcodeComputer {
    private var relativeBase = 0L

    suspend fun runProgram(
      operations: MutableMap<Long, String>,
      userInput: suspend () -> Long,
      handleOutput: suspend (Long) -> Unit
    ) {
      var pointerIndex = 0L
      while(operations.getOperation(pointerIndex).toInt() != HALT_PROGRAM.code) {
        val codeString = operations.getOperation(pointerIndex).padStart(5, '0')

        val thirdParameterMode by lazy { Mode.fromCode(codeString[0].toString()) }
        val secondParameterMode by lazy { Mode.fromCode(codeString[1].toString()) }
        val firstParameterMode by lazy { Mode.fromCode(codeString[2].toString()) }

        val firstParameterValue by lazy { operations.getOperation(pointerIndex + 1).toLong() }
        val secondParameterValue by lazy { operations.getOperation(pointerIndex + 2).toLong() }
        val thirdParameterValue by lazy { operations.getOperation(pointerIndex + 3).toLong() }

        val aValue by lazy {
          when (firstParameterMode) {
            IMMEDIATE_MODE -> firstParameterValue
            POSITION_MODE -> operations.getOperation(firstParameterValue).toLong()
            RELATIVE_MODE -> operations.getOperation(firstParameterValue + relativeBase).toLong()
          }
        }
        val bValue by lazy {
          when (secondParameterMode) {
            IMMEDIATE_MODE -> secondParameterValue
            POSITION_MODE -> operations.getOperation(secondParameterValue).toLong()
            RELATIVE_MODE -> operations.getOperation(secondParameterValue + relativeBase).toLong()
          }
        }
        val setValue by lazy {
          when (thirdParameterMode) {
            IMMEDIATE_MODE, POSITION_MODE -> thirdParameterValue
            RELATIVE_MODE -> thirdParameterValue + relativeBase
          }
        }

        when (Operations.fromCode(codeString.takeLast(2))) {
          ADD -> {
            operations[setValue] = (aValue + bValue).toString()
            pointerIndex += 4
          }
          MUL -> {
            operations[setValue] = (aValue * bValue).toString()
            pointerIndex += 4
          }
          IN -> {
            val input = userInput().toString()
            when (firstParameterMode) {
              POSITION_MODE, IMMEDIATE_MODE -> operations[firstParameterValue] = input
              RELATIVE_MODE -> operations[firstParameterValue + relativeBase] = input
            }
            pointerIndex += 2
          }
          OUT -> {
            val output = when (firstParameterMode) {
              IMMEDIATE_MODE -> firstParameterValue
              POSITION_MODE -> operations.getOperation(firstParameterValue).toLong()
              RELATIVE_MODE -> operations.getOperation(firstParameterValue + relativeBase).toLong()
            }
            handleOutput(output)
            pointerIndex += 2
          }
          JUMP_IF_TRUE -> {
            if (aValue != 0L) {
              pointerIndex = bValue
            } else {
              pointerIndex += 3
            }
          }
          JUMP_IF_FALSE -> {
            if (aValue == 0L) {
              pointerIndex = bValue
            } else {
              pointerIndex += 3
            }
          }
          LESS_THAN -> {
            val valueToSet = if (aValue < bValue) { 1 } else { 0 }
            operations[setValue] = valueToSet.toString()
            pointerIndex += 4
          }
          EQUALS -> {
            val valueToSet = if (aValue == bValue) { 1 } else { 0 }
            operations[setValue] = valueToSet.toString()
            pointerIndex += 4
          }
          RELATIVE_BASE -> {
            relativeBase += aValue
            pointerIndex += 2
          }
          HALT_PROGRAM -> { throw IllegalStateException("Should have exited before here") }
        }
      }
    }

    enum class Operations(val code: Int) {
      ADD(1),
      MUL(2),
      IN(3),
      OUT(4),
      JUMP_IF_TRUE(5),
      JUMP_IF_FALSE(6),
      LESS_THAN(7),
      EQUALS(8),
      RELATIVE_BASE(9),
      // Final Operation
      HALT_PROGRAM(99);

      companion object {
        fun fromCode(codeString: String) = values().first { it.code == codeString.toInt() }
      }
    }

    enum class Mode(val code: Int) {
      POSITION_MODE(0),
      IMMEDIATE_MODE(1),
      RELATIVE_MODE(2);

      companion object {
        fun fromCode(codeString: String) = values().first { it.code == codeString.toInt() }
      }
    }
  }

  companion object {
    fun List<String>.asMutableMap(): MutableMap<Long, String> = toMutableList()
      .mapIndexed { index, operation -> index.toLong() to operation }
      .toMap()
      .toMutableMap()
  }
}
private fun MutableMap<Long, String>.getOperation(pointerIndex: Long): String {
  return this.getOrDefault(pointerIndex, "0")
}
