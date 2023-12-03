package me.peckb.aoc._2023.calendar.day03

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day03 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (symbolLocations, parts) = createData(input) { !it.isDigit() && it != '.' }

    var partSum = 0

    symbolLocations.forEach { (rowIndex, colIndex) ->
      listOfNotNull(parts[rowIndex - 1], parts[rowIndex], parts[rowIndex + 1])
        .flatten()
        .filter { it.location.extendedRange.contains(colIndex) }
        .forEach { partSum += it.value }
    }

    partSum
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (symbolLocations, parts) = createData(input) { it == '*' }

    var gearRatioSum = 0

    symbolLocations.forEach { (rowIndex, colIndex) ->
      val nearbyParts = listOfNotNull(parts[rowIndex - 1], parts[rowIndex], parts[rowIndex + 1])
        .flatten()
        .filter { part -> part.location.extendedRange.contains(colIndex) }

      if (nearbyParts.size == 2) {
        gearRatioSum += (nearbyParts.first().value * nearbyParts.last().value)
      }
    }

    gearRatioSum
  }

  private fun createData(
    input: Sequence<String>,
    check: (Char) -> Boolean
  ): Pair<List<SymbolLocation>, Map<Int, List<Part>>> {
    val validSymbols = mutableListOf<SymbolLocation>()
    val parts: MutableMap<Int, MutableList<Part>> = mutableMapOf()

    input.forEachIndexed { rowIndex, row ->
      row.forEachIndexed { colIndex, c ->
        if (check(c)) {
          validSymbols.add(SymbolLocation(rowIndex, colIndex))
        }
      }
      findPartData(row).forEach { (intRange, value) ->
        val location = Location(rowIndex, intRange)
        val part = Part(value, location)

        parts[rowIndex] = parts.getOrDefault(rowIndex, mutableListOf()).also { it.add(part) }
      }
    }

    return (validSymbols to parts)
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

  data class SymbolLocation(val rowIndex: Int, val colIndex: Int)

  data class Part(val value: Int, val location: Location)

  data class Location(val row: Int, val extendedRange: IntRange)
}
