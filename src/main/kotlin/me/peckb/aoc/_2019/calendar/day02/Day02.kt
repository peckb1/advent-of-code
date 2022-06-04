package me.peckb.aoc._2019.calendar.day02

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day02 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val operations = operations(input).toMutableList()

    operations[1] = 12
    operations[2] = 2

    runProgram(operations)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val originalList = operations(input)

    var foundNoun: Int? = null
    var foundVerb: Int? = null

    (0..99).forEach noun@ { noun ->
      (0 .. 99).forEach { verb ->
        val operationsToExecute = originalList.toMutableList().also {
          it[1] = noun
          it[2] = verb
        }

        val result = runProgram(operationsToExecute)
        if (result == 19690720) {
          foundNoun = noun
          foundVerb = verb
          return@noun
        }
      }
    }

    (100 * (foundNoun ?: 0)) + (foundVerb ?: 0)
  }

  private fun operations(line: String) = line.split(",").map { it.toInt() }

  private fun runProgram(operations: MutableList<Int>): Int {
    var pointerIndex = 0
    while(operations[pointerIndex] != HALT_PROGRAM) {
      val code = operations[pointerIndex]
      val aIndex = operations[pointerIndex + 1]
      val bIndex = operations[pointerIndex + 2]
      val resultIndex = operations[pointerIndex + 3]

      when (code) {
        ADD -> operations[resultIndex] = operations[aIndex] + operations[bIndex]
        MUL -> operations[resultIndex] = operations[aIndex] * operations[bIndex]
        else -> throw IllegalArgumentException("Unknown OpCode $code")
      }

      pointerIndex += 4
    }

    return operations[0]
  }

  companion object {
    const val ADD = 1
    const val MUL = 2
    const val HALT_PROGRAM = 99
  }
}
