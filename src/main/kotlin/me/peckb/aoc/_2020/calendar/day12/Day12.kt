package me.peckb.aoc._2020.calendar.day12

import me.peckb.aoc._2020.calendar.day12.Day12.Action.*
import me.peckb.aoc._2020.calendar.day12.Day12.Direction.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.abs

class Day12 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  data class Navigation(val action: Action, val value: Int)

  enum class Action(private val key: String) {
    NORTH("N"),
    EAST("E"),
    SOUTH("S"),
    WEST("W"),
    LEFT("L"),
    RIGHT("R"),
    FORWARD("F");

    companion object {
      fun fromKey(key: String) = Action.values().first { it.key == key }
    }
  }

  enum class Direction {
    N, E, S, W
  }

  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::instruction) { input ->
    var facingDirection = E
    var northSouthPosition = 0
    var eastWestPosition = 0

    input.forEach {
      if (it.action == NORTH) northSouthPosition -= it.value
      else if (it.action == EAST) eastWestPosition += it.value
      else if (it.action == SOUTH) northSouthPosition += it.value
      else if (it.action == WEST) eastWestPosition -= it.value
      else if (it.action == FORWARD) when (facingDirection) {
        N -> northSouthPosition -= it.value
        E -> eastWestPosition += it.value
        S -> northSouthPosition += it.value
        W -> eastWestPosition -= it.value
      }
      else if (it.value == 180) facingDirection = when (facingDirection) {
        N -> S
        E -> W
        S -> N
        W -> E
      }
      else if ((it.action == LEFT && it.value == 90) || (it.action == RIGHT && it.value == 270)) facingDirection = when (facingDirection) {
        N -> W
        E -> N
        S -> E
        W -> S
      }
      else // (it.action == RIGHT && it.value == 90) || (it.action == LEFT && it.value == 270)
        facingDirection = when (facingDirection) {
          N -> E
          E -> S
          S -> W
          W -> N
        }
    }

    abs(northSouthPosition) + abs(eastWestPosition)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::instruction) { input ->
    var northSouthPosition = 0L
    var eastWestPosition = 0L
    var relativeWaypointEastWest = 10L
    var relativeWaypointNorthSouth = -1L

    input.forEach {
      if (it.action == NORTH) relativeWaypointNorthSouth -= it.value
      else if (it.action == EAST) relativeWaypointEastWest += it.value
      else if (it.action == SOUTH) relativeWaypointNorthSouth += it.value
      else if (it.action == WEST) relativeWaypointEastWest -= it.value
      else if (it.action == FORWARD) {
        northSouthPosition += (it.value * relativeWaypointNorthSouth)
        eastWestPosition += (it.value * relativeWaypointEastWest)
      }
      else if (it.value == 180) {
        relativeWaypointNorthSouth = 0 - relativeWaypointNorthSouth
        relativeWaypointEastWest = 0 - relativeWaypointEastWest
      }
      else if ((it.action == LEFT && it.value == 90) || (it.action == RIGHT && it.value == 270)) {
        val tempNorthSouth = relativeWaypointNorthSouth
        relativeWaypointNorthSouth = -relativeWaypointEastWest
        relativeWaypointEastWest = tempNorthSouth
      }
      else { // (it.action == RIGHT && it.value == 90) || (it.action == LEFT && it.value == 270)
        val tempNorthSouth = relativeWaypointNorthSouth
        relativeWaypointNorthSouth = relativeWaypointEastWest
        relativeWaypointEastWest = -tempNorthSouth
      }
    }

    abs(northSouthPosition) + abs(eastWestPosition)
  }

  private fun instruction(line: String): Navigation {
    val action = Action.fromKey(line.take(1))
    val value = line.drop(1).toInt()

    return Navigation(action, value)
  }
}
