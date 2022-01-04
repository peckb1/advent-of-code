package me.peckb.aoc._2015.calendar.day22

import me.peckb.aoc.Dijkstra

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
class GameDijkstra : Dijkstra<Game, Int, GameWithCost> {
  override fun Game.withCost(cost: Int) = GameWithCost(this, cost)
  override fun Int.plus(cost: Int) = this + cost
  override fun maxCost() = Int.MAX_VALUE
  override fun minCost() = 0
}
