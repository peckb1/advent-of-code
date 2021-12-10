package me.peckb.aoc._2021.calendar.day10

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import java.util.ArrayDeque
import javax.inject.Inject

class Day10 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    val inverse = mapOf(
      '>' to '<',
      ')' to '(',
      ']' to '[',
      '}' to '{',
    )
    val otherInverse = inverse.entries.associate { (k, v) -> v to k }
    val costs = mapOf(
      ')' to 3,
      ']' to 57,
      '}' to 1197,
      '>' to 25137
    )

    val partTwoCosts = mapOf(
      ')' to 1,
      ']' to 2,
      '}' to 3,
      '>' to 4
    )
  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val badCharacters = input.mapNotNull { line ->
      val stack = ArrayDeque<Char>()
      line.mapNotNull { symbol ->
        when (symbol) {
          '<', '[', '{', '(' -> {
            stack.push(symbol)
            null
          }
          '>', ']', '}', ')' -> {
            val popped = stack.pop()
            if(!popped.equals(inverse[symbol]!!)) {
              symbol
            } else {
              null
            }
          }
          else -> throw Exception("Unexpected Input $symbol")
        }
      }.firstOrNull()
    }.toList()

    badCharacters.sumOf { costs[it]!! }
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val data = mutableMapOf<String, ArrayDeque<Char>>()

    val incompleteLines = input.forEach { line ->
      val stack = ArrayDeque<Char>()
      val corruptedCharacter = line.mapNotNull { symbol ->
        when (symbol) {
          '<', '[', '{', '(' -> {
            stack.push(symbol)
            null
          }
          '>', ']', '}', ')' -> {
            val popped = stack.pop()
            if(!popped.equals(inverse[symbol]!!)) {
              symbol
            } else {
              null
            }
          }
          else -> throw Exception("Unexpected Input $symbol")
        }
      }.firstOrNull()

      if(corruptedCharacter == null) {
        data[line] = stack
      }
    }

    val allCosts = data.values.map { remainingData ->
      remainingData.fold(0L) { acc, char ->
        (acc * 5 + partTwoCosts[otherInverse[char]]!!)
      }
    }.sorted()

    allCosts[allCosts.size / 2]
  }

  private fun day10(line: String) = 4
}
