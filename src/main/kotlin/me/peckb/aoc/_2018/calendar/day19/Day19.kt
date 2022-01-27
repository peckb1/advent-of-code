package me.peckb.aoc._2018.calendar.day19

import me.peckb.aoc._2018.calendar.day16.Instruction.*
import me.peckb.aoc._2018.calendar.day16.Instruction
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.sqrt

class Day19 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val registers = mutableListOf(0, 0, 0, 0, 0, 0)
    val (registerIndex, instructions) = setup(input)

    while(registers[registerIndex] in (instructions.indices)) {
      val instruction = instructions[registers[registerIndex]]
      registers[instruction.c] = instruction.performAction(registers)
      registers[registerIndex] = registers[registerIndex] + 1
    }

    registers[0]
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val registers = mutableListOf(1, 0, 0, 0, 0, 0)
    val (registerIndex, instructions) = setup(input)

    while(registers[registerIndex] != 1) {
      val instruction = instructions[registers[registerIndex]]
      registers[instruction.c] = instruction.performAction(registers)
      registers[registerIndex] = registers[registerIndex] + 1
    }

    1 + primeFactors(registers[2]).toMutableSet().apply { add(registers[2]) }.sum()
  }

  private fun setup(input: Sequence<String>): Pair<Int, List<Instruction>> {
    var registerIndex = 0

    val instructions = input.mapNotNull {
      val parts = it.split(" ")

      when (parts[0]) {
        "addr" -> AddRegister(parts[1].toInt(), parts[2].toInt(), parts[3].toInt())
        "addi" -> AddImmediate(parts[1].toInt(), parts[2].toInt(), parts[3].toInt())
        "mulr" -> MultiplyRegister(parts[1].toInt(), parts[2].toInt(), parts[3].toInt())
        "muli" -> MultipleImmediate(parts[1].toInt(), parts[2].toInt(), parts[3].toInt())
        "banr" -> BitwiseAndRegister(parts[1].toInt(), parts[2].toInt(), parts[3].toInt())
        "bani" -> BitwiseAndImmediate(parts[1].toInt(), parts[2].toInt(), parts[3].toInt())
        "borr" -> BitwiseOrRegister(parts[1].toInt(), parts[2].toInt(), parts[3].toInt())
        "bori" -> BitwiseOrImmediate(parts[1].toInt(), parts[2].toInt(), parts[3].toInt())
        "setr" -> AssignmentRegister(parts[1].toInt(), parts[2].toInt(), parts[3].toInt())
        "seti" -> AssignmentImmediate(parts[1].toInt(), parts[2].toInt(), parts[3].toInt())
        "gtir" -> GreaterThanImmediateRegister(parts[1].toInt(), parts[2].toInt(), parts[3].toInt())
        "gtri" -> GreaterThanRegisterImmediate(parts[1].toInt(), parts[2].toInt(), parts[3].toInt())
        "gtrr" -> GreaterThanRegisterRegister(parts[1].toInt(), parts[2].toInt(), parts[3].toInt())
        "eqir" -> EqualImmediateRegister(parts[1].toInt(), parts[2].toInt(), parts[3].toInt())
        "eqri" -> EqualRegisterImmediate(parts[1].toInt(), parts[2].toInt(), parts[3].toInt())
        "eqrr" -> EqualRegisterRegister(parts[1].toInt(), parts[2].toInt(), parts[3].toInt())
        "#ip" -> null.also {
          registerIndex = parts[1].toInt()
        }
        else -> throw IllegalArgumentException("Unknown Instruction state")
      }
    }.toList()

    return registerIndex to instructions
  }

  private fun primeFactors(number: Int): ArrayList<Int> {
    val arr: ArrayList<Int> = arrayListOf()
    var n = number
    while (n % 2 == 0) {
      arr.add(2)
      n /= 2
    }
    val squareRoot = sqrt(n.toDouble()).toInt()

    for (i in 3..squareRoot step 2) {
      while (n % i == 0) {
        arr.add(i)
        n /= i
      }
    }

    if (n > 2) arr.add(n)

    return arr
  }
}
