package me.peckb.aoc._2024.calendar.day11

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day11 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { line ->
    val stonesMap = createStonesMap(line)

    repeat(25) { blink(stonesMap) }

    stonesMap.entries.sumOf { it.value }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { line ->
    val stonesMap = createStonesMap(line)

    repeat(75) { blink(stonesMap) }

    stonesMap.entries.sumOf { it.value }
  }

  private fun createStonesMap(line: String) = mutableMapOf<Long, Long>().apply {
    line.split(" ").forEach { merge(it.toLong(), 1, Long::plus) }
  }

  private fun blink(stones: MutableMap<Long, Long>) {
    val entries = stones.entries.toList()

    stones.clear()

    entries.forEach { (stone, count) ->
      if (stone == 0L) {
        stones.merge(1, count, Long::plus)
      } else if ("$stone".length % 2 == 0) {
        val stoneStr = "$stone"
        val half = stoneStr.length / 2

        stones.merge(stoneStr.take(half).toLong(), count, Long::plus)
        stones.merge(stoneStr.takeLast(half).toLong(), count, Long::plus)
      } else {
        stones.merge(stone * 2024, count, Long::plus)
      }
    }
  }
}
