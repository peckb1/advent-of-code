package me.peckb.aoc._2020.calendar.day08

import me.peckb.aoc._2020.calendar.day08.Day08.Operation.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day08 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  enum class Operation(val keyword: String) {
    ACCUMULATOR("acc"),
    JUMP("jmp"),
    NO_OP("nop");

    companion object {
      fun fromKeyword(keyword: String): Operation {
        return Operation.values().first { it.keyword == keyword }
      }
    }
  }

  data class Instruction(
    val operation: Operation,
    val argument: Int
  )

  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::instruction) { input ->
    val instructions = input.toList()

    var accumulator = 0L
    var index = 0
    val instructionsRan= mutableSetOf<Int>()

    while(index < instructions.size) {
      instructionsRan.add(index)
      val instruction = instructions[index]
      when (instruction.operation) {
        ACCUMULATOR -> {
          accumulator += instruction.argument
          index++
        }
        JUMP -> index += instruction.argument
        NO_OP -> index++
      }
      if (instructionsRan.contains(index)) {
        index = instructions.size
      }
    }

    accumulator
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::instruction) { input ->
    val instructions = input.toList()
    val jumpIndices = instructions.indices.filter { instructions[it].operation == JUMP }
    val noOpOverride = Instruction(NO_OP, 0)

    var terminated = false
    var accumulator = 0L
    var testJumpIndicesIndex = 0

    while(!terminated) {

      var index = 0
      var duplicateInstruction = -1

      val instructionsRan= mutableSetOf<Int>()

      while(index < instructions.size) {
        instructionsRan.add(index)
        val instruction = if (index == jumpIndices[testJumpIndicesIndex]) {
          noOpOverride
        } else {
          instructions[index]
        }

        when (instruction.operation) {
          ACCUMULATOR -> {
            accumulator += instruction.argument
            index++
          }
          JUMP -> index += instruction.argument
          NO_OP -> index++
        }
        if (instructionsRan.contains(index)) {
          duplicateInstruction = index
          index = instructions.size
        }
      }

      if (duplicateInstruction == -1) {
        terminated = true
      } else {
        accumulator = 0
        testJumpIndicesIndex += 1
      }
    }

    accumulator
  }

  private fun instruction(line: String): Instruction = line.split(" ")
    .let { (operation, argument) ->
      Instruction(
        Operation.fromKeyword(operation),
        argument.toInt()
      )
    }
}
