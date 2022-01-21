package me.peckb.aoc._2018.calendar.day13

import me.peckb.aoc._2018.calendar.day13.Day13.FacingDirection.DOWN
import me.peckb.aoc._2018.calendar.day13.Day13.FacingDirection.LEFT
import me.peckb.aoc._2018.calendar.day13.Day13.FacingDirection.RIGHT
import me.peckb.aoc._2018.calendar.day13.Day13.FacingDirection.UP
import me.peckb.aoc._2018.calendar.day13.Day13.NextTurnDirection.STRAIGHT
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc._2018.calendar.day13.Day13.NextTurnDirection.LEFT as NEXT_LEFT
import me.peckb.aoc._2018.calendar.day13.Day13.NextTurnDirection.RIGHT as NEXT_RIGHT

class Day13 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val mines = mutableListOf<MutableList<Char>>()
    val mineCarts = mutableListOf<MineCart>()

    input.forEachIndexed { y, row ->
      val newRow = mutableListOf<Char>()
      row.forEachIndexed { x, c ->
        when (c) {
          '^' -> { newRow.add('|'); mineCarts.add(MineCart(y, x, UP)) }
          '>' -> { newRow.add('-'); mineCarts.add(MineCart(y, x, RIGHT)) }
          'v' -> { newRow.add('|'); mineCarts.add(MineCart(y, x, DOWN)) }
          '<' -> { newRow.add('-'); mineCarts.add(MineCart(y, x, LEFT)) }
          else -> newRow.add(c)
        }
      }
      mines.add(newRow)
    }

    var crash: Map.Entry<Pair<Int, Int>, List<MineCart>>? = null
    var time = 0

    crashLoop@ while(crash == null) {
      mineCarts.sortedBy { it.y }.sortedBy{ it.x }.forEach cartCheck@ { cart ->
        if (crash != null) return@cartCheck

        when (cart.direction) {
          UP -> cart.y--
          RIGHT -> cart.x++
          DOWN -> cart.y++
          LEFT -> cart.x--
        }
        when(mines[cart.y][cart.x]) {
          '+' -> {
            when (cart.nextDirection) {
              NEXT_LEFT -> { cart.direction = cart.direction.turnLeft(); cart.nextDirection = STRAIGHT }
              STRAIGHT -> { cart.nextDirection = NEXT_RIGHT }
              NEXT_RIGHT -> { cart.direction = cart.direction.turnRight(); cart.nextDirection = NEXT_LEFT }
            }
          }
          '\\' -> {
            when (cart.direction) {
              UP -> cart.direction = cart.direction.turnLeft()
              RIGHT -> cart.direction = cart.direction.turnRight()
              DOWN -> cart.direction = cart.direction.turnLeft()
              LEFT -> cart.direction = cart.direction.turnRight()
            }
          }
          '/' -> {
            when (cart.direction) {
              UP -> cart.direction = cart.direction.turnRight()
              RIGHT -> cart.direction = cart.direction.turnLeft()
              DOWN -> cart.direction = cart.direction.turnRight()
              LEFT -> cart.direction = cart.direction.turnLeft()
            }
          }
        }

        val locations = mineCarts.groupBy { (it.x to it.y) }
        crash = locations.entries.firstOrNull { it.value.size > 1 }
      }
      time ++
    }

    crash?.key
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val mines = mutableListOf<MutableList<Char>>()
    val mineCarts = mutableListOf<MineCart>()

    input.forEachIndexed { y, row ->
      val newRow = mutableListOf<Char>()
      row.forEachIndexed { x, c ->
        when (c) {
          '^' -> { newRow.add('|'); mineCarts.add(MineCart(y, x, UP)) }
          '>' -> { newRow.add('-'); mineCarts.add(MineCart(y, x, RIGHT)) }
          'v' -> { newRow.add('|'); mineCarts.add(MineCart(y, x, DOWN)) }
          '<' -> { newRow.add('-'); mineCarts.add(MineCart(y, x, LEFT)) }
          else -> newRow.add(c)
        }
      }
      mines.add(newRow)
    }

    while(mineCarts.size != 1) {
      val cartsToRemove = mutableListOf<MineCart>()
      mineCarts.sortedBy { it.y }.sortedBy{ it.x }.forEach cartCheck@ { cart ->
        if (cart.x == 0 && cart.y == 0) return@cartCheck

        when (cart.direction) {
          UP -> cart.y--
          RIGHT -> cart.x++
          DOWN -> cart.y++
          LEFT -> cart.x--
        }
        when(mines[cart.y][cart.x]) {
          '+' -> {
            when (cart.nextDirection) {
              NEXT_LEFT -> { cart.direction = cart.direction.turnLeft(); cart.nextDirection = STRAIGHT }
              STRAIGHT -> { cart.nextDirection = NEXT_RIGHT }
              NEXT_RIGHT -> { cart.direction = cart.direction.turnRight(); cart.nextDirection = NEXT_LEFT }
            }
          }
          '\\' -> {
            when (cart.direction) {
              UP -> cart.direction = cart.direction.turnLeft()
              RIGHT -> cart.direction = cart.direction.turnRight()
              DOWN -> cart.direction = cart.direction.turnLeft()
              LEFT -> cart.direction = cart.direction.turnRight()
            }
          }
          '/' -> {
            when (cart.direction) {
              UP -> cart.direction = cart.direction.turnRight()
              RIGHT -> cart.direction = cart.direction.turnLeft()
              DOWN -> cart.direction = cart.direction.turnRight()
              LEFT -> cart.direction = cart.direction.turnLeft()
            }
          }
        }

        val locations = mineCarts.groupBy { (it.x to it.y) }
        locations.filter { it.value.size > 1 }.forEach { (_, carts) ->
          carts.forEach {
            it.x = 0
            it.y = 0
          }
          cartsToRemove.addAll(carts)
        }
      }
      mineCarts.removeAll(cartsToRemove)
    }

    mineCarts.first().let { it.x to it.y }
  }

  data class MineCart(
    var y: Int,
    var x: Int,
    var direction: FacingDirection,
    var nextDirection: NextTurnDirection = NEXT_LEFT
  )

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

  enum class NextTurnDirection {
    LEFT, STRAIGHT, RIGHT
  }
}
