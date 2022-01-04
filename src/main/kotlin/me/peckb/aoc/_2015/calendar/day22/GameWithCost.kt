package me.peckb.aoc._2015.calendar.day22

import me.peckb.aoc.DijkstraNodeWithCost

class GameWithCost(private val game: Game, val cost: Int) :
  DijkstraNodeWithCost<Game, Int> {
  override fun node() = game
  override fun cost() = cost
  override fun neighbors() = game.neighbors().map { GameWithCost(it.first, it.second) }
  override fun compareTo(other: DijkstraNodeWithCost<Game, Int>) = cost.compareTo(other.cost())
}
