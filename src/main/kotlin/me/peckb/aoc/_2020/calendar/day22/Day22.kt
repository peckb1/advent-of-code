package me.peckb.aoc._2020.calendar.day22

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max

class Day22 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val players = mutableListOf<CombatPlayer>()

    val iterator = input.iterator()
    while (iterator.hasNext()) {
      val next = iterator.next()
      if (next.startsWith("Player ")) {
        // new player
        players.add(CombatPlayer(id = next.dropLast(1).takeLast(1).toInt()))
      } else if (next.isEmpty()) {
        // end of current player
      } else {
        players.last().addCard(next.toInt())
      }
    }

    val playerOne = players.first()
    val playerTwo = players.last()

    while (playerOne.hasCards() && playerTwo.hasCards()) {
      val playerOneCard = playerOne.turnCard()
      val playerTwoCard = playerTwo.turnCard()

      if (playerOneCard > playerTwoCard) {
        playerOne.addCards(playerOneCard, playerTwoCard)
      } else if (playerTwoCard > playerOneCard) {
        playerTwo.addCards(playerTwoCard, playerOneCard)
      } else { // cards are equal
        playerOne.addCard(playerOneCard)
        playerTwo.addCard(playerTwoCard)
      }
    }

    val p1Score = playerOne.generateScore()
    val p2Score = playerTwo.generateScore()

    max(p1Score, p2Score)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val players = mutableListOf<RecursiveCombatPlayer>()

    val iterator = input.iterator()
    while (iterator.hasNext()) {
      val next = iterator.next()
      if (next.startsWith("Player ")) {
        // new player
        players.add(RecursiveCombatPlayer(id = next.dropLast(1).takeLast(1).toInt()))
      } else if (next.isEmpty()) {
        // end of current player
      } else {
        players.last().addCard(next.toInt())
      }
    }

    val playerOne = players.first()
    val playerTwo = players.last()

    RecursiveCombat(playerOne, playerTwo).playGame()

    val p1Score = playerOne.generateScore()
    val p2Score = playerTwo.generateScore()

    max(p1Score, p2Score)
  }
}
