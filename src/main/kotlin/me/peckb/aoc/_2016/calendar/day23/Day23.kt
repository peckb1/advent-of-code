package me.peckb.aoc._2016.calendar.day23

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import me.peckb.aoc._2016.calendar.day23.Day23.Instruction.Copy
import me.peckb.aoc._2016.calendar.day23.Day23.Instruction.Decrement
import me.peckb.aoc._2016.calendar.day23.Day23.Instruction.Increment
import me.peckb.aoc._2016.calendar.day23.Day23.Instruction.JumpNonZero
import me.peckb.aoc._2016.calendar.day23.Day23.Instruction.Toggle
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

typealias Register = Char
typealias Value = Int

class Day23 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::instruction) { input ->
    val registers = mutableMapOf('a' to 7, 'b' to 0, 'c' to 0, 'd' to 0)
    registers.followInstructions(input.toMutableList())
    registers['a']
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { _ ->
    // DEV NOTE: printing out the values by hand shows us what the calculations are doing
    factorial(12) + (90 * 73)
  }

  private fun factorial(n: Int): Long {
    var factorial: Long = 1
    (1..n).forEach { i ->
      factorial *= i
    }
    return factorial
  }

  private fun MutableMap<Register, Value>.followInstructions(instructions: MutableList<Instruction>) {
    var index = 0
    while (index < instructions.size) {
      when (val instruction = instructions[index]) {
        is Copy -> {
          val source: Value = instruction.source.fold({ it }, { this[it]!! })
          instruction.destination.map { register ->
            this[register] = source
          }
          index++
        }
        is Decrement -> {
          this[instruction.register] = this[instruction.register]!! - 1
          index++
        }
        is Increment -> {
          this[instruction.register] = this[instruction.register]!! + 1
          index++
        }
        is JumpNonZero -> {
          val source: Value = instruction.source.fold({ it }, { this[it]!! })
          val amount: Value = instruction.amount.fold({ it }, { this[it]!! })
          if (source != 0) {
            index += amount
          } else {
            index++
          }
        }
        is Toggle -> {
          val instructionDistance = this[instruction.register]!!
          val instructionIndexToChange = index + instructionDistance
          if (instructionIndexToChange in (0 until instructions.size)) {
            val instructionToToggle = instructions[instructionIndexToChange]
            instructions[instructionIndexToChange] = when (instructionToToggle) {
              is Copy -> JumpNonZero(instructionToToggle.source, instructionToToggle.destination)
              is Decrement -> Increment(instructionToToggle.register)
              is Increment -> Decrement(instructionToToggle.register)
              is JumpNonZero -> Copy(instructionToToggle.source, instructionToToggle.amount)
              is Toggle -> Increment(instructionToToggle.register)
            }
          }
          index++
        }
      }
    }
  }

  private fun instruction(line: String): Instruction {
    val parts = line.split(" ")

    return when (parts[0]) {
      "cpy" -> {
        val source = parts[1].toIntOrNull()?.let(::Left) ?: Right(parts[1][0])
        val destination = parts[2].toIntOrNull()?.let(::Left) ?: Right(parts[2][0])
        Copy(source, destination)
      }
      "inc" -> Increment(parts[1][0])
      "dec" -> Decrement(parts[1][0])
      "jnz" -> {
        val source = parts[1].toIntOrNull()?.let(::Left) ?: Right(parts[1][0])
        val amount = parts[2].toIntOrNull()?.let(::Left) ?: Right(parts[2][0])
        JumpNonZero(source, amount)
      }
      "tgl" -> Toggle(parts[1][0])
      else -> throw IllegalArgumentException("Unknown Instruction: $line")
    }
  }

  sealed class Instruction {
    data class Copy(val source: Either<Value, Register>, val destination: Either<Value, Register>) : Instruction()
    data class Increment(val register: Register) : Instruction()
    data class Decrement(val register: Register) : Instruction()
    data class JumpNonZero(val source: Either<Value, Register>, val amount: Either<Value, Register>) : Instruction()
    data class Toggle(val register: Register) : Instruction()
  }
}
