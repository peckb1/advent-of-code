package me.peckb.aoc._2019.calendar.day11

import kotlinx.coroutines.runBlocking
import me.peckb.aoc._2019.calendar.day11.Day11.Color.*
import me.peckb.aoc._2019.calendar.day11.Day11.Direction.*
import me.peckb.aoc._2019.calendar.day11.Day11.State.*
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer.Companion.asMutableMap
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalArgumentException
import kotlin.math.max
import kotlin.math.min

class Day11 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val operations = operations(input).asMutableMap()
    val paintedHull = operations.paint(initialColor = BLACK)
    paintedHull.size
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val operations = operations(input).asMutableMap()
    val paintedHull = operations.paint(initialColor = WHITE)

    var minX = Int.MAX_VALUE
    var minY = Int.MAX_VALUE
    var maxX = Int.MIN_VALUE
    var maxY = Int.MIN_VALUE

    paintedHull.forEach { (location, color) ->
      minX = min(minX, location.x)
      minY = min(minY, location.y)
      maxX = max(maxX, location.x)
      maxY = max(maxY, location.y)
    }

    (minY..maxY).joinToString(("\n")) { y ->
      (minX..maxX).joinToString("") { x ->
        paintedHull.getOrDefault(Location(x, y), BLACK).color
      }
    }
  }
  
  private fun operations(line: String) = line.split(",")

  private fun MutableMap<Long, String>.paint(initialColor: Day11.Color): MutableMap<Location, Color> {
    val operations = this
    val hull = mutableMapOf<Location, Color>().apply { set(Location(0, 0), initialColor) }
    val computer = IntcodeComputer()

    var robotLocation = Location(0, 0)
    var robotDirection = UP
    var outputState = COLOR

    runBlocking {
      computer.runProgram(
        operations = operations,
        userInput = { hull.getOrDefault(robotLocation, BLACK).code },
        handleOutput = { data ->
          when (outputState) {
            COLOR -> {
              hull[robotLocation] = Color.fromCode(data)
              outputState = DIRECTION
            }
            DIRECTION -> {
              when (data) {
                TURN_LEFT -> robotDirection = robotDirection.turnLeft()
                TURN_RIGHT -> robotDirection = robotDirection.turnRight()
              }
              robotLocation = robotLocation.move(robotDirection)
              outputState = COLOR
            }
          }
        }
      )
    }

    return hull
  }

  enum class State { COLOR, DIRECTION }

  data class Location(val x: Int, val y: Int) {
    fun move(robotDirection: Direction): Location {
      return when (robotDirection) {
        UP -> copy(y = y - 1)
        RIGHT -> copy(x = x + 1)
        DOWN -> copy(y = y + 1)
        LEFT -> copy(x = x - 1)
      }
    }
  }

  enum class Color(val color: String, val code: Long) {
    BLACK(".", 0),
    WHITE("#", 1);

    companion object {
      fun fromCode(code: Long): Color {
        when (code) {
          BLACK.code -> return BLACK
          WHITE.code -> return WHITE
        }
        throw IllegalArgumentException("Invalid Color Code")
      }
    }
  }

  enum class Direction {
    UP, RIGHT, DOWN, LEFT;

    fun turnLeft(): Direction {
      return when (this) {
        UP -> LEFT
        LEFT -> DOWN
        DOWN -> RIGHT
        RIGHT -> UP
      }
    }

    fun turnRight(): Direction {
      return when (this) {
        UP -> RIGHT
        RIGHT -> DOWN
        DOWN -> LEFT
        LEFT -> UP
      }
    }
  }

  companion object {
    private const val TURN_LEFT = 0L
    private const val TURN_RIGHT = 1L
  }
}

