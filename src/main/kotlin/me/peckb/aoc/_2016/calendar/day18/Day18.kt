package me.peckb.aoc._2016.calendar.day18

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day18 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val room = mutableListOf(input)
    repeat(PART_ONE_ROWS - 1) { row ->
      val nextRow = "$OPEN${room[row]}$OPEN".windowed(3).map { data ->
        val left = data[0] == TRAP
        val center = data[1] == TRAP
        val right = data[2] == TRAP
        if (left && center && !right || !left && center && right || left && !center && !right || !left && !center && right) {
          TRAP
        } else {
          OPEN
        }
      }
      room.add(nextRow.joinToString(""))
    }
    room.sumOf { row -> row.count { it == OPEN } }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    var row = input
    var total = row.count { it == OPEN }.toLong()
    repeat(PART_TWO_ROWS - 1) {
      val nextRow = "$OPEN$row$OPEN".windowed(3).map { data ->
        val left = data[0] == TRAP
        val center = data[1] == TRAP
        val right = data[2] == TRAP
        if (left && center && !right || !left && center && right || left && !center && !right || !left && !center && right) {
          TRAP
        } else {
          OPEN
        }
      }
      row = nextRow.joinToString("")
      total += row.count { it == OPEN }
    }
    total
  }

  companion object {
    const val PART_ONE_ROWS = 40
    const val PART_TWO_ROWS = 400000
    const val TRAP = '^'
    const val OPEN = '.'
  }
}
