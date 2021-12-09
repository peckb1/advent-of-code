package me.peckb.aoc._2021.calendar.day09

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.Int.Companion.MAX_VALUE

typealias Topography = List<List<Int>>

class Day09 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun findLowPointSum(fileName: String) = generatorFactory.forFile(fileName).readAs(::day09) { input ->
    input.toList().findLowPoints().sumOf { it.height + 1 }
  }

  fun findLargestBasinProduct(fileName: String) = generatorFactory.forFile(fileName).readAs(::day09) { input ->
    val floor = input.toList()
    // [y,x] pairs
    val minLocations = floor.findLowPoints()

    val basins = minLocations.map { (value, y, x) ->
      val spacesInBasin = mutableSetOf<Pair<Int, Int>>(
        y to x
      )
      val spacesToExplore = mutableSetOf<Pair<Int, Pair<Int, Int>>>(
        value to (y - 1 to x),
        value to (y + 1 to x),
        value to (y to x - 1),
        value to (y to x + 1)
      )
      while(spacesToExplore.isNotEmpty()) {
        val spaceToExplore = spacesToExplore.first().also {
          spacesToExplore.remove(it)
        }

        val originalValue = spaceToExplore.first
        val (myY, myX) = spaceToExplore.second
        val valueOfMe = floor.findHeight(myY, myX)

        if (valueOfMe in (originalValue + 1)..8) {
          spacesInBasin.add(myY to myX)
          spacesToExplore.addAll(
            listOf(
              valueOfMe to (myY - 1 to myX),
              valueOfMe to (myY + 1 to myX),
              valueOfMe to (myY to myX - 1),
              valueOfMe to (myY to myX + 1)
            )
          )
        }
      }

      spacesInBasin
    }

    basins
      .sortedByDescending { it.size }
      .take(3)
      .map { it.size }
      .fold(1) { acc, i ->
        i * acc
      }
  }

  private fun day09(line: String) = line.map(Character::getNumericValue)

  private fun Topography.findLowPoints(): List<Location> {
    val minLocations = mutableListOf<Location>()

    repeat(this[0].size) { x ->
      repeat(this.size) { y ->
        val me = this[y][x]

        val up = findHeight(y - 1, x)
        val down = findHeight(y + 1, x)
        val left = findHeight(y, x - 1)
        val right = findHeight(y, x + 1)

        if (me < up && me < down && me < left && me < right) {
          minLocations.add(Location(me, y, x))
        }
      }
    }

    return minLocations
  }

  private fun Topography.findHeight(y: Int, x: Int) = try {
    this[y][x]
  } catch(e: IndexOutOfBoundsException) {
    MAX_VALUE
  }

  private data class Location(val height: Int, val y: Int, val x: Int)
}
