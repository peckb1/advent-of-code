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
      row.forEachIndexed { colIndex, c ->
        if (c.isSymbol) {
          listOfNotNull(
            parts[rowIndex - 1], parts[rowIndex], parts[rowIndex + 1]
          ).flatten().forEach { part ->
            if (part.location.extendedRange.contains(colIndex)) validParts.add(part)
          }
        }
      }
    }

    validParts.sumOf { it.value }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (engine, parts) = createData(input)

    var gearRatioSum = 0

    engine.forEachIndexed { rowIndex, row ->
      row.forEachIndexed { colIndex, c ->
        if (c.isGear) {
          val nearbyParts = listOfNotNull(
            parts[rowIndex - 1], parts[rowIndex], parts[rowIndex + 1]
          ).flatten().filter { part -> part.location.extendedRange.contains(colIndex) }

          if (nearbyParts.size == 2) {
            gearRatioSum += (nearbyParts.first().value * nearbyParts.last().value)
          }
        }
      }
    }

    gearRatioSum
  }

  private fun createData(input: Sequence<String>): Pair<List<String>, Map<Int, List<Part>>> {
    val engineSchematic = mutableListOf<String>()
    val parts: MutableMap<Int, MutableList<Part>> = mutableMapOf()

    input.forEachIndexed { rowIndex, row ->
      engineSchematic.add(row)
      findPartData(row).forEach { (intRange, value) ->
        val location = Location(rowIndex, intRange)
        val part = Part(value, location)

        parts[rowIndex] = parts.getOrDefault(rowIndex, mutableListOf()).also { it.add(part) }
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
