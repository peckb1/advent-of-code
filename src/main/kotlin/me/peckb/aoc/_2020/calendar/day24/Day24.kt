package me.peckb.aoc._2020.calendar.day24

import me.peckb.aoc._2020.calendar.day24.Day24.Step.*
import me.peckb.aoc._2020.calendar.day24.Day24.TileColor.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day24 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::tilePath) { input ->
    val flippedTiles = initialTileLayout(input)
    flippedTiles.count { it.value == BLACK }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::tilePath) { input ->
    val flippedTiles = initialTileLayout(input)

    repeat(100) {
      val tilesToFlip = mutableSetOf<Location>()
      flippedTiles
        .filter { flippedTiles.getOrDefault(it.key, WHITE) == BLACK }
        .flatMap { it.key.neighborLocations.plus(it.key) }
        .forEach { location ->
          val color = flippedTiles.getOrDefault(location, WHITE)
          val neighbors = location.neighborLocations
          val blackNeighborTileCount = neighbors.mapNotNull { flippedTiles[it] }.count { it == BLACK }

          when (color) {
            BLACK -> if (0 == blackNeighborTileCount || 2 < blackNeighborTileCount) tilesToFlip.add(location)
            WHITE -> if (2 == blackNeighborTileCount) tilesToFlip.add(location)
          }
        }
      tilesToFlip.forEach {
        flippedTiles[it] = flippedTiles.getOrDefault(it, WHITE).flip()
      }
    }

    flippedTiles.count { it.value == BLACK }
  }

  private fun initialTileLayout(input: Sequence<List<Step>>): MutableMap<Location, TileColor> {
    val flippedTiles = mutableMapOf<Location, TileColor>()

    input.forEach { steps ->
      var x = 0
      var y = 0
      var z = 0

      steps.forEach { step ->
        when (step) {
          // East       / West       don't change the Z
          // North East / South West don't change the Y
          // South East / North West don't change the X
          EAST       -> { x += 1; y -= 1 }
          WEST       -> { x -= 1; y += 1 }
          NORTH_EAST -> { x += 1; z -= 1 }
          SOUTH_WEST -> { x -= 1; z += 1 }
          SOUTH_EAST -> { y -= 1; z += 1 }
          NORTH_WEST -> { y += 1; z -= 1 }
        }
      }

      val location = Location(x, y, z)

      flippedTiles[location] = flippedTiles.getOrDefault(location, WHITE).flip()
    }

    return flippedTiles
  }

  private fun tilePath(line: String): List<Step> {
    val iterator = line.iterator()
    val steps = mutableListOf<Step>()

    while(iterator.hasNext()) {
      steps.add(
        when (val d = iterator.nextChar()) {
          'e' -> EAST
          'w'-> WEST
          's' -> {
            when (val d2 = iterator.nextChar()) {
              'e' -> SOUTH_EAST
              'w' -> SOUTH_WEST
              else -> throw IllegalStateException("Invalid Step End: $d2")
            }
          }
          'n' -> {
            when (val d2 = iterator.nextChar()) {
              'e' -> NORTH_EAST
              'w' -> NORTH_WEST
              else -> throw IllegalStateException("Invalid Step End: $d2")
            }
          }
          else -> throw IllegalStateException("Invalid Step Start: $d")
        }
      )
    }

    return steps
  }

  enum class Step {
    // e, se, sw, w, nw, and ne
    EAST, SOUTH_EAST, SOUTH_WEST, WEST, NORTH_WEST, NORTH_EAST
  }

  data class Location(
    val x: Int,
    val y: Int,
    val z: Int,
  ) {
    val neighborLocations by lazy {
      listOf(
        Location(x + 1, y - 1, z),
        Location(x - 1, y + 1, z),
        Location(x + 1, y, z - 1),
        Location(x - 1, y, z + 1),
        Location(x, y - 1, z + 1),
        Location(x, y + 1, z - 1),
      )
    }
  }

  enum class TileColor {
    BLACK { override fun flip(): TileColor = WHITE },
    WHITE { override fun flip(): TileColor = BLACK };

    abstract fun flip(): TileColor
  }
}
