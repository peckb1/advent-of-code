package me.peckb.aoc._2017.calendar.day23

import arrow.core.Either
import me.peckb.aoc._2017.calendar.day23.Day23.Instruction.Jnz
import me.peckb.aoc._2017.calendar.day23.Day23.Instruction.Mul
import me.peckb.aoc._2017.calendar.day23.Day23.Instruction.Set
import me.peckb.aoc._2017.calendar.day23.Day23.Instruction.Sub
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.abs

class Day23 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::instruction) { input ->
    val instructions = input.toList()
    val registers = mutableMapOf<Char, Long>().withDefault { 0 }

    var instructionIndex = 0
    var multiplications = 0

    while(instructionIndex in (instructions.indices)) {
      when (val instruction = instructions[instructionIndex]) {
        is Set -> {
          registers[instruction.register] = instruction.value.fromRegisters(registers)
          instructionIndex++
        }
        is Mul -> {
          multiplications++
          registers.merge(instruction.register, instruction.value.fromRegisters(registers), Long::times)
          instructionIndex++
        }
        is Sub -> {
          registers.merge(instruction.register, instruction.value.fromRegisters(registers), Long::minus)
          instructionIndex++
        }
        is Jnz -> {
          if (instruction.value.fromRegisters(registers) == 0L) {
            instructionIndex++
          } else {
            instructionIndex += instruction.steps.fromRegisters(registers).toInt()
          }
        }
      }
    }

    multiplications
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::instruction) { input ->
    val instructions = input.toList()
    val registers = mutableMapOf<Char, Long>('a' to 1).withDefault { 0L }

    var instructionIndex = 0
    while(instructionIndex in (instructions.indices) && registers.getValue('e') == 0L) {
      when (val instruction = instructions[instructionIndex]) {
        is Set -> {
          registers[instruction.register] = instruction.value.fromRegisters(registers)
          instructionIndex++
        }
        is Mul -> {
          registers.merge(instruction.register, instruction.value.fromRegisters(registers), Long::times)
          instructionIndex++
        }
        is Sub -> {
          registers.merge(instruction.register, instruction.value.fromRegisters(registers), Long::minus)
          instructionIndex++
        }
        is Jnz -> {
          if (instruction.value.fromRegisters(registers) == 0L) {
            instructionIndex++
          } else {
            instructionIndex += instruction.steps.fromRegisters(registers).toInt()
          }
        }
      }
    }

    val lowerBounds = registers.getValue('b')
    val upperBounds = registers.getValue('c')
    val stepSize = (instructions.takeLast(2).dropLast(1).first() as Sub)
      .value
      .fromRegisters(registers)
      .let { abs(it) }

    val primes = (lowerBounds..upperBounds step stepSize).count { n -> n.isPrime() }

    val numberOfItemsChecked = ((lowerBounds/stepSize)..(upperBounds/stepSize)).count()
    numberOfItemsChecked - primes
  }

  private fun instruction(line: String): Instruction {
    val parts = line.split(" ")
    return when (parts[0]) {
      "set" -> Set(parts[1][0], parts[2].toEither())
      "sub" -> Sub(parts[1][0], parts[2].toEither())
      "mul" -> Mul(parts[1][0], parts[2].toEither())
      "jnz" -> Jnz(parts[1].toEither(), parts[2].toEither())
      else -> throw IllegalArgumentException("Unknown instruction: $line")
    }
  }

  sealed class Instruction {
    data class Set(val register: Char, val value: Either<Int, Char>): Instruction()
    data class Sub(val register: Char, val value: Either<Int, Char>): Instruction()
    data class Mul(val register: Char, val value: Either<Int, Char>): Instruction()
    data class Jnz(val value: Either<Int, Char>, val steps: Either<Int, Char>): Instruction()
  }

  private fun String.toEither(): Either<Int, Char> {
    return toIntOrNull()?.let { Either.Left(it) } ?: Either.Right(this[0])
  }

  private fun Either<Int, Char>.fromRegisters(registers: MutableMap<Char, Long>): Long {
    return fold({ it.toLong() }, {registers.getValue(it) })
  }

  private fun Long.isPrime(): Boolean {
    var i = 2
    var flag = false
    while (i <= this / 2) {
      // condition for nonprime number
      if (this % i == 0L) {
        flag = true
        break
      }
      ++i
    }
    return !flag
  }
}
