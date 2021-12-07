package me.peckb.aoc._2021.calendar.day07

import me.peckb.aoc._2021.generators.InputGenerator
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day07 @Inject constructor(private val generatorFactory: InputGenerator.InputGeneratorFactory) {
  fun findSimpleCrabCost(fileName: String) = generatorFactory.forFile(fileName).readOne { data ->
    findMinCrabCost(data) { (crabPosition, desiredPosition) ->
      abs(desiredPosition - crabPosition)
    }
  }

  fun findComplexCrabCost(fileName: String) = generatorFactory.forFile(fileName).readOne { data ->
    findMinCrabCost(data) { (crabPosition, desiredPosition) ->
      (1 .. abs(desiredPosition - crabPosition)).sum()
    }
  }

  private fun findMinCrabCost(movementStrings: String, movementCost: (Movement) -> Int): Int {
    var min = Int.MAX_VALUE
    var max = Int.MIN_VALUE
    val horizontalPositions = movementStrings.split(",").map {
      it.toInt().also { position ->
        min = min(position, min)
        max = max(position, max)
      }
    }

    var cost = Int.MAX_VALUE
    (min .. max).forEach lit@{ desiredPosition ->
      val posCost = horizontalPositions.sumOf { movementCost(Movement(it, desiredPosition)) }
      cost = min(cost, posCost)
    }

    return cost
  }

  private data class Movement(val crabPosition: Int, val desiredPosition: Int)
}
