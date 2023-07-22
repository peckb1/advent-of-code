package me.peckb.aoc._2020.calendar.day13

import me.peckb.aoc.Math
import me.peckb.aoc.Math.CoPrimeValues
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day13 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val data = input.toList()
    val departureTime = data.first().toInt()
    val busTimes = data.last().split(",").mapNotNull { it.toIntOrNull() }

    val remainingRemainders = busTimes.map {
      it to (it - (departureTime % it))
    }

    val nextBus = remainingRemainders.minByOrNull { it.second }!!

    nextBus.first * nextBus.second
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val data = input.toList()
    val busTimes = data.last().split(",")
      .map { it.toLongOrNull() }
      .mapIndexedNotNull { index, value -> value?.let { it to index } }
      .map { (busId, minuteAway) ->
        busId to (busId - minuteAway)
      }

    val coPrimeObjects = busTimes.map {
      CoPrimeValues(it.first, it.second)
    }

    Math.chineseRemainderTheorem(coPrimeObjects)
  }
}
