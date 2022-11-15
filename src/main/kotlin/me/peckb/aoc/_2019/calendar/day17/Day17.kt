package me.peckb.aoc._2019.calendar.day17

import kotlinx.coroutines.runBlocking
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer.Companion.asMutableMap
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalStateException

class Day17 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val cameraView = activateCamera(input)

    var alignmentParameter = 0
    cameraView.forEachIndexed { y, row ->
      row.forEachIndexed { x, imageChar ->
        if (imageChar == '#') {
          val n by lazy { if (y > 0) cameraView[y-1][x] else ' ' }
          val e by lazy { if (x < AREA_WIDTH - 1) cameraView[y][x+1] else ' ' }
          val s by lazy { if (y < AREA_HEIGHT - 1) cameraView[y+1][x] else ' ' }
          val w by lazy { if (x > 0) cameraView[y][x-1] else ' ' }
          if (n == '#' && e == '#' && s == '#' && w == '#') {
            alignmentParameter += x * y
            cameraView[y][x] = 'O'
          }
        }
      }
    }

    alignmentParameter
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val a = "L,12,L,12,R,4".map { it.code }
    val b = "R,10,R,6,R,4,R,4".map { it.code }
    val c = "R,6,L,12,L,12".map { it.code }
    val routine = "A,B,A,C,B,A,B,C,C,B".map { it.code }

    val dustCollected = runRobot(input, a, b, c, routine)

    dustCollected
  }

  private fun runRobot(input: String, A: List<Int>, B: List<Int>, C: List<Int>, routine: List<Int>): Long {
    val operations = IntcodeComputer.operations(input).asMutableMap()
      .also { it[0] = "2" }
    val computer = IntcodeComputer()

    var lastOutput = -1L
    var inputCursor = 0
    val inputs = listOf(routine, A, B, C, listOf('N'.code))
      .fold(mutableListOf<Int>()) { acc, nextData ->
        acc.addAll(nextData)
        acc.add('\n'.code)
        acc
      }

    runBlocking {
      computer.runProgram(
        operations = operations,
        userInput = { inputs[inputCursor].toLong().also { inputCursor++ } },
        handleOutput = { lastOutput = it }
      )
    }

    return lastOutput
  }

  private fun activateCamera(input: String): Array<Array<Char>> {
    val operations = IntcodeComputer.operations(input).asMutableMap()
    val computer = IntcodeComputer()

    val cameraView = Array(AREA_HEIGHT) { Array(AREA_WIDTH) { ' ' } }
    var x = 0
    var y = 0

    runBlocking {
      computer.runProgram(
        operations = operations,
        userInput = { throw IllegalStateException("No Input Expected") },
        handleOutput = { asciiLong ->
          if (asciiLong == 10L) {
            y++
            x = 0
          } else {
            cameraView[y][x] = asciiLong.toInt().toChar()
            x++
          }
        }
      )
    }

    return cameraView
  }

  companion object {
    private const val AREA_HEIGHT = 45
    private const val AREA_WIDTH = 37
  }
}
