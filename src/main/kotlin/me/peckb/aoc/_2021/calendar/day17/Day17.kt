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
    val area = fetchArea(input)

    val lowestMultiJumpX = (0..area.xMin).first { triangleNumber(it) in area.xRange }
    val highestMultiJumpX = area.xMax / 2

    val singleJumpValues = area.xRange
    val multiJumpValues = (lowestMultiJumpX..highestMultiJumpX)
    val validXValues = mutableListOf<Int>().apply {
      addAll(singleJumpValues)
      addAll(multiJumpValues)
    }

    val minYValue= area.yMin
    val maxYValue = abs(area.yMin) - 1

    val validTrajectories = validXValues.associateWith { x ->
      findValidTrajectories(x, minYValue..maxYValue, area)
    }

    validTrajectories.values.sumOf { it.size }
  }

  private fun fetchArea(input: String): Area {
    val area = input.split("target area: ").last()
    val (xData, yData) = area.split(", ").map { it.split("=" ).last() }

    val (xMin, xMax) = xData.split("..").map { it.toInt() }
    val (yMin, yMax) = yData.split("..").map { it.toInt() }

    return Area(xMin, xMax, yMin, yMax)
  }

  private fun findValidTrajectories(xVelocity: Int, validYRange: IntRange, area: Area): List<Int> {
    return validYRange.filter { y ->
      var xSpeed = xVelocity
      var ySpeed = y
      var posX = xSpeed
      var posY = ySpeed

      while (posX <= area.xMax && posY >= area.yMin) {
        if (posX in area.xRange && posY in area.yRange) return@filter true

        xSpeed = max(0, xSpeed - 1)
        ySpeed -= 1
        posX += xSpeed
        posY += ySpeed
      }

      return@filter false
    }
  }

  private fun triangleNumber(n: Int) = ((n + 1) * n) / 2

  data class Area(val xMin: Int, val xMax: Int, val yMin: Int, val yMax: Int) {
    val xRange = (xMin..xMax)
    val yRange = (yMin..yMax)
  }
}
