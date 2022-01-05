package me.peckb.aoc._2016.calendar.day06

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day06 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { noise ->
    val counts = Array(8) { mutableMapOf<Char, Int>() }
    noise.forEach { n ->
      n.forEachIndexed { i, c ->
        counts[i].merge(c, 1, Int::plus)
      }
    }
    counts.map { it.maxByOrNull { it.value }?.key }.joinToString("")
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { noise ->
    val counts = Array(8) { mutableMapOf<Char, Int>() }
    noise.forEach { n ->
      n.forEachIndexed { i, c ->
        counts[i].merge(c, 1, Int::plus)
      }
    }
    counts.map { it.minByOrNull { it.value }?.key }.joinToString("")
  }
}
