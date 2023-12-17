package me.peckb.aoc._2023.calendar.day17;

enum class Direction {
  NORTH { override fun turnDirections() = mutableListOf(EAST, WEST)   },
  SOUTH { override fun turnDirections() = mutableListOf(EAST, WEST)   },
  EAST  { override fun turnDirections() = mutableListOf(NORTH, SOUTH) },
  WEST  { override fun turnDirections() = mutableListOf(NORTH, SOUTH) };

  abstract fun turnDirections(): MutableList<Direction>
}
