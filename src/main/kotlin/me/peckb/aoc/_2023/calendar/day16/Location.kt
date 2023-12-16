package me.peckb.aoc._2023.calendar.day16

import me.peckb.aoc._2023.calendar.day16.Direction.*

data class Location(var row: Int, var col: Int) {
  fun travel(direction: Direction) {
    when (direction) {
      NORTH -> row--
      SOUTH -> row++
      EAST  -> col++
      WEST  -> col--
      else  -> throw IllegalStateException("Unknown Travel Direction $direction")
    }
  }
}
