package me.peckb.aoc._2016.calendar.day13

import me.peckb.aoc._2016.calendar.day11.GenericIntDijkstra
import me.peckb.aoc._2016.calendar.day11.GenericIntDijkstra.DijkstraNode
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day13 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { favouriteNumberString ->
    val num = favouriteNumberString.toInt()
    val office = Array(OFFICE_SIZE) { Array(OFFICE_SIZE) { OPEN } }.build(num)
    val start = Space(1, 1).withOffice(office)
    val solutions = OfficeDijkstra().solve(start)
    solutions[Space(39,31)]
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { favouriteNumberString ->
    val num = favouriteNumberString.toInt()
    val office = Array(OFFICE_SIZE) { Array(OFFICE_SIZE) { OPEN } }.build(num)
    val start = Space(1, 1).withOffice(office)
    val solutions = OfficeDijkstra().solve(start)
    solutions.count { it.value <= 50 }
  }

  data class Space(val y: Int, val x: Int) : DijkstraNode<Space> {
    private var office: Array<Array<Char>> = Array(0) { Array(0) { WALL } }

    override fun neighbors(): Map<Space, Int> {
      val up = if (y - 1 >= 0 && office[y - 1][x] == OPEN) Space(y - 1, x).withOffice(office) else null
      val left = if (x - 1 >= 0 && office[y][x - 1] == OPEN) Space(y, x - 1).withOffice(office) else null
      val down = if (y + 1 < office.size && office[y + 1][x] == OPEN) Space(y + 1, x).withOffice(office) else null
      val right = if (x + 1 < office.size && office[y][x + 1] == OPEN) Space(y, x + 1).withOffice(office) else null

      return listOfNotNull(up, left, down, right).associateWith { 1 }
    }

    fun withOffice(office: Array<Array<Char>>): Space = apply { this.office = office }
  }

  class OfficeDijkstra : GenericIntDijkstra<Space>()

  private fun Array<Array<Char>>.build(num: Int) = apply {
    this.indices.forEach { y ->
      this.indices.forEach { x ->
        val number = (x*x) + (3*x) + (2*x*y) + y + (y*y) + num
        val binaryString = number.toString(Day13.BINARY_RADIX)
        val bits = binaryString.count { it == '1' }
        if (bits % 2 != 0) {
          this[y][x] = Day13.WALL
        }
      }
    }
  }

  companion object {
    const val OFFICE_SIZE = 100
    const val BINARY_RADIX = 2
    const val OPEN = '.'
    const val WALL = '#'
  }
}


