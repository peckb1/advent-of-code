package me.peckb.aoc._2016.calendar.day20

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max

class Day20 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(this::range) { input ->
    val ranges = input.toList().sortedBy { it.first }

    var min = 0L

    ranges.forEach { range ->
      val start = range.first
      val end = range.last

      if (min >= start) {
        min = max(min, end + 1)
      }
    }

    min
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(this::range) { input ->
    val ranges = input.toList().sortedBy { it.first }

    var min = 0L
    val allowedRanges = mutableListOf<LongRange>()

    ranges.forEach { range ->
      val start = range.first
      val end = range.last
      val next = end + 1

      min = if (min >= start) {
        max(min, next)
      } else {
        allowedRanges.add(min until start)
        next
      }
    }

    allowedRanges.sumOf { it.count() }
  }

  private fun range(line: String) = line.split("-").map { it.toLong() }.let {
    it.first()..it.last()
  }
}
