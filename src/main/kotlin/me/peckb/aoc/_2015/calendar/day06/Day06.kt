package me.peckb.aoc._2015.calendar.day06

import me.peckb.aoc._2015.calendar.day06.Day06.Action.TOGGLE
import me.peckb.aoc._2015.calendar.day06.Day06.Action.TURN_OFF
import me.peckb.aoc._2015.calendar.day06.Day06.Action.TURN_ON
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.math.max

class Day06 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::instruction) { input ->
    val lights = Array(1000) { Array(1000) { false } }

    input.forEach {
      (it.UL.first .. it.LR.first).forEach { y ->
        (it.UL.second .. it.LR.second).forEach { x ->
          when (it.action) {
            TURN_ON -> lights[y][x] = true
            TURN_OFF -> lights[y][x] = false
            TOGGLE -> lights[y][x] = !lights[y][x]
          }
        }
      }
    }

    lights.sumOf { row -> row.count { it } }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::instruction) { input ->
    val lights = Array(1000) { Array(1000) { 0 } }

    input.forEach {
      (it.UL.first .. it.LR.first).forEach { y ->
        (it.UL.second .. it.LR.second).forEach { x ->
          when (it.action) {
            TURN_ON -> lights[y][x] += 1
            TURN_OFF -> lights[y][x] = max(0, lights[y][x] - 1)
            TOGGLE -> lights[y][x] += 2
          }
        }
      }
    }

    lights.sumOf { row -> row.sumOf { it } }
  }

  private fun instruction(line: String) : Instruction {
    val parts = line.split(" ")

    val action = Action.fromInput(parts.take(2))
    val (ULy, URx) = parts.takeLast(3).first().split(",").map(String::toInt)
    val (LRy, LRx) = parts.last().split(",").map(String::toInt)

    return Instruction(action, ULy to URx, LRy to LRx)
  }

  enum class Action {
    TURN_ON, TURN_OFF, TOGGLE;

    companion object {
      fun fromInput(actionStrings: List<String>): Action {
        return when (actionStrings[0]) {
          "toggle" -> TOGGLE
          else -> when (actionStrings[1]) {
            "on" -> TURN_ON
            "off" -> TURN_OFF
            else -> throw IllegalArgumentException("Unexpected Action ${actionStrings.joinToString(" ")}")
          }
        }
      }
    }
  }

  data class Instruction(val action: Action, val UL: Pair<Int, Int>, val LR: Pair<Int, Int>)
}
