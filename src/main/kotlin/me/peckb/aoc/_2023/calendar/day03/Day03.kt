package me.peckb.aoc._2023.calendar.day03

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day03 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val engineSchematic = mutableListOf<String>()
    val parts: MutableMap<Location, Int> = mutableMapOf()

    input.forEachIndexed { rowIndex, line ->
      engineSchematic.add(line)

      var currentDigit = ""
      line.forEachIndexed { colIndex, c ->
        if (c.isDigit()) {
          currentDigit += c
        } else {
          currentDigit.toIntOrNull()?.let { number ->
            val l = Location(rowIndex, colIndex - currentDigit.length, colIndex - 1)
            parts[l] = number
          }
          currentDigit = ""
        }
      }
      currentDigit.toIntOrNull()?.let { number ->
        val l = Location(rowIndex, line.length - currentDigit.length, line.length - 1)
        parts[l] = number
      }
    }

    parts.entries.sumOf { (location, value) ->
      if (location.isNearSymbol(engineSchematic)) {
        value
      } else {
        0
      }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val engineSchematic = mutableListOf<String>()
    val parts: MutableMap<Location, Int> = mutableMapOf()

    input.forEachIndexed { rowIndex, line ->
      engineSchematic.add(line)

      var currentDigit = ""
      line.forEachIndexed { colIndex, c ->
        if (c.isDigit()) {
          currentDigit += c
        } else {
          currentDigit.toIntOrNull()?.let { number ->
            val l = Location(rowIndex, colIndex - currentDigit.length, colIndex - 1)
            parts[l] = number
          }
          currentDigit = ""
        }
      }
      currentDigit.toIntOrNull()?.let { number ->
        val l = Location(rowIndex, line.length - currentDigit.length, line.length - 1)
        parts[l] = number
      }
    }

    engineSchematic.mapIndexed { rowIndex, row ->

      row.mapIndexed { colIndex, c ->
        if (c == '*') {
          val nearbyNumbers = parts.filter { (loc, ) ->
            ((rowIndex-1)..(rowIndex+1)).contains(loc.row) &&
              (loc.startIndex-1..loc.endIndex+1).contains(colIndex)
          }

          if (nearbyNumbers.entries.size == 2) {
            nearbyNumbers.entries.fold(1) { acc, entry ->
              acc * entry.value
            }
          } else {
            0
          }
        } else {
          0
        }
      }.sum()
    }.sum()

  }

  data class Location(val row: Int, val startIndex: Int, val endIndex: Int) {
    fun isNearSymbol(engineSchematic: MutableList<String>): Boolean {
      return (startIndex..endIndex).any { colIndex ->
        if (row > 0) {
          if (colIndex > 0) {
            val ul = engineSchematic[row-1][colIndex-1]
            if (ul != '.' && !ul.isDigit()) {
              return true
            }
          }
          val u = engineSchematic[row-1][colIndex]
          if (u != '.' && !u.isDigit()) {
            return true
          }

          if (colIndex < engineSchematic[row-1].length - 1) {
            val ur = engineSchematic[row - 1][colIndex + 1]
            if (ur != '.' && !ur.isDigit()) {
              return true
            }
          }
        }

        if (startIndex > 0) {
          if (engineSchematic[row][startIndex - 1] != '.') {
            return true
          }
        }

        if (endIndex < engineSchematic[row].length - 1) {
          if (engineSchematic[row][endIndex + 1] != '.') {
            return true
          }
        }

        if (row < engineSchematic.size - 1) {
          if (colIndex > 0) {
            val dl = engineSchematic[row+1][colIndex-1]
            if (dl != '.' && !dl.isDigit()) {
              return true
            }
          }
          val d = engineSchematic[row+1][colIndex]
          if (d != '.' && !d.isDigit()) {
            return true
          }
          if (colIndex < engineSchematic[row+1].length - 1) {
            val dr = engineSchematic[row + 1][colIndex + 1]
            if (dr != '.' && !dr.isDigit()) {
              return true
            }
          }
        }

        false
      }
    }
  }

}
