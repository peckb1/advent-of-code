package me.peckb.aoc._2021.calendar.day13

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.Int.Companion.MIN_VALUE
import kotlin.math.max

class Day13 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    const val ACTIVE = true
    const val INACTIVE = false
    const val FOLD_PREFIX = "fold along "
  }

  fun oneFoldCount(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val (dotLines, foldInstructions) = parseInput(input)
    val paper = fillPaper(generatePaperData(dotLines))

    val (updatedX, updatedY) = foldPaper(paper, foldInstructions.take(1))

    (0 until updatedY).sumOf { y ->
      (0 until updatedX).count { x ->
        paper[y][x] == ACTIVE
      }
    }
  }

  fun everyFoldCode(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val (dotLines, foldInstructions) = parseInput(input)
    val paper = fillPaper(generatePaperData(dotLines))

    val (updatedX, updatedY) = foldPaper(paper, foldInstructions)

    val codes = Array(updatedY + 1) { Array(updatedX + 1) { ' ' } }
    (0 until updatedY).forEach { y ->
      (0 until updatedX).forEach { x ->
        codes[y][x] = if (paper[y][x]) '#' else ' '
      }
    }

    codes.joinToString("\n") { it.joinToString("") }
  }

  private fun fillPaper(paper: Paper) =
    Array(paper.y + 1) { Array(paper.x + 1) { INACTIVE } }.apply {
      paper.dots.forEach { (x, y) -> this[y][x] = ACTIVE }
    }

  private fun parseInput(input: Sequence<String>): Pair<List<String>, List<String>> {
    val dotLines = mutableListOf<String>()
    val foldInstructions = mutableListOf<String>()

    input.forEach {
      if (it.startsWith(FOLD_PREFIX)) {
        foldInstructions.add(it.split(FOLD_PREFIX).last())
      } else if (it.isNotEmpty()) {
        dotLines.add(it)
      }
    }

    return dotLines to foldInstructions
  }

  private fun foldPaper(paper: Array<Array<Boolean>>, foldInstructions: List<String>): Pair<Int, Int> {
    var maxX = paper[0].size
    var maxY = paper.size

    fun fold(sourceX: Int, sourceY: Int, destinationX: Int, destinationY: Int) {
      if (paper[sourceY][sourceX] == ACTIVE) paper[destinationY][destinationX] = ACTIVE
    }

    foldInstructions.forEach {
      val (direction, foldIndexString) = it.split("=")
      val foldIndex = foldIndexString.toInt()
      if (direction == "y") {
        ((foldIndex + 1) until maxY).forEach { y ->
          repeat(maxX) { x->
            fold(x, y, x, 2 * foldIndex - y)
          }
        }
        maxY = foldIndex
      } else {
        ((foldIndex + 1) until maxX).forEach { x ->
          repeat(maxY) { y ->
            fold(x, y, 2 * foldIndex - x, y)
          }
        }
        maxX = foldIndex
      }
    }

    return maxX to maxY
  }

  private fun generatePaperData(dotLines: List<String>): Paper {
    var maxX = MIN_VALUE
    var maxY = MIN_VALUE

    val dots = dotLines.map { line ->
      line.split(",").map{ it.toInt() }.let { (x, y) ->
        maxX = max(maxX, x)
        maxY = max(maxY, y)

        Dot(x, y)
      }
    }

    return Paper(dots, maxX, maxY)
  }

  data class Dot(val x: Int, val y: Int)

  data class Paper(val dots: List<Dot>, val x: Int, val y: Int)
}
