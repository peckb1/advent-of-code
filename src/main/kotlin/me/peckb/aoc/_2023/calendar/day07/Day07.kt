package me.peckb.aoc._2023.calendar.day07

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day07 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::hand) { input ->
    input.sortedWith { handOne, handTwo ->
      val c = handOne.strength.compareTo(handTwo.strength)
      if (c != 0) {
        c
      } else {
        var index = 0
        var winner = 0
        while(index < handOne.cards.size && winner == 0) {
          winner = handOne.cards[index].compareTo(handTwo.cards[index])
          index++
        }
        winner
      }
    }.toList()
      .reversed()
      .mapIndexed { index, hand ->
        ((index + 1) * hand.bid).toLong()
      }.sum()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::jokerHand) { input ->
    input.sortedWith { handOne, handTwo ->
      val c = handOne.strength.compareTo(handTwo.strength)
      if (c != 0) {
        c
      } else {
        var index = 0
        var winner = 0
        while(index < handOne.cards.size && winner == 0) {
          winner = handOne.cards[index].compareTo(handTwo.cards[index])
          index++
        }
        winner
      }
    }.toList()
      .reversed()
      .mapIndexed { index, hand -> ((index + 1) * hand.bid).toLong() }
      .sum()
  }

  private fun hand(line: String): Hand {
    val (cardData, bidData) = line.split(" ")
    val cards = cardData.map { Card.fromSymbol(it) }
    val bid = bidData.toInt()

    return Hand(cards, bid)
  }

  private fun jokerHand(line: String): JokerHand {
    val (cardData, bidData) = line.split(" ")
    val cards = cardData.map { JokerCard.fromSymbol(it) }
    val bid = bidData.toInt()

    return JokerHand(cards, bid)
  }
}
