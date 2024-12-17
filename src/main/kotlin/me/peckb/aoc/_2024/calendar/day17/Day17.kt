package me.peckb.aoc._2024.calendar.day17

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.pow

class Day17 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val computer = Computer.fromInput(input)
    val output = runProgram(computer)
    output.joinToString(",")
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val computer = Computer.fromInput(input)

    findProgram(computer, computer.operations.map{ it.toLong() })
  }

  private fun findProgram(computer: Computer, target: List<Long>): Long {
    var aStart = if (target.size > 1) {
      8 * findProgram(computer, target.drop(1))
    } else { 0 }

    while(runProgram(computer.copy(aReg = aStart)) != target) {
      aStart++
    }

    return aStart
  }

  private fun runProgram(c: Computer): List<Long> {
    val output = mutableListOf<Long>()
    var programPointer = 0

    while(programPointer < c.operations.size - 1) {
      val opcode  = c.operations[programPointer]
      val operand = c.operations[programPointer + 1]

      programPointer += 2

      val comboOperand by lazy {
        when (operand) {
          0, 1, 2, 3 -> operand.toLong()
          4 -> c.aReg
          5 -> c.bReg
          6 -> c.cReg
          else -> throw IllegalArgumentException("Invalid operand: $operand")
        }
      }

      when (opcode) {
        0 -> c.aReg = (c.aReg / 2.0.pow(comboOperand.toDouble())).toLong()
        1 -> c.bReg = c.bReg xor operand.toLong()
        2 -> c.bReg = comboOperand % 8
        3 -> if (c.aReg != 0L) { programPointer = operand }
        4 -> c.bReg = c.bReg xor c.cReg
        5 -> output.add(comboOperand % 8)
        6 -> c.bReg = (c.aReg / 2.0.pow(comboOperand.toDouble())).toLong()
        7 -> c.cReg = (c.aReg / 2.0.pow(comboOperand.toDouble())).toLong()
      }
    }

    return output
  }
}

data class Computer(var aReg: Long, var bReg: Long, var cReg: Long, val operations: List<Int>) {
  companion object {
    fun fromInput(input: Sequence<String>): Computer {
      val data = input.toList()
      val aReg = data[0].split("A: ")[1].toLong()
      val bReg = data[1].split("B: ")[1].toLong()
      val cReg = data[2].split("C: ")[1].toLong()

      val program = data[4].split(": ")[1].split(",").map { it.toInt() }

      return Computer(aReg, bReg, cReg, program)
    }
  }
}
