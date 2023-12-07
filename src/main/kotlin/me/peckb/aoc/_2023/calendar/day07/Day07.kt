package me.peckb.aoc._2023.calendar.day07

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day07 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(
    { hand(it, Card::fromSymbol) },
    { getScore(it) }
  )

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(
    { hand(it, Card::fromSymbolWithJokers) },
    { getScore(it) }
  )

  private fun getScore(hands: Sequence<Hand>): Long {
    return hands.sortedWith { handOne, handTwo ->
      val c = handOne.strength.rank.compareTo(handTwo.strength.rank)
      if (c != 0) {
        c
      } else {
        var index = 0
        var winner = 0
        while (index < handOne.cards.size && winner == 0) {
          winner = handOne.cards[index].rank.compareTo(handTwo.cards[index].rank)
          index++
        }
        winner
      }
    }.toList()
      .mapIndexed { index, hand -> ((index + 1) * hand.bid).toLong() }
      .sum()
  }

  private fun hand(line: String, converter: (Char) -> Card): Hand {
    val (cardData, bidData) = line.split(" ")
    val cards = cardData.map { converter(it) }
    val bid = bidData.toInt()

    return Hand(cards, bid)
  }
}
