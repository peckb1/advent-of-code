package me.peckb.aoc._2016.calendar.day12

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import me.peckb.aoc._2016.calendar.day12.Day12.Instruction.Copy
import me.peckb.aoc._2016.calendar.day12.Day12.Instruction.Decrement
import me.peckb.aoc._2016.calendar.day12.Day12.Instruction.Increment
import me.peckb.aoc._2016.calendar.day12.Day12.Instruction.JumpNonZero
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

typealias Register = Char
typealias Value = Int

class Day12 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::day12) { input ->
    val registers = mutableMapOf('a' to 0, 'b' to 0, 'c' to 0, 'd' to 0)
    registers.followInstructions(input.toList())
    registers['a']
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::day12) { input ->
    val registers = mutableMapOf('a' to 0, 'b' to 0, 'c' to 1, 'd' to 0)
    registers.followInstructions(input.toList())
    registers['a']
  }

  private fun MutableMap<Register, Value>.followInstructions(instructions: List<Instruction>) {
    var index = 0
    while(index < instructions.size) {
      when (val instruction = instructions[index]) {
        is Copy -> {
          val source: Value = instruction.source.fold({ it }, { this[it]!! })
          this[instruction.destination] = source
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
          if (source != 0) {
            index += instruction.amount
          } else {
            index++
          }
        }
      }
    }
  }

  private fun day12(line: String) : Instruction {
    val parts = line.split(" ")

    return when (parts[0]) {
      "cpy" -> {
        val source = parts[1].toIntOrNull()?.let(::Left) ?: Right(parts[1][0])
        Copy(source, parts[2][0])
      }
      "inc" -> Increment(parts[1][0])
      "dec" -> Decrement(parts[1][0])
      "jnz" -> {
        val source = parts[1].toIntOrNull()?.let(::Left) ?: Right(parts[1][0])
        JumpNonZero(source, parts[2].toInt())
      }
      else -> throw IllegalArgumentException("Unknown Instruction: $line")
    }
  }

  sealed class Instruction {
    data class Copy(val source: Either<Value, Register>, val destination: Register) : Instruction()
    data class Increment(val register: Register) : Instruction()
    data class Decrement(val register: Register) : Instruction()
    data class JumpNonZero(val source: Either<Value, Register>, val amount: Int) : Instruction()
  }
}
