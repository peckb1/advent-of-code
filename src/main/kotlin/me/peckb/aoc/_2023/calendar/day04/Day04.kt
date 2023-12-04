package me.peckb.aoc._2023.calendar.day04

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.pow

class Day04 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::day04) { input ->
    input.sumOf { it.points() }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::day04) { input ->
    val copyData = mutableMapOf<Int, Int>()
      .apply { this[0] = 1 }
      .withDefault { 1 }

    input.forEachIndexed { index, card ->
      repeat(card.matches) { n ->
        val cardToCopy = index + n + 1
        copyData[cardToCopy] = copyData.getValue(cardToCopy) + copyData.getValue(index)
      }
      // if nobody added a copy of us, we never used our map default - so use it now ^_^
      if (!copyData.containsKey(index)) copyData[index] = copyData.getValue(index)
    }

    copyData.values.sum()
  }

  private fun day04(line: String): Card {
    val (winningData, ourData) = line.split(":").last().split(" | ")

    return Card(
      winningData.split(" ").mapNotNull { it.toIntOrNull() }.toSet(),
      ourData.split(" ").mapNotNull { it.toIntOrNull() }.toSet(),
    )
  }

  data class Card(val winningNumbers: Set<Int>, val ourNumbers: Set<Int>) {
    val matches = winningNumbers.intersect(ourNumbers).size

    fun points(): Int = (2.0).pow(matches - 1).toInt()
  }
}
