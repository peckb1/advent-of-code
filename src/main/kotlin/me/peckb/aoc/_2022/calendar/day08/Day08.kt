package me.peckb.aoc._2022.calendar.day08

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max

class Day08 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::treeHeights) { input ->
    val (jungle, height, width) = createJungle(input)

    var visible = (height * 2) + (width * 2) - 4
    checkHeights(jungle, treeVisibilityHandler = { (left, right, up, down) ->
      if (!left || !right || !up || !down) visible++
    })
    visible
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::treeHeights) { input ->
    val (jungle, _, _) = createJungle(input)

    var bestScenicScore = -1
    checkHeights(jungle, treeVisibilityCounterHandler = { (left, right, up, down) ->
      bestScenicScore = max(bestScenicScore, left * right * up * down)
    })
    bestScenicScore
  }

  private fun createJungle(input: Sequence<List<Int>>): Jungle {
    val jungle = mutableListOf<MutableList<Int>>()
    input.forEach { treeHeights ->
      val row = mutableListOf<Int>().apply {
        treeHeights.forEach { treeHeight -> add(treeHeight) }
      }
      jungle.add(row)
    }

    return Jungle(jungle, jungle.size, jungle[0].size)
  }

  private fun checkHeights(
    jungle: List<List<Int>>,
    treeVisibilityHandler: ((TreeVisibility) -> Unit)? = null,
    treeVisibilityCounterHandler: ((TreeVisibilityCounts) -> Unit)? = null
  ) {
    (1 until (jungle.size - 1)).forEach { y ->
      (1 until (jungle[y].size - 1)).forEach { x ->
        val me = jungle[y][x]
        val yRange = jungle.indices
        val xRange = jungle[y].indices

        fun heightCheck(nextY: (Int) -> Int, nextX: (Int) -> Int): Pair<Int, Boolean> {
          var visibleTrees = 1
          var equalOrGreaterTree = false
          var newY = nextY(visibleTrees)
          var newX = nextX(visibleTrees)
          while (!equalOrGreaterTree && yRange.contains(newY) && xRange.contains(newX)) {
            equalOrGreaterTree = jungle[newY][newX] >= me
            visibleTrees++
            newY = nextY(visibleTrees)
            newX = nextX(visibleTrees)
          }
          return (visibleTrees - 1) to equalOrGreaterTree
        }

        val (visibleL, foundL) = heightCheck({ y }, { x - it })
        val (visibleR, foundR) = heightCheck({ y }, { x + it })
        val (visibleU, foundU) = heightCheck({ y - it }, { x })
        val (visibleD, foundD) = heightCheck({ y + it }, { x })

        treeVisibilityHandler?.invoke(TreeVisibility(foundL, foundR, foundU, foundD))
        treeVisibilityCounterHandler?.invoke(TreeVisibilityCounts(visibleL, visibleR, visibleU, visibleD))
      }
    }
  }

  data class Jungle(
    val jungle: List<List<Int>>,
    val height: Int,
    val width: Int
  )

  data class TreeVisibilityCounts(
    val visibleLeftTrees: Int,
    val visibleRightTrees: Int,
    val visibleUpTrees: Int,
    val visibleDownTrees: Int,
  )

  data class TreeVisibility(
    val foundEqualOrHigherLeft: Boolean,
    val foundEqualOrHigherRight: Boolean,
    val foundEqualOrHigherUp: Boolean,
    val foundEqualOrHigherDown: Boolean,
  )

  private fun treeHeights(line: String) = line.toList().map { Character.getNumericValue(it) }
}
