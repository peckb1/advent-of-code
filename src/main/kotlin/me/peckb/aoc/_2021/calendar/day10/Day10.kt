package me.peckb.aoc._2021.calendar.day10

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
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

  fun findCorruptedCost(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val badCharacters = input.mapNotNull { line ->
      findIncompleteData(line).fold({ it }, { null })
    }

    badCharacters.sumOf { CORRUPTED_COSTS.getValue(it) }
  }

  fun findIncompleteCost(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val incompleteStacks = input.mapNotNull { findIncompleteData(it).orNull() }

    val sortedCosts = incompleteStacks.map { remainingData ->
      remainingData.fold(0L) { cost, symbol ->
        (cost * 5 + (INCOMPLETE_COSTS[PAIRINGS[symbol]] ?: 0))
      }
    }.sorted().toList()

    sortedCosts[sortedCosts.size / 2]
  }

  private fun findIncompleteData(line: String): Either<Char, ArrayDeque<Char>> {
    val stack = ArrayDeque<Char>()

    line.forEach { symbol ->
      when (symbol) {
        '<', '[', '{', '(' -> stack.push(symbol)
        '>', ']', '}', ')' -> stack.pop()
          .let { PAIRINGS[it] }
          .let { if (symbol != it) return Left(symbol) }
        else -> throw Exception("Unexpected Input $symbol")
      }
    }

    return Right(stack)
  }
}
