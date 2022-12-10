package me.peckb.aoc._2022.calendar.day10

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalArgumentException
import kotlin.math.abs

class Day10 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::instruction) { input ->
    val program = input.toList()
    var programIndex = 0

    var xRegister: Int = 1
    var cycle = 0

    val things = ArrayDeque<Int>()
    var sum = 0

    while(programIndex < program.size - 1 || things.isNotEmpty()) {
      val instruction: Instruction? = if (programIndex < program.size - 1) {
        program[programIndex]
      } else {
        null
      }

      when (instruction) {
        is Instruction.AddX -> {
          things.addLast(0)
          things.addLast(instruction.v)
        }
        Instruction.Noop -> things.addLast(0)
        null -> { /* no op */ }
      }
      cycle++
      programIndex++

      if (cycle == 20) sum += (xRegister * 20)
      if (cycle == 60) sum += (xRegister * 60)
      if (cycle == 100) sum += (xRegister * 100)
      if (cycle == 140) sum += (xRegister * 140)
      if (cycle == 180) sum += (xRegister * 180)
      if (cycle == 220) sum += (xRegister * 220)

      xRegister += things.removeFirst()
    }

    sum
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::instruction) { input ->
    val width = 40
    val height = 6
    var xPosition = 0

    val program = input.toList()
    var programIndex = 0

    var xRegister: Int = 1
    var cycle = 0

    val valuesToAddToRegister = ArrayDeque<Int>()
    var sum = 0

    while(programIndex < program.size - 1 || valuesToAddToRegister.isNotEmpty()) {
      val instruction: Instruction? = if (programIndex < program.size - 1) {
        program[programIndex]
      } else {
        null
      }

      when (instruction) {
        is Instruction.AddX -> {
          valuesToAddToRegister.addLast(0)
          valuesToAddToRegister.addLast(instruction.v)
        }

        Instruction.Noop -> valuesToAddToRegister.addLast(0)
        null -> { /* no op */
        }
      }
      cycle++
      programIndex++

      if (abs(xPosition - xRegister) <= 1) {
        print("#")
      } else {
        print(".")
      }
      xPosition++
      if (cycle % width == 0) {
        println()
        xPosition = 0
      }

      xRegister += valuesToAddToRegister.removeFirst()
    }
  }

  private fun instruction(line: String) = line.split(" ").let {
    when (it[0]) {
      "noop" -> Instruction.Noop
      "addx" -> Instruction.AddX(it[1].toInt())
      else -> throw IllegalArgumentException("Invalid instruction $it[0]")
    }
  }

  sealed class Instruction {
    object Noop : Instruction()
    class AddX(val v: Int) : Instruction()
  }
}
