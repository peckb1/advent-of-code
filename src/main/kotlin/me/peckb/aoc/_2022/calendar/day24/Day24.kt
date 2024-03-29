package me.peckb.aoc._2022.calendar.day24

import arrow.core.Tuple4
import me.peckb.aoc._2022.calendar.day24.Day24.Area
import me.peckb.aoc._2022.calendar.day24.Day24.Area.*
import me.peckb.aoc._2022.calendar.day24.Day24.Direction.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.min

typealias ExtractionArea = List<List<MutableList<Area>>>

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
          '.' -> row.add(mutableListOf())
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
            if (extractionArea[newY][newX].size == 0) {
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
      extractionArea[0].indexOfFirst { it.isEmpty() } to 0,
      extractionArea[height - 1].indexOfFirst { it.isEmpty() } to height - 1
    )
  }

  private fun ExtractionArea.advanceBlizzards(): ExtractionArea {
    val height = this.size
    val width = this[0].size

    val newState = blankVersion(height, width)

    fun updateState(area: Blizzard, stepX: Int, stepY: Int, edgeX: Int, edgeY: Int) {
      if (this[stepY][stepX].firstOrNull() is Wall) {
        newState[edgeY][edgeX].add(area)
      } else {
        newState[stepY][stepX].add(area)
      }
    }

    forEachIndexed { y, currentRow ->
      currentRow.forEachIndexed { x, areas ->
        areas.forEach { area ->
          when (area) {
            is Blizzard -> {
              when (area.direction) {
                NORTH -> updateState(area, x, y - 1, x, height - 2)
                SOUTH -> updateState(area, x, y + 1, x, 1)
                EAST  -> updateState(area, x + 1, y, 1, y)
                WEST  -> updateState(area, x - 1, y, width - 2, y)
              }
            }
            Wall -> newState[y][x].add(area)
          }
        }
      }
    }

    return newState
  }

  private fun blankVersion(height: Int, width: Int): ExtractionArea {
    return mutableListOf<List<MutableList<Area>>>().also { new ->
      repeat(height) {
        new.add(
          (0 until width).map { mutableListOf() }
        )
      }
    }
  }

  sealed class Area {
    object Wall : Area()
    data class Blizzard(val direction: Direction) : Area()
  }

  enum class Direction { NORTH, SOUTH, EAST, WEST }
}