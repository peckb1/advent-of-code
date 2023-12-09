package me.peckb.aoc._2023.calendar.day09

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day09 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::history) { input ->
    findHistoryItemSum(input, List<Long>::last, Long::plus)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::history) { input ->
    findHistoryItemSum(input, List<Long>::first, Long::minus)
  }

  private fun history(line: String) = line.split(" ").map { it.toLong() }

  private fun findHistoryItemSum(
    input: Sequence<List<Long>>,
    itemOfInterest: (List<Long>) -> Long,
    combiner: (Long, Long) -> Long
  ): Long {
    return input.map { history ->
      val patterns = mutableListOf<List<Long>>()
      var currentData = history
      while(!currentData.all { it == 0L }) {
        val nextPattern = mutableListOf<Long>()
        currentData
          .windowed(2)
          .forEach { (a, b) -> nextPattern.add(b - a) }
        currentData = nextPattern
        patterns.add(currentData)
      }

      var newItem = 0L
      patterns
        .asReversed()
        .forEach { pattern -> newItem = combiner(itemOfInterest(pattern), newItem) }
      combiner(itemOfInterest(history), newItem)
    }.sum()
  }
}
