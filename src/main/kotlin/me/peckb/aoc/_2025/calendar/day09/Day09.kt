package me.peckb.aoc._2025.calendar.day09

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import org.apache.commons.math3.geometry.euclidean.twod.hull.ConvexHullGenerator2D
import org.apache.commons.math3.geometry.euclidean.twod.hull.MonotoneChain
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max


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
    val vertices = input.map { Vector2D(it.x, it.y) }.toList()
    val polygonSet = PolygonsSet(1e-3, *vertices.toTypedArray())
    val hullGenerator: ConvexHullGenerator2D = MonotoneChain()

    val areas = mutableListOf<Pair<Long, List<Vector2D>>>()

    vertices.indices.forEach { ti1 ->
      ((ti1 + 1) until vertices.size).forEach { ti2 ->
        val t1 = vertices[ti1]
        val t2 = vertices[ti2]

        if (t1.x == t2.x || t1.y == t2.y) { return@forEach }

        val size = ((abs(t1.x - t2.x) + 1) * (abs(t1.y - t2.y) + 1)).toLong()
        val i1 = Vector2D(t1.x, t2.y)
        val i2 = Vector2D(t2.x, t1.y)

        areas.add(size to listOf(t1, i1, t2, i2))
      }
    }

    areas.sortedByDescending { it.first }.first { (_, vertices) ->
      polygonSet.contains(hullGenerator.generate(vertices).createRegion())
    }.first
  }

  private fun day09(line: String) = line.split(",").map { it.toDouble() }.let { (x, y) -> Location(x, y) }
}

data class Location(val x: Double, val y: Double)