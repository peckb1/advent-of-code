package me.peckb.aoc._2018.calendar.day13

enum class FacingDirection {
  UP, RIGHT, DOWN, LEFT;

  fun turnLeft(): FacingDirection {
    return when (this) {
      UP -> LEFT
      LEFT -> DOWN
      DOWN -> RIGHT
      RIGHT -> UP
    }
  }

  fun turnRight(): FacingDirection {
    return when (this) {
      UP -> RIGHT
      RIGHT -> DOWN
      DOWN -> LEFT
      LEFT -> UP
    }
  }
}
