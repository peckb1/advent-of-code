package me.peckb.aoc._2023.calendar.day22

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day22 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::brick) { input ->
    val bricks = input.sortedBy { it.start.z }.toList()

    bricks.forEachIndexed { index, brick -> brick.fall(index, bricks) }

    val brickByZHeight = bricks.groupBy { it.start.z }.withDefault { emptyList() }
    val brickToSupportsMap: MutableMap<Brick, List<Brick>> = mutableMapOf()
    val brickToReliantMap: MutableMap<Brick, List<Brick>> = mutableMapOf()

    brickByZHeight.forEach { (_, bricksToCheck) ->
      bricksToCheck.forEach { me ->
        val possibleReliantBricks = brickByZHeight.getValue(me.end.z + 1)

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

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val brickInput = input.toList()
    val ogBricks = brickInput.map(::brick)
      .sortedBy { it.start.z } // sort the items for falling
      .apply { forEachIndexed { index, brick -> brick.fall(index, this) } }
      .sortedBy { it.start.z } // re-sort after falling for disintegration + falling

    brickInput.indices.sumOf { indexOfBrickToRemove ->
      val bricksToMessWith = ogBricks.map { it.clone() }

      val brickToDisintegrate = bricksToMessWith[indexOfBrickToRemove]
      val bricksAfterDisintegration = bricksToMessWith.minus(brickToDisintegrate)

      bricksAfterDisintegration.withIndex().count { (index, brick) ->
        brick.fall(index, bricksAfterDisintegration)
      }
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
