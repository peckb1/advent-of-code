package me.peckb.aoc._2021.calendar.day01

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day01 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun largerMeasurements(filename: String) = generatorFactory.forFile(filename).readAs(::int) {
    it.windowed(2)
      .map { data -> WindowPair(data.first(), data.last()) }
      .count { windowPair -> windowPair.deepDepth > windowPair.shallowDepth }
  }

  fun largerGroupedMeasurements(filename: String) = generatorFactory.forFile(filename).readAs(::int) {
    it.windowed(3)
      .windowed(2)
      .map { data -> WindowPair(data.first().sum(), data.last().sum()) }
      .count { windowPair -> windowPair.deepDepth > windowPair.shallowDepth }
  }

  private fun int(line: String): Int = line.toInt()

  private data class WindowPair(val shallowDepth: Int, val deepDepth: Int)
}
