package me.peckb.aoc._2021.calendar.day13

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.Int.Companion.MIN_VALUE
import kotlin.math.max

class Day13 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    const val ACTIVE = '#'
    const val INACTIVE = ' '
  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val (dotLines, foldInstructions) = parseInput(input)
    val (dots, maxX, maxY) = generateDots(dotLines)
    val paper = generatePaper(dots, maxX, maxY)

    val (updatedX, updatedY) = foldPaper(paper, foldInstructions.take(1))

    (0 until updatedY).sumOf { y ->
      (0 until updatedX).count { x ->
        paper[y][x] == ACTIVE
      }
    }
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val (dotLines, foldInstructions) = parseInput(input)
    val (dots, maxX, maxY) = generateDots(dotLines)
    val paper = generatePaper(dots, maxX, maxY)

    val (updatedX, updatedY) = foldPaper(paper, foldInstructions)

    val codes = Array(updatedY + 1) { Array(updatedX + 1) { INACTIVE } }
    (0 until updatedY).forEach { y ->
      (0 until updatedX).forEach { x ->
        codes[y][x] = paper[y][x]
      }
    }

    codes.joinToString("\n") { it.joinToString("") }
  }

  private fun generatePaper(dots: List<Dot>, maxX: Int, maxY: Int) =
    Array(maxY + 1) { Array(maxX + 1) { INACTIVE } }.apply {
      dots.forEach { (x, y) -> this[y][x] = ACTIVE }
    }

  private fun parseInput(input: Sequence<String>): Pair<List<String>, List<String>> {
    val dotLines = mutableListOf<String>()
    val foldInstructions = mutableListOf<String>()

    var doingDots = true
    input.forEach {
      if (it.isEmpty()) {
        doingDots = false
      } else {
        if (doingDots) {
          dotLines.add(it)
        } else {
          foldInstructions.add(it)
        }
      }
    }

    return dotLines to foldInstructions
  }

  private fun foldPaper(paper: Array<Array<Char>>, foldInstructions: List<String>): Pair<Int, Int> {
    var maxX = paper[0].size
    var maxY = paper.size

    foldInstructions.forEach {
      val (direction, foldIndex) = it.split("fold along ").last().split("=")
      if (direction == "y") {
        ((foldIndex.toInt() + 1) until maxY).forEach { paperIndex ->
          paper[paperIndex].forEachIndexed { columIndex, rowValue ->
            val sourceY = paperIndex
            val sourceX = columIndex

            val destinationY = foldIndex.toInt() - (paperIndex - foldIndex.toInt())
            val destinationX = columIndex

            paper[destinationY][destinationX] = if (paper[destinationY][destinationX] == ACTIVE || rowValue == ACTIVE) {
              ACTIVE
            } else {
              INACTIVE
            }
            paper[sourceY][sourceX] = INACTIVE
          }
        }
        maxY = foldIndex.toInt()
      } else {
        ((foldIndex.toInt() + 1) until maxX).forEach { columnIndex ->
          (paper.indices).forEach { rowIndex ->
            val sourceY = rowIndex
            val sourceX = columnIndex

            val destinationY = rowIndex
            val destinationX = foldIndex.toInt() - (columnIndex - foldIndex.toInt())

            paper[destinationY][destinationX] = if (paper[destinationY][destinationX] == ACTIVE || paper[sourceY][sourceX] == ACTIVE) {
              ACTIVE
            } else {
              INACTIVE
            }
            paper[sourceY][sourceX] = INACTIVE
          }
        }
        maxX = foldIndex.toInt()
      }
    }

    return maxX to maxY
  }

  private fun generateDots(dotLines: List<String>): Triple<List<Dot>, Int, Int>{
    var maxX = MIN_VALUE
    var maxY = MIN_VALUE

    val dots = dotLines.map {
      it.split(",").let { (x, y) ->
        maxX = max(maxX, x.toInt())
        maxY = max(maxY, y.toInt())

        Dot(x.toInt(), y.toInt())
      }
    }

    return Triple(dots, maxX, maxY)
  }

  data class Dot(val x: Int, val y: Int)
}
