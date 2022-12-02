package me.peckb.aoc._2022.calendar.day02

import me.peckb.aoc._2022.calendar.day02.Choice.PAPER
import me.peckb.aoc._2022.calendar.day02.Choice.ROCK
import me.peckb.aoc._2022.calendar.day02.Choice.SCISSORS
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalStateException

class Day02 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::codedRound) { input ->
    input.sumOf { (them, me) ->
      val theirChoice = loadTheirChoice(them)
      val myChoice = loadMyPartOneChoice(me)

      Round(theirChoice, myChoice).score()
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::codedRound) { input ->
    input.sumOf { (them, me) ->
      val theirChoice = loadTheirChoice(them)
      val myChoice = loadMyPartTwoChoice(theirChoice, me)

      Round(theirChoice, myChoice).score()
    }
  }

  private fun codedRound(line: String) = line.split(" ")

  private fun loadTheirChoice(them: String): Choice = when (them) {
    THEIR_ROCK -> ROCK
    THEIR_PAPER -> PAPER
    THEIR_SCISSORS -> SCISSORS
    else -> throw IllegalStateException("Unknown Choice for them [$them]")
  }

  private fun loadMyPartOneChoice(me: String): Choice = when (me) {
    MY_ROCK -> ROCK
    MY_PAPER -> PAPER
    MY_SCISSORS -> SCISSORS
    else -> throw IllegalStateException("Unknown Choice for me [$me]")
  }

  private fun loadMyPartTwoChoice(theirChoice: Choice, me: String): Choice = when (me) {
    LOSE -> theirChoice.moveToLoseAgainst()
    DRAW -> theirChoice.moveToTieAgainst()
    WIN -> theirChoice.moveToWinAgainst()
    else -> throw IllegalStateException("Unknown Choice for me [$me]")
  }

  companion object {
    private const val THEIR_ROCK = "A"
    private const val THEIR_PAPER = "B"
    private const val THEIR_SCISSORS = "C"

    private const val MY_ROCK = "X"
    private const val MY_PAPER = "Y"
    private const val MY_SCISSORS = "Z"

    private const val LOSE = "X"
    private const val DRAW = "Y"
    private const val WIN = "Z"
  }
}
