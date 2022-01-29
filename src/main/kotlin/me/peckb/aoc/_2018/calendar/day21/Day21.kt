package me.peckb.aoc._2018.calendar.day21

import me.peckb.aoc._2018.calendar.day16.Instruction
import me.peckb.aoc._2018.calendar.day16.Instruction.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day21 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val registers = mutableListOf(0, 0, 0, 0, 0, 0)
    val (registerIndex, instructions) = setup(input)

    var firstStopper: Int? = null
    while(firstStopper == null && registers[registerIndex] in (instructions.indices)) {
      val instruction = instructions[registers[registerIndex]]
      if (instruction.a == 4 && instruction.b == 0 && instruction.c == 2) {
        firstStopper = registers[4]
      }
      registers[instruction.c] = instruction.performAction(registers)
      registers[registerIndex] = registers[registerIndex] + 1
    }

    firstStopper
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val registers = mutableListOf(0, 0, 0, 0, 0, 0)
    val (registerIndex, instructions) = setup(input)

    var lastStopperBeforeRepeat: Int? = null
    val thingsThatStopUs = mutableSetOf<Int>()

    while(lastStopperBeforeRepeat == null && registers[registerIndex] in (instructions.indices)) {
      val instruction = instructions[registers[registerIndex]]
      if (instruction.a == 4 && instruction.b == 0 && instruction.c == 2) {
        if(thingsThatStopUs.contains(registers[4])) {
          lastStopperBeforeRepeat = thingsThatStopUs.last()
        } else {
          thingsThatStopUs.add(registers[4])
        }
      }
      registers[instruction.c] = instruction.performAction(registers)
      registers[registerIndex] = registers[registerIndex] + 1
    }

    lastStopperBeforeRepeat
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
}
