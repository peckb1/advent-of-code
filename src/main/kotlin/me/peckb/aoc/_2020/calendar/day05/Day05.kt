package me.peckb.aoc._2020.calendar.day05

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.ceil

class Day05 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::seat) { input ->
    input.maxOf { it.id }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::seat) { input ->
    val seatBeforeMine = input.sortedBy { it.id } // sort them by seat ID
      .windowed(2)                                 // work in groups of two
      .first { (a, b) -> b.id - a.id != 1 } // find the window that has a gap
      .first()                                          // grab the start of that window
    seatBeforeMine.id + 1                       // add one to get my seat ID
  }

  private fun seat(line: String) : Seat {
    var minRow = MIN_ROW
    var maxRow = MAX_ROW
    var minCol = MIN_COLUMN
    var maxCol = MAX_COLUMN

    line.forEach {
      when (it) {
        'F' -> maxRow -= ceil((maxRow - minRow) / 2.0).toInt()
        'B' -> minRow += ceil((maxRow - minRow) / 2.0).toInt()
        'L' -> maxCol -= ceil((maxCol - minCol) / 2.0).toInt()
        'R' -> minCol += ceil((maxCol - minCol) / 2.0).toInt()
      }
    }

    if (minRow != maxRow) throw IllegalStateException("Failed to find single row")
    if (minCol != maxCol) throw IllegalStateException("Failed to find single column")

    return Seat(minRow, minCol)
  }

  data class Seat(val row: Int, val column: Int) {
    val id = (row * 8) + column
  }

  companion object {
    private const val MIN_ROW = 0
    private const val MAX_ROW = 127
    private const val MIN_COLUMN = 0
    private const val MAX_COLUMN = 7
  }
}
