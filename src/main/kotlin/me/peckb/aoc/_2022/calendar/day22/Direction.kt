package me.peckb.aoc._2022.calendar.day22

enum class Direction(val score: Int) {
  LEFT(2) {
    override fun turnLeft() = DOWN
    override fun turnRight() = UP
  },
  RIGHT(0) {
    override fun turnLeft() = UP
    override fun turnRight() = DOWN
  },
  UP(3) {
    override fun turnLeft() = LEFT
    override fun turnRight() = RIGHT
  },
  DOWN(1) {
    override fun turnLeft() = RIGHT
    override fun turnRight() = LEFT
  };

  abstract fun turnLeft(): Direction
  abstract fun turnRight(): Direction
}
