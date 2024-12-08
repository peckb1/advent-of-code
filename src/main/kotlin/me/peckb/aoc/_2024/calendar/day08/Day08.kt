package me.peckb.aoc._2024.calendar.day08

import me.peckb.aoc.generators.CombinationsGenerator
import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day08 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { lines ->
    val (maxY, maxX, antenna) = findAntenna(lines)
    val antinodes = findAntinodes(maxY, maxX, antenna, false)

    antinodes.size
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { lines ->
    val (maxY, maxX, antenna) = findAntenna(lines)
    val antinodes = findAntinodes(maxY, maxX, antenna, true)

    antinodes.plus(
      antenna.values.flatMap { it.toList() }
    ).size
  }

  private fun findAntenna(input: Sequence<String>): AntennaData {
    val antenna = mutableMapOf<Char, Array<Location>>()

    var maxY = 0
    var maxX = 0

    input.forEachIndexed { yIndex, line ->
      maxY = maxOf(maxY, yIndex)
      line.forEachIndexed { xIndex, space ->
        maxX = maxOf(maxX, xIndex)
        when (space) {
          '.' -> { /* skip */ }
          else -> antenna.merge(space, arrayOf(Location(yIndex, xIndex))) { a, b -> a + b }
        }
      }
    }

    return AntennaData(maxY, maxX, antenna)
  }

  private fun findAntinodes(
    maxY: Int,
    maxX: Int,
    antenna: Map<Char, Array<Location>>,
    forever: Boolean
  ): Set<Location> {
    val antinodes = mutableSetOf<Location>()

    fun find(antenna: Location, xDelta: Int, yDelta: Int, mover: (Int, Int) -> Int) {
      var inbounds = forever
      var frequency = 1

      val (y, x) = antenna

      do {
        val possibleY = mover(y, (yDelta * frequency))
        val possibleX = mover(x, (xDelta * frequency))

        if (possibleX in 0..maxX && possibleY in 0..maxY) {
          antinodes.add(Location(possibleY, possibleX))
          frequency++
        } else {
          inbounds = false
        }
      } while (inbounds)
    }

    antenna.forEach { (_, locations) ->
      val combinations = CombinationsGenerator.findCombinations(locations, 2)

      combinations.forEach { (a, b) ->
        val xDelta = a.x - b.x
        val yDelta = a.y - b.y

        find(a, xDelta, yDelta, Int::plus)
        find(b, xDelta, yDelta, Int::minus)
      }
    }

    return antinodes
  }
}

data class AntennaData(val maxY: Int, val maxX: Int, val locations: Map<Char, Array<Location>>)

data class Location(val y: Int, val x: Int)
