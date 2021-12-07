package me.peckb.aoc._2021.calendar.day07

import me.peckb.aoc._2021.generators.InputGenerator
import javax.inject.Inject
import kotlin.math.abs

class Day07 @Inject constructor(private val generatorFactory: InputGenerator.InputGeneratorFactory) {
  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val horizontalPositions = input.first().split(",").map { it.toInt() }

    val min = horizontalPositions.minOf { it }
    val max = horizontalPositions.maxOf { it }

    var cost = Int.MAX_VALUE
    (min .. max).forEach { position ->
      val posCost = horizontalPositions.sumOf { abs(position - it) }
      if (posCost < cost) {
        cost = posCost
      }
    }

    cost
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val horizontalPositions = input.first().split(",").map { it.toInt() }

    val min = horizontalPositions.minOf { it }
    val max = horizontalPositions.maxOf { it }

    var cost = Int.MAX_VALUE
    (min .. max).forEach { position ->
      val posCost = horizontalPositions.sumOf {
        val moves = abs(position - it)
        (1 .. moves).sumOf {
          it
        }
        // moves
      }
      if (posCost < cost) {
        cost = posCost
      }
    }

    cost
  }



}
