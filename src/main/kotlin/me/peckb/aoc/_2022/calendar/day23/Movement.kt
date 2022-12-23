package me.peckb.aoc._2022.calendar.day23

sealed class Movement(val exclusionPositions: List<Day23.Delta>) {
  abstract val next: Movement
  abstract val movements: List<Movement>

  fun move(x: Int, y: Int): Pair<Int, Int> {
    return when (this) {
      East  -> x + 1 to y
      North -> x to y - 1
      South -> x to y + 1
      West  -> x - 1 to y
    }
  }

  object North : Movement(
    listOf(Day23.Delta(-1, -1), Day23.Delta(0, -1), Day23.Delta(1, -1))
  ) {
    override val next: Movement = South
    override val movements = listOf(North, South, West, East)
  }

  object South : Movement(
    listOf(Day23.Delta(-1, 1), Day23.Delta(0, 1), Day23.Delta(1, 1))
  ) {
    override val next: Movement = West
    override val movements = listOf(South, West, East, North)
  }

  object West : Movement(
    listOf(Day23.Delta(-1, -1), Day23.Delta(-1, 0), Day23.Delta(-1, 1))
  ) {
    override val next: Movement = East
    override val movements = listOf(West, East, North, South)
  }

  object East : Movement(
    listOf(Day23.Delta(1, -1), Day23.Delta(1, 0), Day23.Delta(1, 1))
  ) {
    override val next: Movement = North
    override val movements = listOf(East, North, South, West)
  }
}
