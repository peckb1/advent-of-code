package me.peckb.aoc._2022.calendar.day22

enum class Direction(private val dX: Int, private val dY: Int, val score: Int) {
  RIGHT(1, 0,0) {
    override fun turnLeft() = UP
    override fun turnRight() = DOWN
  },
  DOWN(0, 1, 1) {
    override fun turnLeft() = RIGHT
    override fun turnRight() = LEFT
  },
  LEFT(-1, 0, 2) {
    override fun turnLeft() = DOWN
    override fun turnRight() = UP
  },
  UP(0, -1, 3) {
    override fun turnLeft() = LEFT
    override fun turnRight() = RIGHT
  };

  abstract fun turnLeft(): Direction
  abstract fun turnRight(): Direction

  fun dX(x: Int) = x + dX
  fun dY(y: Int) = y + dY
}
