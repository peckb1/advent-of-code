package me.peckb.aoc._2020.calendar.day22

import java.util.LinkedList
import java.util.Queue

data class CombatPlayer(
  val id: Int,
  val cardValues: Queue<Int> = LinkedList()
) {
  fun addCard(toInt: Int) {
    cardValues.add(toInt)
  }

  fun hasCards(): Boolean = cardValues.peek() != null

  fun turnCard(): Int = cardValues.remove()

  fun addCards(higherCard: Int, lowerCard: Int) {
    addCard(higherCard)
    addCard(lowerCard)
  }

  fun generateScore(): Long {
    return cardValues.reversed().foldIndexed(0L) { index, total, card ->
      val multiplier = index + 1
      total + (multiplier * card)
    }
  }
}
