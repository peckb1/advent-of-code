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
      while(operations.getOrDefault(pointerIndex, "0").toInt() != HALT_PROGRAM.code) {
        val codeString = operations.getOrDefault(pointerIndex, "0").padStart(5, '0')

        val thirdParameterMode by lazy { Mode.fromCode(codeString[0].toString()) }
        val secondParameterMode by lazy { Mode.fromCode(codeString[1].toString()) }
        val firstParameterMode by lazy { Mode.fromCode(codeString[2].toString()) }

        val firstParameterValue by lazy { operations.getOrDefault(pointerIndex + 1, "0").toLong() }
        val secondParameterValue by lazy { operations.getOrDefault(pointerIndex + 2, "0").toLong() }
        val thirdParameterValue by lazy { operations.getOrDefault(pointerIndex + 3, "0").toLong() }

        val aValue by lazy {
          when (firstParameterMode) {
            IMMEDIATE_MODE -> firstParameterValue
            POSITION_MODE -> operations.getOrDefault(firstParameterValue, "0").toLong()
            RELATIVE_MODE -> operations.getOrDefault(firstParameterValue + relativeBase, "0").toLong()
          }
        }
        val bValue by lazy {
          when (secondParameterMode) {
            IMMEDIATE_MODE -> secondParameterValue
            POSITION_MODE -> operations.getOrDefault(secondParameterValue, "0").toLong()
            RELATIVE_MODE -> operations.getOrDefault(secondParameterValue + relativeBase, "0").toLong()
          }
        }

        when (Operations.fromCode(codeString.takeLast(2))) {
          ADD -> {
            when (thirdParameterMode) {
              POSITION_MODE,
              IMMEDIATE_MODE -> operations[thirdParameterValue] = (aValue + bValue).toString()
              RELATIVE_MODE -> operations[thirdParameterValue + relativeBase] = (aValue + bValue).toString()
            }
            pointerIndex += 4
          }
          MUL -> {
            when (thirdParameterMode) {
              POSITION_MODE,
              IMMEDIATE_MODE -> operations[thirdParameterValue] = (aValue * bValue).toString()
              RELATIVE_MODE -> operations[thirdParameterValue + relativeBase] = (aValue * bValue).toString()
            }
            pointerIndex += 4
          }
          IN -> {
            val input = userInput().toString()
            when (firstParameterMode) {
              POSITION_MODE,
              IMMEDIATE_MODE -> operations[firstParameterValue] = input
              RELATIVE_MODE -> operations[firstParameterValue + relativeBase] = input
            }
            pointerIndex += 2
          }
          OUT -> {
            when (firstParameterMode) {
              IMMEDIATE_MODE -> handleOutput(firstParameterValue)
              POSITION_MODE -> handleOutput(operations.getOrDefault(firstParameterValue, "0").toLong())
              RELATIVE_MODE -> handleOutput(operations.getOrDefault(firstParameterValue + relativeBase, "0").toLong())
            }
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
            when (thirdParameterMode) {
              POSITION_MODE,
              IMMEDIATE_MODE -> operations[thirdParameterValue] = valueToSet.toString()
              RELATIVE_MODE -> operations[thirdParameterValue + relativeBase] = valueToSet.toString()
            }
            pointerIndex += 4
          }
          EQUALS -> {
            val valueToSet = if (aValue == bValue) { 1 } else { 0 }
            when (thirdParameterMode) {
              POSITION_MODE,
              IMMEDIATE_MODE -> operations[thirdParameterValue] = valueToSet.toString()
              RELATIVE_MODE -> operations[thirdParameterValue + relativeBase] = valueToSet.toString()
            }
            pointerIndex += 4
          }
          RELATIVE_BASE -> {
            relativeBase += when (firstParameterMode) {
              IMMEDIATE_MODE -> firstParameterValue
              POSITION_MODE -> operations.getOrDefault(firstParameterValue, "0").toLong()
              RELATIVE_MODE -> operations.getOrDefault(firstParameterValue + relativeBase, "0").toLong()
            }
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
