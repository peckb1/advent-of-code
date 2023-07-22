package me.peckb.aoc._2020.calendar.day12

import me.peckb.aoc._2020.calendar.day12.Day12.Action.*
import me.peckb.aoc._2020.calendar.day12.Day12.Direction.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalStateException
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
      when (it.action) {
        NORTH -> northSouthPosition -= it.value
        EAST -> eastWestPosition += it.value
        SOUTH -> northSouthPosition += it.value
        WEST -> eastWestPosition -= it.value
        LEFT -> {
          when (it.value) {
            90 -> facingDirection = when (facingDirection) {
              N -> W
              E -> N
              S -> E
              W -> S
            }

            180 -> facingDirection = when (facingDirection) {
              N -> S
              E -> W
              S -> N
              W -> E
            }

            270 -> facingDirection = when (facingDirection) {
              N -> E
              E -> S
              S -> W
              W -> N
            }

            else -> throw IllegalStateException("Cannot turn to face a non-cardinal direction")
          }
        }

        RIGHT -> when (it.value) {
          90 -> facingDirection = when (facingDirection) {
            N -> E
            E -> S
            S -> W
            W -> N
          }

          180 -> facingDirection = when (facingDirection) {
            N -> S
            E -> W
            S -> N
            W -> E
          }

          270 -> facingDirection = when (facingDirection) {
            N -> W
            E -> N
            S -> E
            W -> S
          }

          else -> throw IllegalStateException("Cannot turn to face a non-cardinal direction")
        }

        FORWARD -> when (facingDirection) {
          N -> northSouthPosition -= it.value
          E -> eastWestPosition += it.value
          S -> northSouthPosition += it.value
          W -> eastWestPosition -= it.value
        }
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
      when (it.action) {
        NORTH -> relativeWaypointNorthSouth -= it.value
        EAST -> relativeWaypointEastWest += it.value
        SOUTH -> relativeWaypointNorthSouth += it.value
        WEST -> relativeWaypointEastWest -= it.value
        LEFT -> {
          when (it.value) {
            90 -> {
              val tempNorthSouth = relativeWaypointNorthSouth
              relativeWaypointNorthSouth = -relativeWaypointEastWest
              relativeWaypointEastWest = tempNorthSouth
            }

            180 -> {
              relativeWaypointNorthSouth = 0 - relativeWaypointNorthSouth
              relativeWaypointEastWest = 0 - relativeWaypointEastWest
            }

            270 -> {
              val tempNorthSouth = relativeWaypointNorthSouth
              relativeWaypointNorthSouth = relativeWaypointEastWest
              relativeWaypointEastWest = -tempNorthSouth
            }
          }
        }

        RIGHT -> when (it.value) {
          90 -> {
            val tempNorthSouth = relativeWaypointNorthSouth
            relativeWaypointNorthSouth = relativeWaypointEastWest
            relativeWaypointEastWest = -tempNorthSouth
          }

          180 -> {
            relativeWaypointNorthSouth = 0 - relativeWaypointNorthSouth
            relativeWaypointEastWest = 0 - relativeWaypointEastWest
          }

          270 -> {
            val tempNorthSouth = relativeWaypointNorthSouth
            relativeWaypointNorthSouth = -relativeWaypointEastWest
            relativeWaypointEastWest = tempNorthSouth
          }
        }

        FORWARD -> {
          northSouthPosition += (it.value * relativeWaypointNorthSouth)
          eastWestPosition += (it.value * relativeWaypointEastWest)
        }
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
