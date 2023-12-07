package me.peckb.aoc._2023.calendar.day07

data class Hand(val cards: List<Card>, val bid: Int) {
  val strength: HandStrength by lazy {
    val cardGroups = cards.groupBy { it.rank }
    if (cardGroups.values.any { it.size == 5 }) {
      HandStrength.FIVE_OF_A_KIND
    } else if (cardGroups.values.any { it.size == 4 }) {
      HandStrength.FOUR_OF_A_KIND
    } else if (cardGroups.values.any { it.size == 3 } && cardGroups.values.any { it.size == 2 }) {
      HandStrength.FULL_HOUSE
    } else if (cardGroups.values.any { it.size == 3 }) {
      HandStrength.THREE_OF_A_KIND
    } else if (cardGroups.values.count { it.size == 2 } == 2) {
      HandStrength.TWO_PAIR
    } else if (cardGroups.values.any { it.size == 2 }) {
      HandStrength.ONE_PAIR
    } else {
      HandStrength.HIGH_CARD
    }
  }

  override fun toString(): String {
    return "cards=$cards, bid=$bid, str=$strength"
  }
}
