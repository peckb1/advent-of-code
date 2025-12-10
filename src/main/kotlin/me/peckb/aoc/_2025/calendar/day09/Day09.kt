package me.peckb.aoc._2025.calendar.day09

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.awt.Polygon
import java.awt.geom.Area
import java.awt.geom.Rectangle2D
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day09 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::day09) { redTileLocations ->
    val tiles = redTileLocations.toList()

    var largestArea = -1.0

    tiles.indices.forEach { ti1 ->
      ((ti1 + 1) until tiles.size).forEach { ti2 ->
        val t1 = tiles[ti1]
        val t2 = tiles[ti2]

        val area = (abs(t1.x - t2.x) + 1) * (abs(t1.y - t2.y) + 1)

        largestArea = max(largestArea, area)
      }
    }

    largestArea.toLong()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::day09) { input ->
    val vertices = input.toList()

    val xPoints = mutableListOf<Int>()
    val yPoints = mutableListOf<Int>()

    vertices.forEach {
      xPoints.add(it.x.toInt())
      yPoints.add(it.y.toInt())
    }

    val polygon = Polygon(xPoints.toIntArray(), yPoints.toIntArray(), xPoints.size)
    val area = Area(polygon)

    val areas = mutableListOf<Pair<Long, Pair<Location, Location>>>()

    vertices.indices.forEach { ti1 ->
      ((ti1 + 1) until vertices.size).forEach { ti2 ->
        val t1 = vertices[ti1]
        val t2 = vertices[ti2]

        if (t1.x == t2.x || t1.y == t2.y) { return@forEach }

        val size = ((abs(t1.x - t2.x) + 1) * (abs(t1.y - t2.y) + 1)).toLong()

        areas.add(size to (t1 to t2))
      }
    }

    areas.sortedByDescending { it.first }.first { (_, corners) ->
      val t1 = corners.first
      val t2 = corners.second

      val minX = min(t1.x, t2.x)
      val minY = min(t1.y, t2.y)
      val width = abs(t1.x - t2.x)
      val height = abs(t1.y - t2.y)

      area.contains(Rectangle2D.Double(minX, minY, width, height))
    }.first
  }

  private fun day09(line: String) = line.split(",").map { it.toDouble() }.let { (x, y) -> Location(x, y) }
}

data class Location(val x: Double, val y: Double)