package me.peckb.aoc._2020.calendar.day06

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day06 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val groups = mutableListOf<Set<Char>>()
    val currentGroup = mutableListOf<Char>()

    input.forEach {
      if (it.isEmpty()) {
        groups.add(currentGroup.toSet())
        currentGroup.clear()
      } else {
        currentGroup.addAll(it.toList())
      }
    }

    if (currentGroup.isNotEmpty()) {
      groups.add(currentGroup.toSet())
    }

    groups.sumOf { it.size }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val groups = mutableListOf<Set<Char>>()
    var currentGroup = ALL_OPTIONS

    input.forEach {
      currentGroup = if (it.isEmpty()) {
        ALL_OPTIONS.also { groups.add(currentGroup.toSet()) }
      } else {
        it.toSet().intersect(currentGroup)
      }
    }

    if (currentGroup.isNotEmpty()) {
      groups.add(currentGroup.toSet())
    }

    groups.sumOf { it.size }
  }

  companion object {
    private val ALL_OPTIONS = ('a' .. 'z').toSet()
  }
}
