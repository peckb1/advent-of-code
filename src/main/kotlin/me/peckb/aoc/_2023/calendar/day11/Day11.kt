package me.peckb.aoc._2023.calendar.day11

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day11 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val spaceSize = 2L
    findSum(input, spaceSize)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val spaceSize = 1_000_000L
    findSum(input, spaceSize)
  }

  private fun findSum(input: Sequence<String>, spaceSize: Long): Long {
    val area = input.map { line ->
      mutableListOf<Char>().apply {
        line.forEach { c -> this.add(c) }
      }
    }.toMutableList()

    val (emptyRows, emptyColumns) = findEmpties(area)

    val stars = mutableListOf<Location>()

    area.forEachIndexed { rowIndex, row ->
      row.forEachIndexed { colIndex, c ->
        if (c == '#') stars.add(Location(rowIndex, colIndex))
      }
    }

    var distanceSum = 0L

    stars.indices.forEach { firstStarIndex ->
      ((firstStarIndex + 1) until stars.size).forEach { secondStarIndex ->
        val s1 = stars[firstStarIndex]
        val s2 = stars[secondStarIndex]

        val minRow = min(s1.row, s2.row)
        val maxRow = max(s1.row, s2.row)
        val minCol = min(s1.col, s2.col)
        val maxCol = max(s1.col, s2.col)

        val extraRows = emptyRows.count { ((minRow + 1) until maxRow).contains(it) }
        val extraColumns = emptyColumns.count { ((minCol + 1) until maxCol).contains(it) }

        distanceSum += s1.distanceFrom(s2) + ((spaceSize - 1) * (extraColumns + extraRows))
      }
    }

    return distanceSum
  }

  private fun findEmpties(initialArea: MutableList<MutableList<Char>>): Pair<List<Int>, List<Int>> {
    val emptyRows = mutableListOf<Int>()
    val emptyColumns = mutableListOf<Int>()

    initialArea.forEachIndexed { rowIndex, row ->
      if (row.none { it == '#' }) emptyRows.add(rowIndex)
    }

    initialArea[0].indices.forEach { columnIndex ->
      if (initialArea.indices.none { initialArea[it][columnIndex] == '#' }) emptyColumns.add(columnIndex)
    }

    return emptyRows to emptyColumns
  }

  data class Location(val row: Int, val col: Int) {
    fun distanceFrom(s2: Location): Long {
      return (abs(row - s2.row) + abs(col -s2.col)).toLong()
    }
  }
}
