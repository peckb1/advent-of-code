package me.peckb.aoc._2022.calendar.day10

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalArgumentException
import java.lang.StringBuilder
import kotlin.math.abs

class Day10 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::instruction) { input ->
    val specificSumCycles = hashSetOf(20, 60, 100, 140, 180, 220)
    var specificSum = 0

    runProgram(input) { (cycle, xRegister) ->
      if (specificSumCycles.contains(cycle)) specificSum += cycle * xRegister
    }

    specificSum
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::instruction) { input ->
    var xPosition = 0
    val crtOutput = StringBuilder()

    runProgram(input) { (cycle, xRegister) ->
      if (abs(xPosition - xRegister) <= 1) crtOutput.append("#") else crtOutput.append(" ")
      if (cycle % SCREEN_WIDTH == 0) {
        crtOutput.append("\n")
        xPosition = 0
      } else {
        xPosition++
      }
    }

    // DEV NOTE: drop the last "\n" we added to make our test comparison nicer
    crtOutput.dropLast(1)
  }

  private fun runProgram(programInput: Sequence<Instruction>, cycleHandler: (CycleData) -> Unit) {
    var xRegister = 1
    val valuesToAddToRegister = ArrayDeque<Int>()

    var cycle = 0

    fun advanceCycle() {
      cycle++
      cycleHandler(CycleData(cycle, xRegister))
      xRegister += valuesToAddToRegister.removeFirst()
    }

    programInput.forEach { instruction ->
      valuesToAddToRegister.add(0)
      if (instruction is Instruction.AddX) valuesToAddToRegister.add(instruction.v)
      advanceCycle()
    }

    while (valuesToAddToRegister.isNotEmpty()) {
      advanceCycle()
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

  data class CycleData(val cycle: Int, val xRegister: Int)

  companion object {
    private const val SCREEN_WIDTH = 40
  }
}
