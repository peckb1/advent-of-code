package me.peckb.aoc._2017.calendar.day06

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day06 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val first = input.split(WHITESPACE_REGEX).map { it.toInt() }
    val (configurations, _) = generateDataUntilDuplicate(first)
    configurations.size
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val first = input.split(WHITESPACE_REGEX).map { it.toInt() }
    val (configurations, duplicate) = generateDataUntilDuplicate(first)
    configurations.size - configurations.indexOf(duplicate)
  }

  private fun generateDataUntilDuplicate(first: List<Int>): Pair<MutableSet<List<Int>>, List<Int>> {
    val configurations = mutableSetOf<List<Int>>()
    var current = first

    val numBuckets = current.size
    while(!configurations.contains(current)) {
      configurations.add(current)

      val max = current.withIndex().maxByOrNull { it.value }!!
      val everyoneGets = max.value / numBuckets
      val next = current.map { it }.toMutableList()
      next[max.index] = 0
      next.indices.forEach { next[it] += everyoneGets }

      var toDistribute = max.value % numBuckets
      var distributionIndex = (max.index + 1) % numBuckets
      while(toDistribute > 0) {
        next[distributionIndex] += 1
        distributionIndex = (distributionIndex + 1) % numBuckets
        toDistribute--
      }

      current = next
    }

    return configurations to current
  }

  companion object {
    private val WHITESPACE_REGEX = "\\s+".toRegex()
  }
}
