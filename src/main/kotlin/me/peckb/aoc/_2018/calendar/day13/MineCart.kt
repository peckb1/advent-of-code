package me.peckb.aoc._2018.calendar.day13

import me.peckb.aoc._2018.calendar.day13.FacingDirection.DOWN
import me.peckb.aoc._2018.calendar.day13.FacingDirection.LEFT
import me.peckb.aoc._2018.calendar.day13.FacingDirection.RIGHT
import me.peckb.aoc._2018.calendar.day13.FacingDirection.UP
import me.peckb.aoc._2018.calendar.day13.NextTurnDirection.STRAIGHT
import me.peckb.aoc._2018.calendar.day13.NextTurnDirection.LEFT as NEXT_LEFT
import me.peckb.aoc._2018.calendar.day13.NextTurnDirection.RIGHT as NEXT_RIGHT

data class MineCart(
  var y: Int,
  var x: Int,
  var direction: FacingDirection,
  var nextDirection: NextTurnDirection = NEXT_LEFT
) {
  fun move(mines: MutableList<MutableList<Char>>) {
    when (direction) {
      UP -> y--
      RIGHT -> x++
      DOWN -> y++
      LEFT -> x--
    }
    when(mines[y][x]) {
      '+' -> {
        when (nextDirection) {
          NEXT_LEFT -> { direction = direction.turnLeft(); nextDirection = STRAIGHT }
          STRAIGHT -> { nextDirection = NEXT_RIGHT }
          NEXT_RIGHT -> { direction = direction.turnRight(); nextDirection = NEXT_LEFT}
        }
      }
      '\\' -> {
        direction = when (direction) {
          UP -> direction.turnLeft()
          RIGHT -> direction.turnRight()
          DOWN -> direction.turnLeft()
          LEFT -> direction.turnRight()
        }
      }
      '/' -> {
        direction = when (direction) {
          UP -> direction.turnRight()
          RIGHT -> direction.turnLeft()
          DOWN -> direction.turnRight()
          LEFT -> direction.turnLeft()
        }
      }
    }
  }
}
