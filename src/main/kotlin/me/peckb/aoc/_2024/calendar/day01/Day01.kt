package me.peckb.aoc._2024.calendar.day01

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.abs

class Day01 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::twoLists) { input ->
    val firstList = mutableListOf<Long>()
    val secondList = mutableListOf<Long>()

    input.forEach { (a, b) ->
      firstList.add(a)
      secondList.add(b)
    }

    firstList.sort()
    secondList.sort()

    firstList.zip(secondList)
      .sumOf { (first, second) -> abs(first - second) }
  }


  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::twoLists) { input ->
    val firstList = mutableListOf<Long>()
    val secondListOccurrences = mutableMapOf<Long, Long>()

    input.forEach { (first, second) ->
      firstList.add(first)
      secondListOccurrences.merge(second, 1) { previous, one -> previous + one }
    }

    firstList.sumOf { item -> secondListOccurrences.getOrDefault(item, 0) * item }
  }

  private fun twoLists(line: String) = line
    .split("   ")
    .let { (a, b) -> a.toLong() to b.toLong() }
}
