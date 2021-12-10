package me.peckb.aoc._2021.calendar.day10

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import java.util.ArrayDeque
import javax.inject.Inject

class Day10 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    private val PAIRINGS = mapOf(
      '<' to '>',
      '(' to ')',
      '[' to ']',
      '{' to '}'
    )

    private val CORRUPTED_COSTS = mapOf(
      ')' to 3,
      ']' to 57,
      '}' to 1197,
      '>' to 25137
    ).withDefault { 0 }

    private val INCOMPLETE_COSTS = mapOf(
      ')' to 1,
      ']' to 2,
      '}' to 3,
      '>' to 4
    ).withDefault { 0 }
  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val badCharacters = input.mapNotNull { line ->
      val stack = ArrayDeque<Char>()
      var corruptedSymbol: Char? = null

      line.asSequence().forEach { symbol ->
        when (symbol) {
          '<', '[', '{', '(' -> stack.push(symbol)
          '>', ']', '}', ')' -> stack.pop().let { PAIRINGS[it] }.let { popped ->
            if (symbol != popped) {
              corruptedSymbol = symbol
              return@forEach
            }
          }
          else -> throw Exception("Unexpected Input $symbol")
        }
      }

      corruptedSymbol
    }

    badCharacters.sumOf { CORRUPTED_COSTS.getValue(it) }
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
            if(symbol == PAIRINGS[popped]) {
              null
            } else {
              symbol
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
        (acc * 5 + INCOMPLETE_COSTS[PAIRINGS[char]]!!)
      }
    }.sorted()

    allCosts[allCosts.size / 2]
  }

  private fun day10(line: String) = 4
}
