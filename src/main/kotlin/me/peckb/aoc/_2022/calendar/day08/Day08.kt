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
    checkHeights(jungle) { (_, _, _, _, left, right, up, down) ->
      if (!left || !right || !up || !down) visible++
    }
    visible
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::treeHeights) { input ->
    val (jungle, _, _) = createJungle(input)

    var bestScenicScore = -1
    checkHeights(jungle) { (left, right, up, down, _, _, _, _) ->
      bestScenicScore = max(bestScenicScore, left * right * up * down)
    }
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

  private fun checkHeights(jungle: List<List<Int>>, treeVisibilityHandler: (TreeVisibility) -> Unit) {
    (1 until (jungle.size - 1)).forEach { y ->
      (1 until (jungle[y].size - 1)).forEach { x ->
        val me = jungle[y][x]

        var counterLeft = 1
        var foundEqualOrHigherLeft = false
        while (!foundEqualOrHigherLeft && x - counterLeft >= 0) {
          foundEqualOrHigherLeft = jungle[y][x - counterLeft] >= me
          counterLeft++
        }

        var counterRight = 1
        var foundEqualOrHigherRight = false
        while (!foundEqualOrHigherRight && x + counterRight < jungle[y].size) {
          foundEqualOrHigherRight = jungle[y][x + counterRight] >= me
          counterRight++
        }

        var counterUp = 1
        var foundEqualOrHigherUp = false
        while (!foundEqualOrHigherUp && y - counterUp >= 0) {
          foundEqualOrHigherUp = jungle[y - counterUp][x] >= me
          counterUp++
        }

        var counterDown = 1
        var foundEqualOrHigherDown = false
        while (!foundEqualOrHigherDown && y + counterDown < jungle.size) {
          foundEqualOrHigherDown = jungle[y + counterDown][x] >= me
          counterDown++
        }

        treeVisibilityHandler(TreeVisibility(
          counterLeft - 1, counterRight - 1, counterUp - 1, counterDown - 1,
          foundEqualOrHigherLeft, foundEqualOrHigherRight, foundEqualOrHigherUp, foundEqualOrHigherDown
        ))
      }
    }
  }

  data class Jungle(
    val jungle: List<List<Int>>,
    val height: Int,
    val width: Int
  )

  data class TreeVisibility(
    val visibleLeftTrees: Int,
    val visibleRightTrees: Int,
    val visibleUpTrees: Int,
    val visibleDownTrees: Int,
    val foundEqualOrHigherLeft: Boolean,
    val foundEqualOrHigherRight: Boolean,
    val foundEqualOrHigherUp: Boolean,
    val foundEqualOrHigherDown: Boolean,
  )

  private fun treeHeights(line: String) = line.toList().map { Character.getNumericValue(it) }
}
