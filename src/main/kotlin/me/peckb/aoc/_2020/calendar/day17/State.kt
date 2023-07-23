package me.peckb.aoc._2020.calendar.day17

enum class State(val char: Char) {
  ACTIVE('#'),
  INACTIVE('.');

  companion object {
    fun fromChar(char: Char) = State.values().first { it.char == char }
  }
}
