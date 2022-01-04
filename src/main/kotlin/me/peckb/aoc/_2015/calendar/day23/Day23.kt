package me.peckb.aoc._2015.calendar.day23

import me.peckb.aoc._2015.calendar.day23.Day23.Instruction.Half
import me.peckb.aoc._2015.calendar.day23.Day23.Instruction.Increment
import me.peckb.aoc._2015.calendar.day23.Day23.Instruction.Jump
import me.peckb.aoc._2015.calendar.day23.Day23.Instruction.JumpIfEven
import me.peckb.aoc._2015.calendar.day23.Day23.Instruction.JumpIfOne
import me.peckb.aoc._2015.calendar.day23.Day23.Instruction.Triple
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day23 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::day23) { input ->
    val instructions = input.toList()
    val registers: MutableMap<Char, Long> = mutableMapOf(
      'a' to 0,
      'b' to 0
    )

    runInstructions(instructions, registers)

    registers['b']
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::day23) { input ->
    val instructions = input.toList()
    val registers: MutableMap<Char, Long> = mutableMapOf(
      'a' to 1,
      'b' to 0
    )

    runInstructions(instructions, registers)

    registers['b']
  }

  private fun runInstructions(instructions: List<Instruction>, registers: MutableMap<Char, Long>) {
    var done = false
    var instructionIndex = 0
    while (!done) {
      when (val nextInstruction = instructions[instructionIndex]) {
        is Half -> {
          registers[nextInstruction.register] = registers[nextInstruction.register]!! / 2
          instructionIndex++
        }
        is Triple -> {
          registers[nextInstruction.register] = registers[nextInstruction.register]!! * 3
          instructionIndex++
        }
        is Increment -> {
          registers[nextInstruction.register] = registers[nextInstruction.register]!! + 1
          instructionIndex++
        }
        is Jump -> {
          instructionIndex += nextInstruction.pointer
        }
        is JumpIfEven -> {
          if (registers[nextInstruction.register]!! % 2 == 0L) {
            instructionIndex += nextInstruction.pointer
          } else {
            instructionIndex++
          }
        }
        is JumpIfOne -> {
          if (registers[nextInstruction.register]!! == 1L) {
            instructionIndex += nextInstruction.pointer
          } else {
            instructionIndex++
          }
        }
      }
      if (instructionIndex >= instructions.size) done = true
    }
  }

  private fun day23(line: String): Instruction {
    return when (val instructionString = line.substringBefore(" ")) {
      "hlf" -> Half(line[4])
      "tpl" -> Triple(line[4])
      "inc" -> Increment(line[4])
      "jmp" -> Jump(line.substringAfter(" ").toInt())
      "jie" -> JumpIfEven(line[4], line.substringAfter(", ").toInt())
      "jio" -> JumpIfOne(line[4], line.substringAfter(", ").toInt())
      else -> throw IllegalArgumentException("Unknown Instruction: $instructionString")
    }
  }

  sealed class Instruction {
    data class Half(val register: Char) : Instruction()
    data class Triple(val register: Char) : Instruction()
    data class Increment(val register: Char) : Instruction()
    data class Jump(val pointer: Int) : Instruction()
    data class JumpIfEven(val register: Char, val pointer: Int) : Instruction()
    data class JumpIfOne(val register: Char, val pointer: Int) : Instruction()
  }
}
