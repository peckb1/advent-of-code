package me.peckb.aoc._2023.calendar.day16

import me.peckb.aoc._2023.calendar.day16.CaveArea.LaserState.Companion.continueLaser
import me.peckb.aoc._2023.calendar.day16.CaveArea.LaserState.Companion.stopLaser
import me.peckb.aoc._2023.calendar.day16.Direction.*

data class CaveArea(
  val symbol: Char,
  var eastEntranceEnergized: Boolean = false,
  var westEntranceEnergized: Boolean = false,
  var northEntranceEnergized: Boolean = false,
  var southEntranceEnergized: Boolean = false,
) {

  data class LaserState(
    val newDirections: List<Direction>,
    val stopLaser: Boolean,
    val newLaserDirection: Direction,
  ) {
    companion object {
      fun stopLaser(newDirections: List<Direction> = emptyList()) =
        LaserState(newDirections, true, STOP)

      fun continueLaser(direction: Direction) =
        LaserState(emptyList(), false, direction)
    }
  }

  fun isEnergized(): Boolean {
    return eastEntranceEnergized || westEntranceEnergized || northEntranceEnergized || southEntranceEnergized
  }

  fun reset() {
    eastEntranceEnergized = false
    westEntranceEnergized = false
    northEntranceEnergized = false
    southEntranceEnergized = false
  }

  fun enterGoing(direction: Direction): LaserState {
    return when (direction) {
      NORTH -> if (southEntranceEnergized) { stopLaser() } else {
        southEntranceEnergized = true
        when (symbol) {
          '-'      -> stopLaser(listOf(EAST, WEST))
          '.', '|' -> continueLaser(direction)
          '/'      -> continueLaser(EAST)
          '\\'     -> continueLaser(WEST)
          else     -> throw IllegalStateException("Unknown direction [$symbol]")
        }
      }

      SOUTH -> if (northEntranceEnergized) { stopLaser(emptyList()) } else {
        northEntranceEnergized = true
        when (symbol) {
          '-'      -> stopLaser(listOf(EAST, WEST))
          '.', '|' -> continueLaser(direction)
          '/'      -> continueLaser(WEST)
          '\\'     -> continueLaser(EAST)
          else     -> throw IllegalStateException("Unknown direction [$symbol]")
        }
      }

      EAST -> if (westEntranceEnergized) { stopLaser(emptyList()) } else {
        westEntranceEnergized = true
        when (symbol) {
          '|'      -> stopLaser(listOf(NORTH, SOUTH))
          '.', '-' -> continueLaser(direction)
          '/'      -> continueLaser(NORTH)
          '\\'     -> continueLaser(SOUTH)
          else     -> throw IllegalStateException("Unknown direction [$symbol]")
        }
      }

      WEST -> if (eastEntranceEnergized) { stopLaser(emptyList()) } else {
        eastEntranceEnergized = true
        when (symbol) {
          '|'      -> stopLaser(listOf(NORTH, SOUTH))
          '.', '-' -> continueLaser(direction)
          '/'      -> continueLaser(SOUTH)
          '\\'     -> continueLaser(NORTH)
          else     -> throw IllegalStateException("Unknown direction [$symbol]")
        }
      }

      else -> throw IllegalStateException("Unknown travel Direction $direction")
    }
  }

  override fun toString(): String = if (isEnergized()) { "#" } else { "." }
}
