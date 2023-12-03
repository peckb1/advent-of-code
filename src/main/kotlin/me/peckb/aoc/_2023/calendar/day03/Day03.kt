package me.peckb.aoc._2023.calendar.day03

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day03 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (engine, parts) = createData(input)

    val validParts = mutableSetOf<Part>()

    engine.forEachIndexed { rowIndex, row ->
      val rowRange = (rowIndex - 1)..(rowIndex + 1)
      row.forEachIndexed { colIndex, c ->
        if (c.isSymbol) {
          parts.forEach { part ->
            val partIsNearRow = rowRange.contains(part.location.row)
            val partIsNearCol by lazy { part.location.extendedRange.contains(colIndex) }

            if (partIsNearRow && partIsNearCol) validParts.add(part)
          }
        }
      }
    }

    validParts.sumOf { it.value }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (engine, parts) = createData(input)

    val gearRatios = mutableListOf<Int>()

    engine.forEachIndexed { rowIndex, row ->
      val rowRange = (rowIndex - 1)..(rowIndex + 1)
      row.forEachIndexed { colIndex, c ->
        if (c.isGear) {
          val nearbyParts = parts.filter { part ->
            val partIsNearRow = rowRange.contains(part.location.row)
            val partIsNearCol by lazy { part.location.extendedRange.contains(colIndex) }

            partIsNearRow && partIsNearCol
          }

          if (nearbyParts.size == 2) {
            gearRatios.add(nearbyParts.first().value * nearbyParts.last().value)
          }
        }
      }
    }

    gearRatios.sum()
  }

  private fun createData(input: Sequence<String>): Pair<MutableList<String>, MutableList<Part>> {
    val engineSchematic = mutableListOf<String>()
    val parts: MutableList<Part> = mutableListOf()

    input.forEachIndexed { rowIndex, row ->
      engineSchematic.add(row)
      findPartData(row).forEach { (intRange, value) ->
        val location = Location(rowIndex, intRange)
        val part = Part(value, location)
        parts.add(part)
      }
    }

    return (engineSchematic to parts)
  }

  private fun findPartData(row: String): List<Pair<IntRange, Int>> {
    val parts = mutableListOf<Pair<IntRange, Int>>()
    var currentDigit = ""

    row.forEachIndexed { colIndex, c ->
      if (c.isDigit()) {
        currentDigit += c
      } else {
        currentDigit.toIntOrNull()?.let { number ->
          val range = (colIndex - currentDigit.length - 1)..colIndex
          parts.add(range to number)
        }
        currentDigit = ""
      }
    }
    currentDigit.toIntOrNull()?.let { number ->
      val range = (row.length - currentDigit.length - 1)..row.length
      parts.add(range to number)
    }

    return parts
  }

  data class Part(val value: Int, val location: Location)

  data class Location(val row: Int, val extendedRange: IntRange)

  private val Char.isSymbol get() = !this.isDigit() && this != '.'

  private val Char.isGear get() = this == '*'
}
