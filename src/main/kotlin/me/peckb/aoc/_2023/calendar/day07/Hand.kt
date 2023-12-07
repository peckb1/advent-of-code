package me.peckb.aoc._2023.calendar.day07

import me.peckb.aoc._2023.calendar.day07.Card.JOKER
import me.peckb.aoc._2023.calendar.day07.HandStrength.*

data class Hand(val cards: List<Card>, val bid: Int) {
  private val jokers = cards.count { it == JOKER }

  val strength: HandStrength by lazy {
    val cardGroups = cards.filterNot { it == JOKER }
      .groupBy { it }

    val fives = cardGroups.values.count { it.size == 5 }
    val fours = cardGroups.values.count { it.size == 4 }
    val threes = cardGroups.values.count { it.size == 3 }
    val twos = cardGroups.values.count { it.size == 2 }
    val ones = cardGroups.values.count { it.size == 1 }

    if (
      fives == 1 ||
      (fours == 1 && jokers == 1) ||
      (threes == 1 && jokers == 2) ||
      (twos == 1 && jokers == 3) ||
      (ones == 1 && jokers == 4) ||
      jokers == 5
    ) {
      FIVE_OF_A_KIND
    } else if (
      fours == 1 ||
      (threes == 1 && jokers == 1) ||
      (twos == 1 && jokers == 2) ||
      (ones >= 1 && jokers == 3)
    ) {
      FOUR_OF_A_KIND
    } else if (
      (threes == 1 && twos == 1) ||
      (twos == 2 && jokers == 1)
    ) {
      FULL_HOUSE
    } else if (
      threes == 1 ||
      (twos >= 1 && jokers == 1) ||
      (ones >= 1 && jokers == 2)
    ) {
      THREE_OF_A_KIND
    } else if (
      twos == 2 ||
      (ones > 1 && jokers >= 2)
    ) {
      TWO_PAIR
    } else if (
      twos == 1 ||
      ones > 1 && jokers >= 1
    ) {
      ONE_PAIR
    } else {
      HIGH_CARD
    }
  }

  override fun toString(): String {
    return "cards=$cards, bid=$bid, str=$strength"
  }
}
