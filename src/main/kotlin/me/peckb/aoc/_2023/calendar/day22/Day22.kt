package me.peckb.aoc._2023.calendar.day22

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day22 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::brick) { input ->
    val bricks = input.sortedBy { it.start.z }.toList().apply {
      forEachIndexed { index, brick -> brick.fall(index, this) }
    }

    findDeletableBricks(bricks).size
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val brickInput = input.toList()
    val ogBricks = brickInput.map(::brick)
      .sortedBy { it.start.z } // sort the items for falling
      .apply { forEachIndexed { index, brick -> brick.fall(index, this) } }
      .sortedBy { it.start.z } // re-sort after falling for disintegration + falling

    val deletableBricks = findDeletableBricks(ogBricks)

    val times1 = mutableListOf<Long>()
    val times2 = mutableListOf<Long>()
    val times3 = mutableListOf<Long>()

    brickInput.indices.asSequence().filterNot { deletableBricks.containsKey(ogBricks[it]) }.sumOf { indexOfBrickToRemove ->
      val s1 = System.currentTimeMillis()
      val bricksToMessWith = ogBricks.map { it.clone() }
      val e1 = System.currentTimeMillis()

      val s2 = System.currentTimeMillis()
      val brickToDisintegrate = bricksToMessWith[indexOfBrickToRemove]
      val bricksAfterDisintegration = bricksToMessWith.minus(brickToDisintegrate)
      val e2 = System.currentTimeMillis()

      val s3 = System.currentTimeMillis()
      bricksAfterDisintegration.withIndex().count { (index, brick) ->
        brick.fall(index, bricksAfterDisintegration)
      }.also {
        val e3 = System.currentTimeMillis()
        times1.add(e1 - s1)
        times2.add(e2 - s2)
        times3.add(e3 - s3)
      }
    }.also {
      println(times1.sum())
      println(times1)
      println(times2.sum())
      println(times2)
      println(times3.sum())
      println(times3)
    }
  }

  private fun findDeletableBricks(bricks: List<Brick>): Map<Brick, List<Brick>> {
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

    return brickToReliantMap.filter { (me, bricksThatRelyOnMe) ->
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
