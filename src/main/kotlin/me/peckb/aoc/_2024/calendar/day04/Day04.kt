package me.peckb.aoc._2024.calendar.day04

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day04 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { lines ->
    val (xLocations, area) = setup(lines, 'X')

    xLocations.sumOf { (x, y) ->
      val N  = area.findXMAS(y, -1, x,  0)
      val NE = area.findXMAS(y, -1, x,  1)
      val E  = area.findXMAS(y,  0, x,  1)
      val SE = area.findXMAS(y,  1, x,  1)
      val S  = area.findXMAS(y,  1, x,  0)
      val SW = area.findXMAS(y,  1, x, -1)
      val W  = area.findXMAS(y,  0, x, -1)
      val NW = area.findXMAS(y, -1, x, -1)

      listOf(N, NE, E, SE, S, SW, W, NW).count { it == "XMAS" }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { lines ->
    val (aLocations, area) = setup(lines, 'A')

    aLocations.count { (x, y) ->
      val NE = area.getValue(y - 1, x + 1)
      val SE = area.getValue(y + 1, x + 1)
      val SW = area.getValue(y + 1, x - 1)
      val NW = area.getValue(y - 1, x - 1)

      if      (NW == NE && SE == SW) { (NW == 'M' && SW == 'S') || (NW == 'S' && SW == 'M') }
      else if (NW == SW && NE == SE) { (NW == 'M' && NE == 'S') || (NW == 'S' && NE == 'M') }
      else false
    }
  }

  private fun setup(lines: Sequence<String>, target: Char): Pair<List<Location>, List<List<Char>>> {
    val locations = mutableListOf<Location>()
    val area = lines.toList().mapIndexed { yIndex, line ->
      line.mapIndexed { xIndex, letter ->
        letter.also { if (it == target) { locations.add(Location(xIndex, yIndex)) } }
      }
    }

    return locations to area
  }
}

data class Location(val x: Int, val y: Int)

private fun List<List<Char>>.findXMAS(y: Int, yDelta: Int, x: Int, xDelta: Int): String {
  return (0..3)
    .mapNotNull { delta -> this.getValue(y + (yDelta * delta), x + (xDelta * delta)) }
    .joinToString("")
}

private fun List<List<Char>>.getValue(y: Int, x: Int): Char? {
  return if (y in indices && x in this[y].indices) { this[y][x] } else { null }
}
