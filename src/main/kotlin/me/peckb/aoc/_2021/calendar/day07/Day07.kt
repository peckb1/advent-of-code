package me.peckb.aoc._2021.calendar.day07

import me.peckb.aoc._2021.generators.InputGenerator
import javax.inject.Inject
import kotlin.Int.Companion.MAX_VALUE
import kotlin.Int.Companion.MIN_VALUE
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day07 @Inject constructor(private val generatorFactory: InputGenerator.InputGeneratorFactory) {
  fun findSimpleCrabCost(fileName: String) = generatorFactory.forFile(fileName).readOne { data ->
    findMinCrabCost(data) { it }
  }

  fun findComplexCrabCost(fileName: String) = generatorFactory.forFile(fileName).readOne { data ->
    findMinCrabCost(data) { triangleNumber(it) }
  }

  private fun findMinCrabCost(movementStrings: String, movementCost: (Int) -> Int): Int {
    val horizontalPositions = movementStrings.split(",").map { it.toInt() }
    val minPosition = horizontalPositions.minOf { it }
    val maxPosition = horizontalPositions.maxOf { it }

    return (minPosition .. maxPosition).fold(MAX_VALUE) { min, desiredPosition ->
      val posCost = horizontalPositions.sumOf { movementCost(abs(desiredPosition - it)) }
      min(min, posCost)
    }
  }

  private fun triangleNumber(n: Int) = ((n + 1) * n) / 2
}
