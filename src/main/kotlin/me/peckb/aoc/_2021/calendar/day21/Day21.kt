package me.peckb.aoc._2021.calendar.day21

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day21 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    val BOARD = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::player) { input ->
    val players = input.toList()
    val die = DeterministicD100()

    var playerCounter = 0
    while(players.none { it.points >= 1000 }) {
      val player = players[playerCounter % players.size]
      val distanceToTravel = (1..3).sumOf { die.roll() }
      val circularDistance = distanceToTravel % BOARD.size
      player.zeroIndexedSpace += circularDistance
      player.zeroIndexedSpace %= BOARD.size
      player.points+= BOARD[player.zeroIndexedSpace]



      playerCounter++
    }

    players.filterNot { it.points >= 1000 }.sumOf { it.points } * die.rollCount
  }


  data class Game(val player1: Pair<Int, Int>, val player2: Pair<Int, Int>, val universesIExistIn: Long, val nextPlayer: Int)

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::player) { input ->
    val d = input.toList()
    val p1 = d.first().let { it.zeroIndexedSpace to it.points }
    val p2 = d.last().let { it.zeroIndexedSpace to it.points }

    val firstGame = Game(p1, p2, 1, 0)

    val moveSets = mapOf(
      3 to 1,
      4 to 3,
      5 to 6,
      6 to 7,
      7 to 6,
      8 to 3,
      9 to 1
    )



    var games = mutableListOf(firstGame)

    val winCounts = mutableMapOf<Int, Long>().apply {
      this[0] = 0
      this[1] = 0
    }

    while(games.isNotEmpty()) {
      println("Games: ${games.size}")
      val newGames = mutableListOf<Game>()

      games.forEachIndexed { i, game ->
        moveSets.forEach { (dieSum, newUniverseCount) ->
          val (playerAIndex, playerAPoints) = game.player1
          val (playerBIndex, playerBPoints) = game.player2
          val newIndex = (playerAIndex + dieSum) % 10
          val newPoints = playerAPoints + BOARD[newIndex]

          if (newPoints >= 21) {
            winCounts[game.nextPlayer] = winCounts[game.nextPlayer]!! + (game.universesIExistIn * newUniverseCount)
          } else {
            newGames.add(
              Game(
                player1 = playerBIndex to playerBPoints,
                player2 = newIndex to newPoints,
                universesIExistIn = game.universesIExistIn * newUniverseCount,
                nextPlayer = (game.nextPlayer + 1) % 2
              )
            )
          }
        }
      }

      games = newGames
    }

    winCounts.maxOf { it.value }
  }

  data class Player(val id: Int, var zeroIndexedSpace: Int, var points: Int = 0) {
    fun move(distanceToTravel: Int): Player {
      return copy().also {
        val circularDistance = distanceToTravel % BOARD.size
        it.zeroIndexedSpace += circularDistance
        it.zeroIndexedSpace %= BOARD.size
        it.points+= BOARD[it.zeroIndexedSpace]
      }
    }
  }

  interface Dice {
    fun roll(): Int
    fun rollCount(): Long
  }

  class DiracDice {
    fun roll(): List<Int> = listOf(1, 2, 3)
  }

  class DeterministicD100 : Dice {
    var result = 100
    var rollCount: Long = 0

    override fun roll(): Int {
      rollCount++
      result++
      if (result > 100) {
        result = 1
      }
      return result
    }

    override fun rollCount(): Long {
      return rollCount()
    }
  }

  private fun player(line: String) = Player(
    Character.getNumericValue(line[7]),
    Character.getNumericValue(line[line.length - 1]) - 1
  )
}
