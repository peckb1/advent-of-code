package me.peckb.aoc._2020.calendar.day22

import me.peckb.aoc._2020.calendar.day22.RecursiveCombatWinner.*
import org.apache.commons.lang3.ObjectUtils.clone

class RecursiveCombat(
  private val playerOne: RecursiveCombatPlayer,
  private val playerTwo: RecursiveCombatPlayer
) {
  private val previouslySeenPlayerOneConfigurations = mutableSetOf<String>()
  private val previouslySeenPlayerTwoConfigurations = mutableSetOf<String>()

  fun playGame(): RecursiveCombatWinner {
    while (playerOne.hasCards() && playerTwo.hasCards()) {
      val (winner, p1Card, p2Card) = deal()
      if (p1Card == null || p2Card == null) return PLAYER_ONE

      when (winner) {
        PLAYER_ONE -> playerOne.addCards(p1Card, p2Card)
        PLAYER_TWO -> playerTwo.addCards(p2Card, p1Card)
        NO_WINNER -> throw IllegalStateException("There must always be a winner!")
      }
    }

    if (playerOne.hasCards()) return PLAYER_ONE
    return PLAYER_TWO
  }

  private fun deal(): Triple<RecursiveCombatWinner, Int?, Int?> {
    val playerOneConfiguration = playerOne.deckConfiguration()
    val playerTwoConfiguration = playerTwo.deckConfiguration()

    if (
      previouslySeenPlayerOneConfigurations.contains(playerOneConfiguration) &&
      previouslySeenPlayerTwoConfigurations.contains(playerTwoConfiguration)
    ) {
      return Triple(PLAYER_ONE, null, null)
    } else {
      previouslySeenPlayerOneConfigurations.add(playerOneConfiguration)
      previouslySeenPlayerTwoConfigurations.add(playerTwoConfiguration)
    }

    val playerOneCard = playerOne.turnCard()
    val playerTwoCard = playerTwo.turnCard()

    val winner = if (playerOne.haveAtLeast(playerOneCard) && playerTwo.haveAtLeast(playerTwoCard)) {
      RecursiveCombat(
        playerOne.copy(cardValues = clone(playerOne.cardValues)).dropUntil(playerOneCard),
        playerTwo.copy(cardValues = clone(playerTwo.cardValues)).dropUntil(playerTwoCard)
      ).playGame()
    } else {
      if (playerOneCard > playerTwoCard) {
        PLAYER_ONE
      } else if (playerTwoCard > playerOneCard) {
        PLAYER_TWO
      } else { // cards are equal
        NO_WINNER
      }
    }

    return Triple(winner, playerOneCard, playerTwoCard)
  }
}


enum class RecursiveCombatWinner {
  PLAYER_ONE, PLAYER_TWO, NO_WINNER
}