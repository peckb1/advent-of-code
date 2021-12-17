package me.peckb.aoc._2021.calendar.day17

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max

class Day17 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {

  }

fun maxHeight(fileName: String) = generatorFactory.forFile(fileName).readOne { input ->
  val yMin = input // full string
    .split("target area: ").last() // x and y data
    .split(", ").map { it.split("=" ).last() }.last() // just the y data range
    .split("..").first().toInt() // the min Y value

  triangleNumber(yMin)
}

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readOne { input ->
    val area = input.split("target area: ").last()
    val (xData, yData) = area.split(", ").map { it.split("=" ).last() }

    val (xMin, xMax) = xData.split("..").map { it.toInt() }
    val (yMin, yMax) = yData.split("..").map { it.toInt() }

    val validXValues = (xMin..xMax).toMutableList()

    var minX = Int.MAX_VALUE
    var guessX = 0
    var tNum = triangleNumber(guessX)
    while(tNum <= xMax && minX == Int.MAX_VALUE) {
      if (tNum in (xMin..xMax)) {
        minX = guessX
      }
      guessX++
      tNum = triangleNumber(guessX)
    }

    val maxSlowX = xMax / 2

    validXValues.addAll(minX..maxSlowX)
    val maxYVelocity = abs(yMin - 1)
    val minYVelicity= yMin
    val actualValues = validXValues.associateWith { x ->
      findYs(x, minYVelicity, maxYVelocity, xMin, xMax, yMin, yMax)
    }


    actualValues.values.sumOf { it.size }
  }

  private fun findYs(x: Int, minY: Int, maxY: Int, xMin: Int, xMax: Int, yMin: Int, yMax: Int): List<Int> {
    return (minY..maxY).filter { y ->
      var xSpeed = x
      var ySpeed = y
      var posX = xSpeed
      var posY = ySpeed

      while (posX <= xMax && posY >= yMin) {
        if (posX in (xMin..xMax) && posY in (yMin..yMax)) {
          return@filter true
        }
        xSpeed = max(0, xSpeed - 1)
        ySpeed = ySpeed - 1
        posX = posX + xSpeed
        posY = posY + ySpeed
      }
      return@filter false
    }
  }

  private fun triangleNumber(n: Int) = ((n + 1) * n) / 2
}
