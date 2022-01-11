package me.peckb.aoc._2017.calendar.day11

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.abs
import kotlin.math.max

class Day11 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    var norths = 0.0
    var easts = 0.0
    var souths = 0.0
    var wests = 0.0

    input.split(",").forEach {
      when(it) {
        "n" -> norths += 1.0
        "ne" -> { norths += 0.5; easts += 0.5 }
        "se" -> { souths += 0.5; easts += 0.5 }
        "s" -> souths += 1.0
        "sw" -> { souths += 0.5; wests += 0.5 }
        "nw" -> { norths += 0.5; wests += 0.5 }
        else -> throw IllegalArgumentException("Unknown direction: $it")
      }
    }

    val upDowns = abs(norths - souths)
    val leftRights = abs(easts - wests)

    upDowns + leftRights
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    var norths = 0.0
    var easts = 0.0
    var souths = 0.0
    var wests = 0.0
    var maxDistance = 0.0

    input.split(",").forEach {
      when(it) {
        "n" -> norths += 1.0
        "ne" -> { norths += 0.5; easts += 0.5 }
        "se" -> { souths += 0.5; easts += 0.5 }
        "s" -> souths += 1.0
        "sw" -> { souths += 0.5; wests += 0.5 }
        "nw" -> { norths += 0.5; wests += 0.5 }
        else -> throw IllegalArgumentException("Unknown direction: $it")
      }
      val upDowns = abs(norths - souths)
      val leftRights = abs(easts - wests)
      maxDistance = max(maxDistance, upDowns + leftRights)
    }

    maxDistance
  }
}
