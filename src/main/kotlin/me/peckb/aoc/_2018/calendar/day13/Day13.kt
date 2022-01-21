package me.peckb.aoc._2018.calendar.day13

import me.peckb.aoc._2018.calendar.day13.FacingDirection.DOWN
import me.peckb.aoc._2018.calendar.day13.FacingDirection.LEFT
import me.peckb.aoc._2018.calendar.day13.FacingDirection.RIGHT
import me.peckb.aoc._2018.calendar.day13.FacingDirection.UP
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day13 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (mines, mineCarts) = setup(input)

    var crash: Map.Entry<Pair<Int, Int>, List<MineCart>>? = null
    while (crash == null) {
      mineCarts.sortedBy { it.y }.sortedBy { it.x }.forEach cartCheck@{ cart ->
        if (crash != null) return@cartCheck

        cart.move(mines)

        val locations = mineCarts.groupBy { (it.x to it.y) }
        crash = locations.entries.firstOrNull { it.value.size > 1 }
      }
    }

    crash?.key
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (mines, mineCarts) = setup(input)

    while (mineCarts.size != 1) {
      val cartsToRemove = mutableListOf<MineCart>()
      mineCarts.sortedBy { it.y }.sortedBy { it.x }.forEach cartCheck@{ cart ->
        if (cart.x == 0 && cart.y == 0) return@cartCheck

        cart.move(mines)

        val locations = mineCarts.groupBy { (it.x to it.y) }
        locations.filter { it.value.size > 1 }.forEach { (_, carts) ->
          carts.forEach { it.x = 0; it.y = 0; }
          cartsToRemove.addAll(carts)
        }
      }
      mineCarts.removeAll(cartsToRemove)
    }

    mineCarts.first().let { it.x to it.y }
  }

  private fun setup(input: Sequence<String>): Pair<MutableList<MutableList<Char>>, MutableList<MineCart>> {
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

    return mines to mineCarts
  }
}
