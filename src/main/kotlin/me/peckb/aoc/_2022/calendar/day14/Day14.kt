package me.peckb.aoc._2022.calendar.day14

import me.peckb.aoc._2022.calendar.day14.Day14.CaveSubstance.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max
import kotlin.math.min

class Day14 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::rockPath) { input ->
    val (caves, maxY, minX, maxX) = populateCaves(input)
    val xRange = (minX .. maxX)

    var sandFellIntoTheAbyss = false
    while(!sandFellIntoTheAbyss) {
      var sandX = SPOUT_X
      var sandY = SPOUT_Y
      var sandPlaced = false
      while(!sandPlaced) {
        if (sandX !in xRange || sandY >= maxY) { sandPlaced = true; sandFellIntoTheAbyss = true }
        else {
          if (caves[sandY + 1][sandX] is Empty) { sandY++ }
          else if (caves[sandY + 1][sandX - 1] is Empty) { sandY++; sandX-- }
          else if (caves[sandY + 1][sandX + 1] is Empty) { sandY++; sandX++ }
          else { sandPlaced = true; caves[sandY][sandX] = Sand }
        }
      }
    }

    caves.countSand()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::rockPath) { input ->
    val caveData = populateCaves(input)
    val (caves, maxY) = caveData

    // put in that "infinite" wall
    caves[maxY + 2].indices.forEach { x -> caves[maxY + 2][x] = Wall }

    // sand falling time
    var sandBlockedTheHole = false
    while(!sandBlockedTheHole) {
      var sandX = SPOUT_X
      var sandY = SPOUT_Y
      var sandPlaced = false
      while(!sandPlaced) {
        if (caves[sandY + 1][sandX] is Empty) { sandY++ }
        else if (caves[sandY + 1][sandX - 1] is Empty) { sandY++; sandX-- }
        else if (caves[sandY + 1][sandX + 1] is Empty) { sandY++; sandX++ }
        else {
          sandPlaced = true
          caves[sandY][sandX] = Sand
          if (sandY == SPOUT_Y && sandX == SPOUT_X) { sandBlockedTheHole = true }
        }
      }
    }

    caves.countSand()
  }

  private fun rockPath(line: String): List<Line> =
    line.split(" -> ").windowed(2).map { (start, end) ->
      Line(
        start.split(",").let { (x, y) -> Point(x.toInt(), y.toInt()) },
        end.split(",").let { (x, y) -> Point(x.toInt(), y.toInt()) }
      )
    }

  private fun populateCaves(input: Sequence<List<Line>>): CaveData {
    val height = CAVE_HEIGHT
    val width = CAVE_WIDTH

    val caves = Array(height) { Array<CaveSubstance>(width) { Empty } }
    caves[SPOUT_Y][SPOUT_X] = Spout

    var globalMinX = Int.MAX_VALUE
    var globalMaxX = Int.MIN_VALUE
    var globalMaxY = Int.MIN_VALUE


    input.forEach { lines ->
      lines.forEach { (startPoint, endPoint) ->
        val minY = min(startPoint.y, endPoint.y)
        val maxY = max(startPoint.y, endPoint.y)
        val minX = min(startPoint.x, endPoint.x)
        val maxX = max(startPoint.x, endPoint.x)

        globalMaxX = max(globalMaxX, maxX)
        globalMaxY = max(globalMaxY, maxY)
        globalMinX = min(globalMinX, minX)

        (minX..maxX).forEach { x -> (minY..maxY).forEach { y -> caves[y][x] = Wall } }
      }
    }

    return CaveData(caves, globalMaxY, globalMinX, globalMaxX)
  }

  data class CaveData(val caves: Array<Array<CaveSubstance>>, val maxY: Int, val minX: Int, val maxX: Int)

  data class Point(val x: Int, val y: Int)

  data class Line(val start: Point, val end: Point)

  sealed class CaveSubstance(private val representation: String) {
    object Wall : CaveSubstance("#")
    object Empty : CaveSubstance(".")
    object Spout : CaveSubstance("+")
    object Sand : CaveSubstance(" ")

    override fun toString(): String = representation
  }

  companion object {
    private const val SPOUT_Y = 0
    private const val SPOUT_X = 500

    private const val CAVE_WIDTH = 1000
    private const val CAVE_HEIGHT = 200
  }

  private fun Array<Array<CaveSubstance>>.countSand() = sumOf { row -> row.count { it is Sand } }
}
