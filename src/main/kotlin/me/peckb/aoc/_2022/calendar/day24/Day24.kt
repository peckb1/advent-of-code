package me.peckb.aoc._2022.calendar.day24

import me.peckb.aoc._2022.calendar.day24.Day24.Area.*
import me.peckb.aoc._2022.calendar.day24.Day24.Direction.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.min

class Day24 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    var extractionArea = mutableListOf<MutableList<MutableList<Area>>>()

    input.forEach { line ->
      val row = mutableListOf<MutableList<Area>>()
      line.forEach { c ->
        when (c) {
          '#' -> row.add(mutableListOf(Wall))
          '.' -> row.add(mutableListOf(Empty))
          '^' -> row.add(mutableListOf(Blizzard(NORTH)))
          'v' -> row.add(mutableListOf(Blizzard(SOUTH)))
          '>' -> row.add(mutableListOf(Blizzard(EAST)))
          '<' -> row.add(mutableListOf(Blizzard(WEST)))
        }
      }
      extractionArea.add(row)
    }

    val height = extractionArea.size
    val width = extractionArea[0].size
    val startLocation = extractionArea[0].indexOfFirst { it[0] is Empty } to 0
    val endLocation = extractionArea[height - 1].indexOfFirst { it[0] is Empty } to height - 1

    // locations to steps to get there
    var currentLocations: MutableMap<Pair<Int, Int>, Int> = mutableMapOf(startLocation to 0)

    while(!currentLocations.containsKey(endLocation)) {
      // advance the blizzards
      extractionArea = extractionArea.advanceBlizzards()

      val proposedMovements: MutableMap<Pair<Int, Int>, Int> = mutableMapOf()

      // for every location we're in, wait, and move them
      currentLocations.forEach { (location: Pair<Int, Int>, cost: Int) ->
        val (x, y) = location
        // current spot
        if (extractionArea[y][x].first() is Empty) {
          proposedMovements.merge(location, cost + 1) { a, b -> min(a, b) }
        }
        // n, s, e, w
        listOf(
          -1 to 0, 1 to 0, 0 to -1, 0 to 1
        ).forEach { (dx, dy) ->
          val newX = x + dx
          val newY = y + dy
          if (newX in 0 until width && newY in 0 until height) {
            if (extractionArea[newY][newX].size == 1 && extractionArea[newY][newX].first() is Empty) {
              proposedMovements.merge(newX to newY, cost + 1) { a, b -> min(a, b) }
            }
          }
        }
      }

      currentLocations = proposedMovements
    }

    currentLocations[endLocation]
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    var extractionArea = mutableListOf<MutableList<MutableList<Area>>>()

    input.forEach { line ->
      val row = mutableListOf<MutableList<Area>>()
      line.forEach { c ->
        when (c) {
          '#' -> row.add(mutableListOf(Wall))
          '.' -> row.add(mutableListOf(Empty))
          '^' -> row.add(mutableListOf(Blizzard(NORTH)))
          'v' -> row.add(mutableListOf(Blizzard(SOUTH)))
          '>' -> row.add(mutableListOf(Blizzard(EAST)))
          '<' -> row.add(mutableListOf(Blizzard(WEST)))
        }
      }
      extractionArea.add(row)
    }

    val height = extractionArea.size
    val width = extractionArea[0].size
    var startLocation = extractionArea[0].indexOfFirst { it[0] is Empty } to 0
    var endLocation = extractionArea[height - 1].indexOfFirst { it[0] is Empty } to height - 1

    // locations to steps to get there
    var currentLocations: MutableMap<Pair<Int, Int>, Int> = mutableMapOf(startLocation to 0)

    while(!currentLocations.containsKey(endLocation)) {
      // advance the blizzards
      extractionArea = extractionArea.advanceBlizzards()

      val proposedMovements: MutableMap<Pair<Int, Int>, Int> = mutableMapOf()

      // for every location we're in, wait, and move them
      currentLocations.forEach { (location: Pair<Int, Int>, cost: Int) ->
        val (x, y) = location
        // current spot
        if (extractionArea[y][x].first() is Empty) {
          proposedMovements.merge(location, cost + 1) { a, b -> min(a, b) }
        }
        // n, s, e, w
        listOf(
          -1 to 0, 1 to 0, 0 to -1, 0 to 1
        ).forEach { (dx, dy) ->
          val newX = x + dx
          val newY = y + dy
          if (newX in 0 until width && newY in 0 until height) {
            if (extractionArea[newY][newX].size == 1 && extractionArea[newY][newX].first() is Empty) {
              proposedMovements.merge(newX to newY, cost + 1) { a, b -> min(a, b) }
            }
          }
        }
      }

      currentLocations = proposedMovements
    }

    val startToEndCost = currentLocations[endLocation] ?: 0

    endLocation = extractionArea[0].indexOfFirst { it[0] is Empty } to 0
    startLocation = extractionArea[height - 1].indexOfFirst { it[0] is Empty } to height - 1

    // locations to steps to get there
    currentLocations = mutableMapOf(startLocation to 0)

    while(!currentLocations.containsKey(endLocation)) {
      // advance the blizzards
      extractionArea = extractionArea.advanceBlizzards()

      val proposedMovements: MutableMap<Pair<Int, Int>, Int> = mutableMapOf()

      // for every location we're in, wait, and move them
      currentLocations.forEach { (location: Pair<Int, Int>, cost: Int) ->
        val (x, y) = location
        // current spot
        if (extractionArea[y][x].first() is Empty) {
          proposedMovements.merge(location, cost + 1) { a, b -> min(a, b) }
        }
        // n, s, e, w
        listOf(
          -1 to 0, 1 to 0, 0 to -1, 0 to 1
        ).forEach { (dx, dy) ->
          val newX = x + dx
          val newY = y + dy
          if (newX in 0 until width && newY in 0 until height) {
            if (extractionArea[newY][newX].size == 1 && extractionArea[newY][newX].first() is Empty) {
              proposedMovements.merge(newX to newY, cost + 1) { a, b -> min(a, b) }
            }
          }
        }
      }

      currentLocations = proposedMovements
    }

    val endToStartCost = currentLocations[endLocation] ?: 0

    startLocation = extractionArea[0].indexOfFirst { it[0] is Empty } to 0
    endLocation = extractionArea[height - 1].indexOfFirst { it[0] is Empty } to height - 1

    // locations to steps to get there
    currentLocations = mutableMapOf(startLocation to 0)

    while(!currentLocations.containsKey(endLocation)) {
      // advance the blizzards
      extractionArea = extractionArea.advanceBlizzards()

      val proposedMovements: MutableMap<Pair<Int, Int>, Int> = mutableMapOf()

      // for every location we're in, wait, and move them
      currentLocations.forEach { (location: Pair<Int, Int>, cost: Int) ->
        val (x, y) = location
        // current spot
        if (extractionArea[y][x].first() is Empty) {
          proposedMovements.merge(location, cost + 1) { a, b -> min(a, b) }
        }
        // n, s, e, w
        listOf(
          -1 to 0, 1 to 0, 0 to -1, 0 to 1
        ).forEach { (dx, dy) ->
          val newX = x + dx
          val newY = y + dy
          if (newX in 0 until width && newY in 0 until height) {
            if (extractionArea[newY][newX].size == 1 && extractionArea[newY][newX].first() is Empty) {
              proposedMovements.merge(newX to newY, cost + 1) { a, b -> min(a, b) }
            }
          }
        }
      }

      currentLocations = proposedMovements
    }

    startToEndCost + endToStartCost + (currentLocations[endLocation] ?: 0)
  }


  private fun MutableList<MutableList<MutableList<Area>>>.advanceBlizzards(): MutableList<MutableList<MutableList<Area>>> {
    val height = this.size
    val width = this[0].size

    val newState = mutableListOf<MutableList<MutableList<Area>>>().also { new ->
      repeat(height) {
        new.add(
          mutableListOf<MutableList<Area>>().also { row ->
            repeat(width) {
              row.add(mutableListOf())
            }
          }
        )
      }
    }

    forEachIndexed { y, currentRow ->
      currentRow.forEachIndexed { x, areas ->
        areas.forEach { area ->
          when (area) {
            is Blizzard -> {
              when (area.direction) {
                NORTH -> {
                  if (this[y - 1][x].first() is Wall) {
                    newState[height - 2][x].add(area)
                  } else {
                    newState[y - 1][x].add(area)
                  }
                }

                SOUTH -> {
                  if (this[y + 1][x].first() is Wall) {
                    newState[1][x].add(area)
                  } else {
                    newState[y + 1][x].add(area)
                  }
                }

                EAST -> {
                  if (this[y][x + 1].first() is Wall) {
                    newState[y][1].add(area)
                  } else {
                    newState[y][x + 1].add(area)
                  }
                }

                WEST -> {
                  if (this[y][x - 1].first() is Wall) {
                    newState[y][width - 2].add(area)
                  } else {
                    newState[y][x - 1].add(area)
                  }
                }
              }
            }
            Wall -> newState[y][x].add(area)
            Empty -> { /* */ }
          }
        }
      }
    }

    forEachIndexed { y, currentRow ->
      currentRow.forEachIndexed { x, _ ->
        if (newState[y][x].isEmpty()) {
          (newState[y][x].add(Empty))
        }
      }
    }

    return newState
  }

  private fun printExtractionArea(extractionArea: MutableList<MutableList<MutableList<Area>>>) {
    extractionArea.forEach { row ->
      row.forEach { areas ->
        if (areas.size > 1) {
          print(areas.size)
        } else {
          when (areas[0]) {
            is Blizzard -> print((areas[0] as Blizzard).direction.representation)
            Empty -> print('.')
            Wall -> print('#')
          }
        }
      }
      println()
    }
  }

  sealed class Area {
    object Wall : Area()
    object Empty : Area()
    data class Blizzard(val direction: Direction) : Area()
  }

  enum class Direction(val representation: String) {
    NORTH("^"),
    SOUTH("v"),
    EAST(">"),
    WEST("<")
  }
}