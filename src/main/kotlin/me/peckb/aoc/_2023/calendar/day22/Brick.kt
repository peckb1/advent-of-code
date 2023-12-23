package me.peckb.aoc._2023.calendar.day22

import me.peckb.aoc._2023.calendar.day22.Shape.*

data class Brick(val start: Position, val end: Position) {
  private val xRange = start.x .. end.x

  private val yRange = start.y .. end.y

  fun clone() = Brick(start.copy(), end.copy())

  fun fall(index: Int, bricks: List<Brick>) : Boolean {
    val myBrick = bricks[index]

    val bricksThatHaveAlreadyFallen = if (index == 0) { emptyList() } else {
      bricks.subList(0, index).sortedBy { it.end.z }
    }

    val brickToLandOn = bricksThatHaveAlreadyFallen.asReversed().firstOrNull { myBrick.wouldLandOn(it) }
    val newZ = (brickToLandOn?.end?.z ?: 0) + 1
    val zToDrop = start.z - newZ

    drop(zToDrop)

    return zToDrop != 0
  }

  fun canBeDeleted(
    bricksThatRelyOnMe: List<Brick>,
    brickToSupportsMap: MutableMap<Brick, List<Brick>>
  ): Boolean {
    if (bricksThatRelyOnMe.isEmpty()) { return true }

    return bricksThatRelyOnMe.all { brick ->
      (brickToSupportsMap[brick]?.filter { it != this } ?: emptyList()).isNotEmpty()
    }
  }

  private fun wouldLandOn(b: Brick): Boolean {
    val xPointOverlap    by lazy { (b.xRange).contains(start.x) }
    val yPointOverlap    by lazy { (b.yRange).contains(start.y) }
    val xIntervalOverlap by lazy { (b.start.x <= end.x && b.end.x >= start.x) }
    val yIntervalOverlap by lazy { (b.start.y <= end.y && b.end.y >= start.y) }

    return when (shape) {
      VERTICAL, CUBE -> xPointOverlap && yPointOverlap
      HORIZONTAL_X   -> yPointOverlap && xIntervalOverlap
      HORIZONTAL_Y   -> xPointOverlap && yIntervalOverlap
    }
  }

  private fun drop(deltaZ: Int) {
    start.drop(deltaZ)
    end.drop(deltaZ)
  }

  private val shape: Shape = when {
    start.z != end.z -> VERTICAL
    start.x != end.x -> HORIZONTAL_X
    start.y != end.y -> HORIZONTAL_Y
    else             -> CUBE
  }
}
