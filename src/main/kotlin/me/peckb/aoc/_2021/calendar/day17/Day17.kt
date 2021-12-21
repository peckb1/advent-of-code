package me.peckb.aoc._2021.calendar.day17

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max

class Day17 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {

  fun maxHeight(fileName: String) = generatorFactory.forFile(fileName).readOne { input ->
    val yMin = input // full string
      .split("target area: ").last() // x and y data
      .split(", ").map { it.split("=").last() }.last() // just the y data range
      .split("..").first().toLong() // the min Y value

    triangleNumber(yMin)
  }

  fun totalNumberOfLaunchVelocities(fileName: String) = generatorFactory.forFile(fileName).readOne { input ->
    val area = fetchArea(input)

    val lowestMultiJumpX = (0..area.xMin).first { triangleNumber(it) in area.xRange }
    val highestMultiJumpX = (area.xMax / 2) + 1

    val singleJumpValues = area.xRange
    val multiJumpValues = (lowestMultiJumpX..highestMultiJumpX)
    val validXValues = mutableListOf<Long>().apply {
      addAll(singleJumpValues)
      addAll(multiJumpValues)
    }

    val minYValue = area.yMin
    val maxYValue = abs(area.yMin) - 1

    val validTrajectories = validXValues.sumOf { x ->
      findValidTrajectories(x, minYValue..maxYValue, area)
    }

    validTrajectories
  }

  private fun fetchArea(input: String): Area {
    val area = input.split("target area: ").last()
    val (xData, yData) = area.split(", ").map { it.split("=").last() }

    val (xMin, xMax) = xData.split("..").map { it.toLong() }
    val (yMin, yMax) = yData.split("..").map { it.toLong() }

    return Area(xMin, xMax, yMin, yMax)
  }

  private fun findValidTrajectories(xVelocity: Long, validYRange: LongRange, area: Area): Long {
    return validYRange.sumOf { y ->
      var xSpeed = xVelocity
      var ySpeed = y
      var posX = xSpeed
      var posY = ySpeed

      while (posX <= area.xMax && posY >= area.yMin) {
        if (posX in area.xRange && posY in area.yRange) return@sumOf 1L

        xSpeed = max(0, xSpeed - 1)
        ySpeed -= 1
        posX += xSpeed
        posY += ySpeed
      }

      return@sumOf 0L
    }
  }

  private fun triangleNumber(n: Long) = ((n + 1) * n) / 2

  data class Area(val xMin: Long, val xMax: Long, val yMin: Long, val yMax: Long) {
    val xRange = (xMin..xMax)
    val yRange = (yMin..yMax)
  }
}
