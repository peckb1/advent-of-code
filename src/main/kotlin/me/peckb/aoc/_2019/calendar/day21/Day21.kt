package me.peckb.aoc._2019.calendar.day21

import kotlinx.coroutines.runBlocking
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer.Companion.asMutableMap
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer.Companion.operations
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

/**
 * Trial and Error Assembly code is ... not fun
 * So since I'm just catching up on years I skipped this day
 * https://todd.ginsberg.com/post/advent-of-code/2019/day21/
 */
class Day21 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val commands = listOf(
      "NOT A J", // If there is a hole one spot away, set the jump register to TRUE
                 // otherwise set it to FALSE
      "NOT B T", // If there is a hole two spots away, set the jump register to TRUE
                 //otherwise set it to FALSE
      "AND D T", // Set the temporary register to TRUE if there is a hole four spots away
                 // Note that the temporary register is FALSE when we start
      "OR T J",  // Set the jump register to TRUE if either the jump or temporary
                 // registers are TRUE, otherwise FALSE
      "NOT C T", // Set the temporary register to TRUE if there is a hole three spots away
      "OR T J",  // Set the jump register to TRUE if either the jump or temporary registers
                 // are TRUE, otherwise FALSE
      "AND D J", // If there is a not hole four spots away, and the jump register is true
                 // set the jump register to true
      "WALK",    // Execute the program
    ).flatMap { line ->
      line.map { it.code.toLong() }.plus('\n'.code.toLong())
    }

    executeCommands(input, commands)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val commands = listOf(
      "NOT C J", // If there is a hole three spots away, set the jump register to TRUE
                 // otherwise set it to FALSE
      "AND D J", // Set the jump register to true if the jump register is TRUE and there is
                 // not a hole four spots away
      "NOT H T", // Set the temporary register to true if there is a hole eight spots away
      "NOT T T", // Invert the temporary register if it is false
      "OR E T",  // If the temporary register is true or there is a not a hole five spots away
      "AND T J", // Set the jump register to true if it is already true and so is the temporary register
      "NOT A T", // Set the temporary register to true if there is a hole one spot away
      "OR T J",  // Set the jump register to true if it is already true or the temporary register is true
      "NOT B T", // Set the temporary register to true if there is a hole two spots away
      "NOT T T", // Invert the temporary register if it is false
      "OR E T",  // Set the temporary register to true if it is already true or there is a not a hole five spots away
      "NOT T T", // Invert the temporary register if it is false
      "OR T J",  // Set the jump register to TRUE if either the jump or temporary registers
                 // are TRUE, otherwise FALSE
      "RUN",     // Execute the program
    ).flatMap { line ->
      line.map { it.code.toLong() }.plus('\n'.code.toLong())
    }

    executeCommands(input, commands)
  }

  private fun executeCommands(input: String, commands: List<Long>): Long {
    val computer = IntcodeComputer()

    var commandIndex = 0
    var lastOutput = -1L
    runBlocking {
      computer.runProgram(
        operations = operations(input).asMutableMap(),
        userInput = { commands[commandIndex].also { commandIndex++ } },
        handleOutput = { lastOutput = it }
      )
    }

    return lastOutput
  }
}
