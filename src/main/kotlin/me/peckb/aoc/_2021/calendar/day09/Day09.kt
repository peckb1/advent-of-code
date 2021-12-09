package me.peckb.aoc._2021.calendar.day09

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.Int.Companion.MAX_VALUE

typealias Topography = List<List<Int>>

class Day09 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun findLowPointSum(fileName: String) = generatorFactory.forFile(fileName).readAs(::heights) { input ->
    input.toList().findLowPoints().sumOf { it.height + 1 }
  }

  fun findLargestBasinProduct(fileName: String) = generatorFactory.forFile(fileName).readAs(::heights) { input ->
    val floor = input.toList()
    val minLocations = floor.findLowPoints()

    val basins = minLocations.map { (value, y, x) ->
      val initialLocation = Location(value, y, x)

      val spacesInBasin = mutableSetOf(initialLocation)
      val spacesToExplore = mutableSetOf<Location>().apply {
        addAll(initialLocation.generateExploringNeighbors())
      }

      while(spacesToExplore.isNotEmpty()) {
        val spaceToExplore = spacesToExplore.first().also { spacesToExplore.remove(it) }
        val valueOfMe = floor.findHeight(spaceToExplore.y, spaceToExplore.x)

        if (valueOfMe in (spaceToExplore.height + 1)..8) {
          val exploredLocation = Location(valueOfMe, spaceToExplore.y, spaceToExplore.x)

          spacesInBasin.add(exploredLocation)
          spacesToExplore.addAll(exploredLocation.generateExploringNeighbors())
        }
      }

      spacesInBasin
    }

    basins.sortedByDescending { it.size }
      .take(3)
      .map { it.size }
      .reduce(Int::times)
  }

  private fun heights(line: String) = line.map(Character::getNumericValue)

  private fun Topography.findLowPoints(): List<Location> {
    val minLocations = mutableListOf<Location>()

    repeat(this[0].size) { x ->
      repeat(this.size) { y ->
        val me = this[y][x]
        val myLocation = Location(me, y, x)

        val neighbors = myLocation.generateExploringNeighbors().map { findHeight(it.y, it.x) }
        if (neighbors.all { me < it }) {
          minLocations.add(myLocation)
        }
      }
    }

    return minLocations
  }

  private fun Topography.findHeight(y: Int, x: Int) =
    if (y in (0 until size) && x in (0 until this[y].size)) {
      this[y][x]
    } else {
      MAX_VALUE
    }

  private data class Location(val height: Int, val y: Int, val x: Int) {
    fun generateExploringNeighbors() = setOf(
      Location(height, y - 1, x),
      Location(height, y + 1, x),
      Location(height, y, x - 1),
      Location(height, y, x + 1)
    )
  }
}
