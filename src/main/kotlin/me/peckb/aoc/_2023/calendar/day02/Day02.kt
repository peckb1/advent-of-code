package me.peckb.aoc._2023.calendar.day02

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max

class Day02 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::game) { games ->
    val maxRed = 12
    val maxGreen = 13
    val maxBlue = 14

    games.filter { game ->
      game.rounds.none { round ->
        round.red > maxRed || round.blue > maxBlue || round.green > maxGreen
      }
    }.sumOf { it.id }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::game) { games ->
    games.sumOf { game ->
      var minRequiredRed = 0
      var minRequiredBlue = 0
      var minRequiredGreen = 0

      game.rounds.forEach { round ->
        minRequiredRed = max(minRequiredRed, round.red)
        minRequiredBlue = max(minRequiredBlue, round.blue)
        minRequiredGreen = max(minRequiredGreen, round.green)
      }

      minRequiredRed * minRequiredGreen * minRequiredBlue
    }
  }

  private fun game(line: String): Game {
    val (gameIdString, roundData) = line.split(": ")
    val id = gameIdString.split(" ").last().toInt()

    val roundStrings = roundData.split("; ")

    val rounds = roundStrings.map { roundInformation ->
      var red = 0
      var blue = 0
      var green = 0

      roundInformation.split(", ")
        .forEach { colourInformation ->
          val (count, colour) = colourInformation.split(" ")
          when (colour) {
            "red" -> red = count.toInt()
            "blue" -> blue = count.toInt()
            "green" -> green = count.toInt()
          }
        }

      Round(red, green, blue)
    }

    return Game(id, rounds)
  }

  data class Game(val id: Int, val rounds: List<Round>)

  data class Round(val red: Int, val green: Int, val blue: Int)
}
