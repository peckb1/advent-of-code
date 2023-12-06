package me.peckb.aoc._2023.calendar.day06

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day06 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (times, distances) = input.map {
      it.split(":").last().split(" ").mapNotNull(String::toIntOrNull)
    }.toList()

    times.zip(distances).fold(1) { acc, race ->
      val (time, distance) = race

      acc * (0..time).count { speed -> speed * (time - speed) > distance }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (time, distance) = input.toList().map {
      it.split(":").last().filter(Char::isDigit).toLong()
    }

    (0..time).count { speed -> speed * (time - speed) > distance }
  }
}
