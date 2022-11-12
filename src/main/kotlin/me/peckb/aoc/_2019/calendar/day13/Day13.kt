package me.peckb.aoc._2019.calendar.day13

import kotlinx.coroutines.runBlocking
import me.peckb.aoc._2019.calendar.day13.Day13.Mode.*
import me.peckb.aoc._2019.calendar.day13.Day13.Tile.*
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer.Companion.asMutableMap
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer.Companion.operations
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalStateException
import kotlin.math.max

class Day13 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val operations = operations(input).asMutableMap()
    val computer = IntcodeComputer()

    var blockCount = 0
    var maxX = 0L
    var maxY = 0L
    runBlocking {
      var outputMode = X_POSITION
      computer.runProgram(
        operations = operations,
        userInput = { throw IllegalStateException("No Input expected") },
        handleOutput = { data ->
          when (outputMode) {
            X_POSITION -> { maxX = max(maxX, data) }
            Y_POSITION -> { maxY = max(maxY, data) }
            TILE_ID -> {
              when (Tile.fromData(data)) {
                EMPTY -> { /* no op for part one */ }
                WALL -> { /* no op for part one */ }
                BLOCK -> { blockCount++ }
                HORIZONTAL_PADDLE -> { /* no op for part one */ }
                BALL -> { /* no op for part one */ }
              }
            }
          }
          outputMode = outputMode.next()
        }
      )
    }

    blockCount
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val computer = IntcodeComputer()
    val operations = operations(input).asMutableMap()
      .also { it[0] = "2" }

    // These values are "captured" from part one ;)
    val screen = Array(24) { Array(42) { EMPTY } }
    var score = 0L
    runBlocking {
      var outputMode = X_POSITION
      var (lastX, lastY) = -1 to -1
      var paddleX = -1
      var ballX = -1
      computer.runProgram(
        operations = operations,
        userInput = { ballX.compareTo(paddleX).toLong() },
        handleOutput = {data ->
          when (outputMode) {
            X_POSITION -> lastX = data.toInt()
            Y_POSITION -> lastY = data.toInt()
            TILE_ID -> {
              if (lastX == -1 && lastY == 0) {
                score = data
              } else {
                val tile = Tile.fromData(data)
                if (tile == BALL) {
                  ballX = lastX
                } else if (tile == HORIZONTAL_PADDLE) {
                  paddleX = lastX
                }
                screen[lastY][lastX] = tile
              }
            }
          }
          outputMode = outputMode.next()
        }
      )
    }

    score
  }

  enum class Mode {
    X_POSITION { override fun next() = Y_POSITION },
    Y_POSITION { override fun next() = TILE_ID },
    TILE_ID { override fun next() = X_POSITION };

    abstract fun next():  Mode
  }

  enum class Tile(val tileId: Long, val representation: String) {
    EMPTY(0, " "),
    WALL(1, "|"),
    BLOCK(2, "#"),
    HORIZONTAL_PADDLE(3, "-"),
    BALL(4, "*");

    companion object {
      fun fromData(data: Long): Tile = values().first { it.tileId == data }
    }
  }
}
