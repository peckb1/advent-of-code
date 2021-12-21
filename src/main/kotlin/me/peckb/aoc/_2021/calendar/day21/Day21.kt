package me.peckb.aoc._2021.calendar.day21

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day21 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    const val PLAYER_1_INDEX = 0
    const val PLAYER_2_INDEX = 1
    const val WINNING_SCORE = 21

    val BOARD = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val MOVE_SETS = mapOf(
      3 to 1L,
      4 to 3L,
      5 to 6L,
      6 to 7L,
      7 to 6L,
      8 to 3L,
      9 to 1L
    )
  }

  fun deterministicScore(fileName: String) = generatorFactory.forFile(fileName).readAs(::player) { input ->
    val players = input.toList()
    val die = DeterministicD100()

    var playerCounter = 0
    while(players.none { it.points >= 1000 }) {
      val player = players[playerCounter % players.size]
      val distanceToTravel = (1..3).sumOf { die.roll() }
      val circularDistance = distanceToTravel % BOARD.size
      player.index = (player.index + circularDistance) % BOARD.size
      player.points+= BOARD[player.index]
      playerCounter++
    }

    players.filterNot { it.points >= 1000 }.sumOf { it.points } * die.rollCount
  }

  fun multiUniverseWinCount(fileName: String) = generatorFactory.forFile(fileName).readAs(::player) { input ->
    val (player1, player2) = input.toList()
    val firstGame = Game(player1.index, 0, player2.index, 0)
    val gamesMap = mutableMapOf(firstGame to 1L)

    val winCounts = mutableMapOf<Int, Long>().withDefault { 0 }

    while(gamesMap.isNotEmpty()) {
      val (game, universesIExistIn) = gamesMap.iterator().next()
      gamesMap.remove(game)

      MOVE_SETS.forEach { (player1RollSum, extraUniversesAfterPlayer1) ->
        val newPlayer1Index = (game.player1Index + player1RollSum) % BOARD.size
        val newPlayer1Points = game.player1Points + BOARD[newPlayer1Index]

        val newUniverseCountAfterOneMove = universesIExistIn * extraUniversesAfterPlayer1
        if (newPlayer1Points >= WINNING_SCORE) {
          winCounts.merge(PLAYER_1_INDEX, newUniverseCountAfterOneMove, Long::plus)
        } else {
          MOVE_SETS.forEach { (player2RollSum, extraUniversesAfterPlayer2) ->
            val newPlayer2Index = (game.player2Index + player2RollSum) % BOARD.size
            val newPlayer2Points = game.player2Points + BOARD[newPlayer2Index]

            val newUniverseCountAfterTwoMoves: Long = newUniverseCountAfterOneMove * extraUniversesAfterPlayer2
            if (newPlayer2Points >= WINNING_SCORE) {
              winCounts.merge(PLAYER_2_INDEX, newUniverseCountAfterTwoMoves, Long::plus)
            } else {
              // since we don't make a new game until both players have gone
              // we don't need to worry about storing which player goes next
              // every time we pull a game to play, it's player one's turn
              val newGame = Game(newPlayer1Index, newPlayer1Points, newPlayer2Index, newPlayer2Points)
              gamesMap.merge(newGame, newUniverseCountAfterTwoMoves, Long::plus)
            }
          }
        }
      }
    }

    winCounts.maxOf { it.value }
  }

  data class Game(val player1Index: Int, val player1Points: Int, val player2Index: Int, val player2Points: Int)

  data class Player(var index: Int, var points: Int = 0)

  class DeterministicD100 {
    private var result = 100
    var rollCount: Long = 0

    fun roll(): Int {
      rollCount++
      result++
      if (result > 100) {
        result = 1
      }
      return result
    }
  }

  private fun player(line: String) = Player(Character.getNumericValue(line[line.length - 1]) - 1)
}
