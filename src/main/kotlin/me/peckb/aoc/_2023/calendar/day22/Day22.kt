package me.peckb.aoc._2023.calendar.day22

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max
import kotlin.math.min

class Day22 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::brick) { input ->
    bricksThatCanBeDeleted(input.sortedBy { minOf(it.start.z, it.end.z) }.toList())
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val brickInput = input.toList()

    brickInput.indices.sumOf { indexOfBrickToRemove ->
      val bricks = brickInput.map(::brick).sortedBy { minOf(it.start.z, it.end.z) }
      bricks.forEachIndexed { index, brick -> brick.fall(index, bricks) }

      val brickToDisintegrate = bricks[indexOfBrickToRemove]
      val newBricks = bricks.minus(brickToDisintegrate).sortedBy { minOf(it.start.z, it.end.z) }
      val bricksThatFell = newBricks.withIndex().filter { (index, brick) ->
        brick.fall(index, newBricks)
      }

      bricksThatFell.size
    }
  }

  private fun bricksThatCanBeDeleted(bricks: List<Brick>): Int {
    bricks.forEachIndexed { index, brick -> brick.fall(index, bricks) }

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

    return brickToReliantMap.count { (me, bricksThatRelyOnMe) ->
      me.canBeDeleted(bricksThatRelyOnMe, brickToSupportsMap)
    }
  }

  private fun brick(line: String): Brick {
    val (start, end) = line.split("~").map { coords ->
      val (x, y, z) = coords.split(",").map { it.toInt() }
      Position(x, y, z)
    }

    return Brick(start, end)
  }
}
