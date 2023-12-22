package me.peckb.aoc._2023.calendar.day22

import me.peckb.aoc._2023.calendar.day22.Shape.*
import kotlin.math.max
import kotlin.math.min

data class Brick(val start: Position, val end: Position) {
  fun fall(index: Int, bricks: List<Brick>) : Boolean {
    val me = bricks[index]

    val bricksThatHaveAlreadyFallen = if (index == 0) { emptyList() } else {
      bricks.subList(0, index).sortedBy { maxOf(it.start.z, it.end.z) }
    }

    val currentMinZ = min(start.z, end.z)

    val myNewBottom = bricksThatHaveAlreadyFallen.asReversed().firstOrNull { b ->
      val xPointOverlap    by lazy { (b.start.x..b.end.x).contains(me.start.x) }
      val yPointOverlap    by lazy { (b.start.y..b.end.y).contains(me.start.y) }
      val xIntervalOverlap by lazy { (b.start.x <= me.end.x && b.end.x >= me.start.x) }
      val yIntervalOverlap by lazy { (b.start.y <= me.end.y && b.end.y >= me.start.y) }

      val (xOverlap, yOverlap) = when (me.shape) {
        VERTICAL, CUBE -> xPointOverlap to yPointOverlap
        HORIZONTAL_X   -> yPointOverlap to xIntervalOverlap
        HORIZONTAL_Y   -> xPointOverlap to yIntervalOverlap
      }

      xOverlap && yOverlap
    }

    val newZ = (myNewBottom?.let { max(it.start.z, it.end.z) } ?: 0) + 1
    val zToDrop = currentMinZ - newZ

    start.drop(zToDrop)
    end.drop(zToDrop)

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

  private val shape: Shape = when {
    start.z != end.z -> VERTICAL
    start.x != end.x -> HORIZONTAL_X
    start.y != end.y -> HORIZONTAL_Y
    else             -> CUBE
  }
}
