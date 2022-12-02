package me.peckb.aoc._2022.calendar.day02

enum class Choice(val score: Int) {
  ROCK(1), PAPER(2), SCISSORS(3);

  fun moveToWinAgainst(): Choice = when (this) {
    ROCK -> PAPER
    PAPER -> SCISSORS
    SCISSORS -> ROCK
  }

  fun moveToLoseAgainst(): Choice = when (this) {
    ROCK -> SCISSORS
    PAPER -> ROCK
    SCISSORS -> PAPER
  }

  fun moveToTieAgainst() = this
}
