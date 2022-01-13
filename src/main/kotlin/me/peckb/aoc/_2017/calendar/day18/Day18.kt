package me.peckb.aoc._2017.calendar.day18

import arrow.core.Either
import me.peckb.aoc._2017.calendar.day18.Instruction.Add
import me.peckb.aoc._2017.calendar.day18.Instruction.JumpGreaterThanZero
import me.peckb.aoc._2017.calendar.day18.Instruction.Modulo
import me.peckb.aoc._2017.calendar.day18.Instruction.Multiply
import me.peckb.aoc._2017.calendar.day18.Instruction.Receive
import me.peckb.aoc._2017.calendar.day18.Instruction.Send
import me.peckb.aoc._2017.calendar.day18.Instruction.Set
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day18 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::instruction) { input ->
    val registers = mutableMapOf<Char, Long>().withDefault { 0 }

    val instructions = input.toList()
    var recoveredFrequency: Long? = null
    var lastFrequencySent: Long? = null
    var instructionIndex = 0

    while(recoveredFrequency == null) {
      when (val next = instructions[instructionIndex]) {
        is Add -> {
          registers[next.register] = registers.getValue(next.register) + next.value.fromRegisters(registers)
          instructionIndex++
        }
        is JumpGreaterThanZero -> {
          if (next.value.fromRegisters(registers) > 0) {
            instructionIndex += next.steps.fromRegisters(registers).toInt()
          } else {
            instructionIndex++
          }
        }
        is Modulo -> {
          registers[next.register] = registers.getValue(next.register) % next.value.fromRegisters(registers)
          instructionIndex++
        }
        is Multiply -> {
          registers[next.register] = registers.getValue(next.register) * next.value.fromRegisters(registers)
          instructionIndex++
        }
        is Receive -> {
          if (registers.getValue(next.register) != 0L) {
            recoveredFrequency = lastFrequencySent
          }
          instructionIndex++
        }
        is Send -> {
          lastFrequencySent = registers.getValue(next.register)
          instructionIndex++
        }
        is Set -> {
          registers[next.register] = next.value.fromRegisters(registers)
          instructionIndex++
        }
      }
    }

    recoveredFrequency
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::instruction) { input ->
    val instructions = input.toList()

    val messagesForProgram0 = ArrayDeque<Long>()
    val messagesForProgram1 = ArrayDeque<Long>()

    var messagesProgram1Sent = 0

    val program0 = Program(0, instructions) {
      messagesForProgram1.add(it)
    }
    val program1 = Program(1, instructions) {
      messagesForProgram0.add(it)
      messagesProgram1Sent++
    }

    program1.start()
    program0.start()

    while (messagesForProgram0.isNotEmpty() || messagesForProgram1.isNotEmpty()) {
      if (messagesForProgram0.isNotEmpty()) program0.send(messagesForProgram0.removeFirst())
      if (messagesForProgram1.isNotEmpty()) program1.send(messagesForProgram1.removeFirst())
    }

    messagesProgram1Sent
  }

  private fun instruction(line: String): Instruction {
    val parts = line.split(" ")
    return when (parts[0]) {
      "snd" -> Send(parts[1][0])
      "set" -> Set(parts[1][0], parts[2].toEither())
      "add" -> Add(parts[1][0], parts[2].toEither())
      "mul" -> Multiply(parts[1][0], parts[2].toEither())
      "mod" -> Modulo(parts[1][0], parts[2].toEither())
      "rcv" -> Receive(parts[1][0])
      "jgz" -> JumpGreaterThanZero(parts[1].toEither(), parts[2].toEither())
      else -> throw IllegalArgumentException("Unknown Instruction: $line")
    }
  }

  private fun String.toEither(): Either<Int, Char> {
    return toIntOrNull()?.let { Either.Left(it) } ?: Either.Right(this[0])
  }

  companion object {
    fun Either<Int, Char>.fromRegisters(registers: MutableMap<Char, Long>): Long {
      return fold({ it.toLong() }, {registers.getValue(it) })
    }
  }
}
