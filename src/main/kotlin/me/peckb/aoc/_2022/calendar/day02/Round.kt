package me.peckb.aoc._2022.calendar.day02

data class Round(val them: Choice, val me: Choice) {
  fun score(): Int = me.score + when (me) {
    them.moveToWinAgainst() -> WIN_SCORE
    them.moveToTieAgainst() -> TIE_SCORE
    else -> LOSS_SCORE
  }

  companion object {
    private const val WIN_SCORE = 6
    private const val TIE_SCORE = 3
    private const val LOSS_SCORE = 0
  }
}
