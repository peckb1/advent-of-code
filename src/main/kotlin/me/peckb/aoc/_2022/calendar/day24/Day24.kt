package me.peckb.aoc._2022.calendar.day24

import arrow.core.Tuple4
import me.peckb.aoc._2022.calendar.day24.Day24.Area.*
import me.peckb.aoc._2022.calendar.day24.Day24.Direction.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.min

typealias ExtractionArea = List<List<MutableList<Day24.Area>>>

class Day24 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val extractionArea = populateArea(input)
    val (height, width, start, end) = getData(extractionArea)

    findTravelTime(extractionArea, height, width, start, end).first
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    var extractionArea = populateArea(input)
    var startToGoal: Int
    var goalToStart: Int
    var backToGoal: Int

    val (height, width, start, end) = getData(extractionArea)

    findTravelTime(extractionArea, height, width, start, end).also {
      startToGoal = it.first ?: 0
      extractionArea = it.second
    }

    findTravelTime(extractionArea, height, width, end, start).also {
      goalToStart = it.first ?: 0
      extractionArea = it.second
    }

    findTravelTime(extractionArea, height, width, start, end).also {
      backToGoal = it.first ?: 0
      extractionArea = it.second
    }

    startToGoal + goalToStart + backToGoal
  }

  private fun populateArea(input: Sequence<String>): ExtractionArea {
    val extractionArea = mutableListOf<MutableList<MutableList<Area>>>()

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

    return extractionArea
  }

  private fun findTravelTime(
    area: ExtractionArea,
    height: Int,
    width: Int,
    startLocation: Pair<Int, Int>,
    endLocation: Pair<Int, Int>
  ): Pair<Int?, ExtractionArea> {
    // locations to steps to get there
    var currentLocations: MutableMap<Pair<Int, Int>, Int> = mutableMapOf(startLocation to 0)
    var extractionArea = area

    while(!currentLocations.containsKey(endLocation)) {
      // advance the blizzards
      extractionArea = extractionArea.advanceBlizzards()
      val proposedMovements: MutableMap<Pair<Int, Int>, Int> = mutableMapOf()

      // for every location we're in, wait, and move them
      currentLocations.forEach { (location: Pair<Int, Int>, cost: Int) ->
        val (x, y) = location
        // wait, n, s, e, w
        listOf(0 to 0, -1 to 0, 1 to 0, 0 to -1, 0 to 1).forEach { (dx, dy) ->
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

    return currentLocations[endLocation] to extractionArea
  }

  private fun getData(extractionArea: ExtractionArea): Tuple4<Int, Int, Pair<Int, Int>, Pair<Int, Int>> {
    val height = extractionArea.size
    val width = extractionArea[0].size

    return Tuple4(
      height,
      width,
      extractionArea[0].indexOfFirst { it[0] is Empty } to 0,
      extractionArea[height - 1].indexOfFirst { it[0] is Empty } to height - 1
    )
  }

  private fun ExtractionArea.advanceBlizzards(): ExtractionArea {
    val height = this.size
    val width = this[0].size

    val newState = blankVersion(height, width)

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
        if (newState[y][x].isEmpty()) newState[y][x].add(Empty)
      }
    }

    return newState
  }

  private fun blankVersion(height: Int, width: Int): ExtractionArea {
    return mutableListOf<MutableList<MutableList<Area>>>().also { new ->
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
  }

  sealed class Area {
    object Wall : Area()
    object Empty : Area()
    data class Blizzard(val direction: Direction) : Area()
  }

  enum class Direction { NORTH, SOUTH, EAST, WEST }
}