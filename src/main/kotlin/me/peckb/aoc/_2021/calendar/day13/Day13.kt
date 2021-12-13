package me.peckb.aoc._2021.calendar.day13

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.Int.Companion.MIN_VALUE
import kotlin.math.max

class Day13 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::day13) { input ->
    val (dotLines, foldInstructions) = parseInput(input)
    val (dots, maxX, maxY) = generateDots(dotLines)

    val paper = Array(maxY + 1) { Array(maxX + 1) { ' ' } }.apply {
      dots.forEach { (x, y) -> this[y][x] = '#' }
    }

    val (updatedX, updatedY) = foldPaper(paper, foldInstructions.take(1))

    (0 until updatedY).sumOf { y ->
      (0 until updatedX).count { x ->
        paper[y][x] == '#'
      }
    }
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

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day13) { input ->
    val dotLines = mutableListOf<String>()
    val foldInstructions = mutableListOf<String>()

    var maxX = MIN_VALUE
    var maxY = MIN_VALUE

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

    val dots = dotLines.map {
      it.split(",").let { (x, y) ->
        maxX = max(maxX, x.toInt())
        maxY = max(maxY, y.toInt())

        Dot(x.toInt(), y.toInt())
      }
    }

    val paper = Array(maxY + 1) { Array(maxX + 1) { ' ' } }

    dots.forEach { (x, y) ->
      paper[y][x] = '#'
    }

    val (mX, mY) = foldPaper(paper, foldInstructions)
    val codes = Array(mY + 1) { Array(mX + 1) { ' ' } }

    (0 until mY).forEach { y ->
      (0 until mX).forEach { x ->
        codes[y][x] = paper[y][x]
      }
      println()
    }

    codes.joinToString("\n") { it.joinToString("") }
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

            paper[destinationY][destinationX] = if (paper[destinationY][destinationX] == '#' || rowValue == '#') {
              '#'
            } else {
              ' '
            }
            paper[sourceY][sourceX] = ' '
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

            paper[destinationY][destinationX] = if (paper[destinationY][destinationX] == '#' || paper[sourceY][sourceX] == '#') {
              '#'
            } else {
              ' '
            }
            paper[sourceY][sourceX] = ' '
          }
        }
        maxX = foldIndex.toInt()
      }
    }

    return maxX to maxY
  }

  private fun day13(line: String) = line

  data class Dot(val x: Int, val y: Int)

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

}
