package me.peckb.aoc._2023.calendar.day22

import me.peckb.aoc._2023.calendar.day22.Day22.Shape.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max
import kotlin.math.min

class Day22 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::snapshot) { input ->
    val bricks = input.sortedBy { minOf(it.start.z, it.end.z) }.toList()

    bricks.forEachIndexed { index, brick ->
      brick.fall(index, bricks)
    }

    val brickByZHeight = bricks.groupBy { min(it.start.z, it.end.z) }.withDefault { emptyList() }
    val brickToSupportsMap: MutableMap<Brick, List<Brick>> = mutableMapOf()
    val brickToReliantMap: MutableMap<Brick, List<Brick>> = mutableMapOf()

    brickByZHeight.forEach { (_, bricksToCheck) ->
      bricksToCheck.forEach { me ->
        val possibleReliantBricks = brickByZHeight.getValue(max(me.start.z, me.end.z) + 1)

        val bricksThatRelyOnMe = possibleReliantBricks.filter { b ->
          val xOverlap = (b.start.x <= me.end.x && b.end.x >= me.start.x)
          val yOverlap = (b.start.y <= me.end.y && b.end.y >= me.start.y)

          xOverlap && yOverlap
        }

        brickToReliantMap.merge(me, bricksThatRelyOnMe) { a, b -> a + b }
        bricksThatRelyOnMe.forEach { brick ->
          brickToSupportsMap.merge(brick, listOf(me)) { a, b -> a + b }
        }
      }
    }

    brickToReliantMap.count { (me, bricksThatRelyOnMe) ->
      me.canBeDeleted(bricksThatRelyOnMe, brickToSupportsMap)
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::snapshot) { input ->
    -1
  }

  private fun snapshot(line: String): Brick {
    val (start, end) = line.split("~").map { coords ->
      val (x, y, z) = coords.split(",").map { it.toInt() }
      Position(x, y, z)
    }

    val id = options[index]
    index++
    index = index % 26

    return Brick(id, start, end)
  }

  companion object {
    var index = 0
    var options = "abcdefghijklmnopqrstubwxyz"


  }

  enum class Shape {
    VERTICAL, HORIZONTAL_X, HORIZONTAL_Y, CUBE
  }

  data class Brick(val label: Char, val start: Position, val end: Position) {
    fun fall(index: Int, bricks: List<Brick>) {
      val me = bricks[index]

      val bricksThatHaveAlreadyFallen = if (index == 0) {
        emptyList()
      } else {
        bricks.subList(0, index).sortedBy { maxOf(it.start.z, it.end.z) }
      }

      val currentMinZ = min(start.z, end.z)
      var newZ        = max(min(start.z, end.z), 1)

      when (me.shape) {
        VERTICAL, CUBE -> {
          val myNewBottom = bricksThatHaveAlreadyFallen.asReversed().firstOrNull { b ->
            val xOverlap = (b.start.x..b.end.x).contains(me.start.x)
            val yOverlap = (b.start.y..b.end.y).contains(me.start.y)

            xOverlap && yOverlap
          }
          newZ = (myNewBottom?.let { max(it.start.z, it.end.z) } ?: 0) + 1
        }
        HORIZONTAL_X   -> {
          val myNewBottom = bricksThatHaveAlreadyFallen.asReversed().firstOrNull { b ->
            val yOverlap = (b.start.y..b.end.y).contains(me.start.y)
            val xOverlap = (b.start.x <= me.end.x && b.end.x >= me.start.x)

            xOverlap && yOverlap
          }
          newZ = (myNewBottom?.let { max(it.start.z, it.end.z) } ?: 0) + 1
        }
        HORIZONTAL_Y   -> {
          val myNewBottom = bricksThatHaveAlreadyFallen.asReversed().firstOrNull { b ->
            val xOverlap = (b.start.x..b.end.x).contains(me.start.x)
            val yOverlap = (b.start.y <= me.end.y && b.end.y >= me.start.y)

            xOverlap && yOverlap
          }
          newZ = (myNewBottom?.let { max(it.start.z, it.end.z) } ?: 0) + 1
        }
      }

      val zToDrop = currentMinZ - newZ
      start.drop(zToDrop)
      end.drop(zToDrop)
    }

    val shape: Shape = when {
      start.z != end.z -> VERTICAL
      start.x != end.x -> HORIZONTAL_X
      start.y != end.y -> HORIZONTAL_Y
      else             -> CUBE
    }

    override fun toString(): String {
      return "$label: $start ~ $end"
    }

    fun canBeDeleted(
      bricksThatRelyOnMe: List<Brick>,
      brickToSupportsMap: MutableMap<Brick, List<Brick>>
    ): Boolean {
      if (bricksThatRelyOnMe.isEmpty()) {
//        println("$label can be deleted")
        return true
      }

      return bricksThatRelyOnMe.all { brick ->
        val otherBricksSupportingBrickISupport = brickToSupportsMap[brick]?.filter { it != this } ?: emptyList()
        otherBricksSupportingBrickISupport.isNotEmpty()
      }//.also { if (it) println("$label can be deleted") }
    }
  }

  data class Position(var x: Int, var y: Int, var z: Int) {
    fun drop(deltaZ: Int) { z -= deltaZ }

    override fun toString(): String {
      return "[$x, $y, $z]"
    }
  }
}
