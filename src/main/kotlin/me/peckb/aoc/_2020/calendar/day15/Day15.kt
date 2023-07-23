package me.peckb.aoc._2020.calendar.day15

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day15 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  data class NumberDetails(val firstTurnSpoken: Int, var timeBeforeLast: Int, var lastTurnSpoken: Int)

  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    playGame(input.split(","), 2020)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    playGame(input.split(","), 30_000_000)
  }

  private fun playGame(baseNumbers: List<String>, turns: Int): Int {
    // map of number to last time it was spoken
    val spokenNumbers = mutableMapOf<Int, NumberDetails>()

    var lastNumberSpoken = 0
    baseNumbers.forEachIndexed { index, numberString ->
      val number = numberString.toInt()
      spokenNumbers[number] = NumberDetails(index + 1, index + 1, index + 1)
      lastNumberSpoken = number
    }

    var currentTurn = spokenNumbers.size
    while(currentTurn < turns) {
      currentTurn++
      val (firstTimeSpoken, timeBeforeLast, lastTimeSpoken) = spokenNumbers[lastNumberSpoken]!!
      lastNumberSpoken = if (firstTimeSpoken == currentTurn - 1) {
        0
      } else {
        lastTimeSpoken - timeBeforeLast
      }
      spokenNumbers[lastNumberSpoken]
        ?.also {
          it.timeBeforeLast = it.lastTurnSpoken
          it.lastTurnSpoken = currentTurn
        }
        ?: run {
          spokenNumbers[lastNumberSpoken] = NumberDetails(currentTurn, currentTurn, currentTurn)
        }
    }

    return lastNumberSpoken
  }
}
