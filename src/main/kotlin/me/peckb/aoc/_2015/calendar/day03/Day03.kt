package me.peckb.aoc._2015.calendar.day03

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

typealias House = Pair<Long, Long>

class Day03 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    var santaY = 0L
    var santaX = 0L

    val visitedHouses: MutableSet<House> = mutableSetOf(santaY to santaX)

    input.forEach { directionChar ->
      when (directionChar) {
        '^' -> santaY++
        '>' -> santaX++
        'v' -> santaY--
        '<' -> santaX--
      }
      visitedHouses.add(santaY to santaX)
    }

    visitedHouses.size
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    var santaY = 0L
    var santaX = 0L

    var robotSantaY = 0L
    var robotSantaX = 0L

    val visitedHouses: MutableSet<House> = mutableSetOf(santaY to santaX)

    input.chunked(2).forEach { directions ->
      directions.firstOrNull()?.also {
        when (it) {
          '^' -> santaY++
          '>' -> santaX++
          'v' -> santaY--
          '<' -> santaX--
        }
        visitedHouses.add(santaY to santaX)
      }
      directions.lastOrNull()?.also {
        when (it) {
          '^' -> robotSantaY++
          '>' -> robotSantaX++
          'v' -> robotSantaY--
          '<' -> robotSantaX--
        }
        visitedHouses.add(robotSantaY to robotSantaX)
      }
    }

    visitedHouses.size
  }
}
