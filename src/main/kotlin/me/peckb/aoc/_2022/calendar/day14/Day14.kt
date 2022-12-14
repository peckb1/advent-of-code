package me.peckb.aoc._2022.calendar.day14

import me.peckb.aoc._2022.calendar.day14.Day14.CaveSubstance.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalStateException
import kotlin.math.max
import kotlin.math.min

class Day14 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::rockPath) { input ->
    val width = 1000
    val height = 500

    val spoutX = 500
    val spoutY = 0

    val caves = Array(height) { Array<CaveSubstance>(width) { Empty } }
    caves[spoutY][spoutX] = CaveSubstance.Spout

    var globalMinX = Int.MAX_VALUE
    var globalMinY = 0
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

        (minX .. maxX) .forEach { x ->
          (minY .. maxY).forEach { y ->
            caves[y][x] = Wall
          }
        }
      }
    }

    fun printCaves() {
      caves.forEachIndexed { y, row ->
        if (y <= globalMaxY + 2) {
          (globalMinX - 2..globalMaxX + 2).forEach { x ->
            print(row[x])
          }
          println()
        }
      }
      println()
    }

    // sand falling time
    var sandFellIntoTheAbyss = false
    while(!sandFellIntoTheAbyss) {
//      printCaves()
      var sandX = spoutX
      var sandY = spoutY
      var sandPlaced = false
      while(!sandPlaced) {
        if (sandX !in (globalMinX .. globalMaxX) || sandY >= globalMaxY) {
          sandPlaced = true
          sandFellIntoTheAbyss = true
        } else {
          if (caves[sandY + 1][sandX] is Empty) {
            sandY++
          } else if (caves[sandY + 1][sandX - 1] is Empty) {
            sandY++
            sandX--
          } else if (caves[sandY + 1][sandX + 1] is Empty) {
            sandY++
            sandX++
          } else {
            sandPlaced = true
            caves[sandY][sandX] = Sand
          }
        }
      }
    }

    printCaves()

    caves.sumOf { row ->
      row.count { it is Sand }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::rockPath) { input ->
    val width = 1000
    val height = 500

    val spoutX = 500
    val spoutY = 0

    val caves = Array(height) { Array<CaveSubstance>(width) { Empty } }
    caves[spoutY][spoutX] = CaveSubstance.Spout

    var globalMinX = Int.MAX_VALUE
    var globalMinY = 0
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

        (minX .. maxX) .forEach { x ->
          (minY .. maxY).forEach { y ->
            caves[y][x] = Wall
          }
        }
      }
    }

    (0 until width).forEach { x -> caves[globalMaxY + 2][x] = Wall}

    fun printCaves() {
      caves.forEachIndexed { y, row ->
        if (y <= globalMaxY + 3) {
          (0 until width).forEach { x ->
            print(row[x])
          }
          println()
        }
      }
      println()
    }

    // sand falling time
    var sandBlockedTheHole = false
    while(!sandBlockedTheHole) {
      var sandX = spoutX
      var sandY = spoutY
      var sandPlaced = false
      while(!sandPlaced) {
        if (caves[sandY + 1][sandX] is Empty) {
          sandY++
        } else if (caves[sandY + 1][sandX - 1] is Empty) {
          sandY++
          sandX--
        } else if (caves[sandY + 1][sandX + 1] is Empty) {
          sandY++
          sandX++
        } else {
          sandPlaced = true
          caves[sandY][sandX] = Sand
          if (sandY == spoutY && sandX == spoutX) {
            sandBlockedTheHole = true
          }
        }
      }
    }

    printCaves()

    caves.sumOf { row ->
      row.count { it is Sand }
    }
  }

  private fun rockPath(line: String): List<Line> =
    line.split(" -> ").windowed(2).map { (start, end) ->
      Line(
        start.split(",").let { (x, y) -> Point(x.toInt(), y.toInt()) },
        end.split(",").let { (x, y) -> Point(x.toInt(), y.toInt()) }
      )
    }

  data class Point(val x: Int, val y: Int)

  data class Line(val start: Point, val end: Point)

  sealed class CaveSubstance(private val representation: String) {
    object Wall : CaveSubstance("#")
    object Empty : CaveSubstance(".")
    object Spout : CaveSubstance("+")
    object Sand : CaveSubstance("o")

    override fun toString(): String {
      return representation
    }
  }
}
