package me.peckb.aoc._2019.calendar.day05

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day05 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String, userInput: () -> Int, handleOutput: (Int) -> Unit) = generatorFactory.forFile(filename).readOne { input ->
    val operations = operations(input).toMutableList()

    runProgram(operations, userInput, handleOutput)
  }

  fun partTwo(filename: String, userInput: () -> Int, handleOutput: (Int) -> Unit) = generatorFactory.forFile(filename).readOne { input ->
    val operations = operations(input).toMutableList()

    runProgram(operations, userInput, handleOutput)
  }

  private fun operations(line: String) = line.split(",")

  private fun runProgram(
    operations: MutableList<String>,
    userInput: () -> Int,
    handleOutput: (Int) -> Unit
  ) {
    var pointerIndex = 0
    while(operations[pointerIndex].toInt() != HALT_PROGRAM) {
      val codeString = operations[pointerIndex].padStart(5, '0')
      val thirdParameterMode = codeString[0].digitToInt()
      val secondParameterMode = codeString[1].digitToInt()
      val firstParameterMode = codeString[2].digitToInt()
      val code = codeString.takeLast(2).toInt()

      when (code) {
        ADD -> {
          val firstParameterValue = operations[pointerIndex + 1].toInt()
          val secondParameterValue = operations[pointerIndex + 2].toInt()
          val thirdParameterValue = operations[pointerIndex + 3].toInt()

          val aValue = when (firstParameterMode) {
            IMMEDIATE_MODE -> firstParameterValue
            POSITION_MODE -> operations[firstParameterValue].toInt()
            else -> throw IllegalArgumentException("Invalid Mode")
          }.toInt()
          val bValue = when (secondParameterMode) {
            IMMEDIATE_MODE -> secondParameterValue
            POSITION_MODE -> operations[secondParameterValue].toInt()
            else -> throw IllegalArgumentException("Invalid Mode")
          }.toInt()

          operations[thirdParameterValue] = (aValue + bValue).toString()

          pointerIndex += 4
        }
        MUL -> {
          val firstParameterValue = operations[pointerIndex + 1].toInt()
          val secondParameterValue = operations[pointerIndex + 2].toInt()
          val thirdParameterValue = operations[pointerIndex + 3].toInt()

          val aValue = when (firstParameterMode) {
            IMMEDIATE_MODE -> firstParameterValue
            POSITION_MODE -> operations[firstParameterValue].toInt()
            else -> throw IllegalArgumentException("Invalid Mode")
          }.toInt()
          val bValue = when (secondParameterMode) {
            IMMEDIATE_MODE -> secondParameterValue
            POSITION_MODE -> operations[secondParameterValue].toInt()
            else -> throw IllegalArgumentException("Invalid Mode")
          }.toInt()

          operations[thirdParameterValue] = (aValue * bValue).toString()

          pointerIndex += 4
        }
        IN -> {
          val input = userInput().toString()
          val parameterValue = operations[pointerIndex + 1].toInt()

          operations[parameterValue] = input

          pointerIndex += 2
        }
        OUT -> {
          val parameterValue = operations[pointerIndex + 1].toInt()
          val registerValue = operations[parameterValue].toInt()

          when (firstParameterMode) {
            IMMEDIATE_MODE -> handleOutput(parameterValue)
            POSITION_MODE -> handleOutput(registerValue)
          }
          pointerIndex += 2
        }
        JUMP_IF_TRUE -> {
          val firstParameterValue = operations[pointerIndex + 1].toInt()
          val secondParameterValue = operations[pointerIndex + 2].toInt()

          val aValue = when (firstParameterMode) {
            IMMEDIATE_MODE -> firstParameterValue
            POSITION_MODE -> operations[firstParameterValue].toInt()
            else -> throw IllegalArgumentException("Invalid Mode")
          }.toInt()

          if (aValue != 0) {
            val bValue = when (secondParameterMode) {
              IMMEDIATE_MODE -> secondParameterValue
              POSITION_MODE -> operations[secondParameterValue].toInt()
              else -> throw IllegalArgumentException("Invalid Mode")
            }.toInt()

            pointerIndex = bValue
          } else {
            pointerIndex += 3
          }
        }
        JUMP_IF_FALSE -> {
          val firstParameterValue = operations[pointerIndex + 1].toInt()
          val secondParameterValue = operations[pointerIndex + 2].toInt()

          val aValue = when (firstParameterMode) {
            IMMEDIATE_MODE -> firstParameterValue
            POSITION_MODE -> operations[firstParameterValue].toInt()
            else -> throw IllegalArgumentException("Invalid Mode")
          }.toInt()

          if (aValue == 0) {
            val bValue = when (secondParameterMode) {
              IMMEDIATE_MODE -> secondParameterValue
              POSITION_MODE -> operations[secondParameterValue].toInt()
              else -> throw IllegalArgumentException("Invalid Mode")
            }.toInt()

            pointerIndex = bValue
          } else {
            pointerIndex += 3
          }
        }
        LESS_THAN -> {
          val firstParameterValue = operations[pointerIndex + 1].toInt()
          val secondParameterValue = operations[pointerIndex + 2].toInt()
          val thirdParameterValue = operations[pointerIndex + 3].toInt()

          val aValue = when (firstParameterMode) {
            IMMEDIATE_MODE -> firstParameterValue
            POSITION_MODE -> operations[firstParameterValue].toInt()
            else -> throw IllegalArgumentException("Invalid Mode")
          }.toInt()

          val bValue = when (secondParameterMode) {
            IMMEDIATE_MODE -> secondParameterValue
            POSITION_MODE -> operations[secondParameterValue].toInt()
            else -> throw IllegalArgumentException("Invalid Mode")
          }.toInt()

          val valueToSet = if (aValue < bValue) { 1 } else { 0 }

          operations[thirdParameterValue] = valueToSet.toString()

          pointerIndex += 4
        }
        EQUALS -> {
          val firstParameterValue = operations[pointerIndex + 1].toInt()
          val secondParameterValue = operations[pointerIndex + 2].toInt()
          val thirdParameterValue = operations[pointerIndex + 3].toInt()

          val aValue = when (firstParameterMode) {
            IMMEDIATE_MODE -> firstParameterValue
            POSITION_MODE -> operations[firstParameterValue].toInt()
            else -> throw IllegalArgumentException("Invalid Mode")
          }.toInt()

          val bValue = when (secondParameterMode) {
            IMMEDIATE_MODE -> secondParameterValue
            POSITION_MODE -> operations[secondParameterValue].toInt()
            else -> throw IllegalArgumentException("Invalid Mode")
          }.toInt()

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
