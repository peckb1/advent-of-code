package me.peckb.aoc._2023.calendar.day18

import me.peckb.aoc._2023.calendar.day18.Day18.Direction.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day18 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::digStep) { input ->
    minkowskiSum(input)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::digStep2) { input ->
    minkowskiSum(input)
  }

  private fun minkowskiSum(input: Sequence<DigStep>): Long {
    var currentX = 0L
    var currentY = 0L
    var perimeter = 0L

    // populate our convex hull
    val pointsOfConvexHull = mutableListOf<Pair<Long, Long>>()
    input.forEach { (direction, steps) ->
      when (direction) {
        UP    -> currentY -= steps
        LEFT  -> currentX -= steps
        DOWN  -> currentY += steps
        RIGHT -> currentX += steps
      }
      perimeter += steps
      pointsOfConvexHull.add(currentY to currentX)
    }

    // Shoelace Formula over the hull
    val shoelaceArea = pointsOfConvexHull.plus(pointsOfConvexHull.first()).windowed(2).fold(0L) { acc, pointPair ->
      val (y1, x1) = pointPair.first()
      val (y2, x2) = pointPair.last()
      acc + ((x1 * y2) - (x2 * y1))
    } / 2

    // minkowskiSum:
    //   Shoelace Sum of the inside +
    //   half the perimeter +
    //   the width of the edge
    val edgeWidth = 1

    return shoelaceArea + (perimeter / 2) + edgeWidth
  }

  private fun digStep(line: String): DigStep {
    val (directionStr, stepsStr, _) = line.split(" ")

    return DigStep(
      direction = Direction.fromSymbol(directionStr.first()),
      steps = stepsStr.toInt(),
    )
  }

  private fun digStep2(line: String): DigStep {
    val (_, _, colourStr) = line.split(" ")

    val encoding = colourStr.drop(2).dropLast(1)
    val stepStrHex = encoding.take(5)
    val direction = encoding.takeLast(1)

    return DigStep(
      direction = Direction.fromNumber(direction.first()),
      steps = stepStrHex.toInt(16),
    )
  }

  data class DigStep(val direction: Direction, val steps: Int)

  enum class Direction(val symbol: Char, val number: Char) {
    UP('U', '3'),
    LEFT('L', '2'),
    DOWN('D', '1'),
    RIGHT('R', '0');

    companion object {
      fun fromSymbol(s: Char) = values().first { it.symbol == s }
      fun fromNumber(s: Char) = values().first { it.number == s }
    }
  }
}
