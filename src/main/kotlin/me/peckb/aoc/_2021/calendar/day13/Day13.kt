package me.peckb.aoc._2021.calendar.day13

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.Int.Companion.MIN_VALUE
import kotlin.math.max

class Day13 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::day13) { input ->
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

    foldInstructions.first().let {
      val (direction, foldIndex) = it.split("fold along ").last().split("=")
      if (direction == "y") {
        maxY = foldIndex.toInt()
        ((foldIndex.toInt() + 1) until paper.size).forEach { paperIndex ->
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
      } else {
        maxX = foldIndex.toInt()
        ((foldIndex.toInt() + 1) until paper[0].size).forEach { columnIndex ->
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
      }
    }

    val maxSpaces = maxX * maxY

    var count = 0
    (0 until paper.size).forEach { y ->
      (0 until paper[y].size).forEach { x ->
        if (paper[y][x] == '#') count++
      }
    }

    count
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

            if (sourceX == -1 || sourceX == -1 || destinationY == -1 || destinationX == -1) {
              val x = 3
            }

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

    val codes = Array(maxY + 1) { Array(maxX + 1) { ' ' } }

    (0 until maxY).forEach { y ->
      (0 until maxX).forEach { x ->
        codes[y][x] = paper[y][x]
      }
      println()
    }

    codes.joinToString("\n") { it.joinToString("") }
  }

  private fun day13(line: String) = line

  data class Dot(val x: Int, val y: Int)
}
