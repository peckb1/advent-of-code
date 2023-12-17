package me.peckb.aoc._2023.calendar.day17

import me.peckb.aoc._2023.calendar.day17.LavaPoolDijkstra.HeatNode
import me.peckb.aoc.pathing.Dijkstra
import me.peckb.aoc.pathing.DijkstraNodeWithCost

class LavaPoolDijkstra(
  val lava: List<List<Int>>,
  val neighborsGenerator: (List<List<Int>>, Node) -> List<Pair<Node, Int>>
) : Dijkstra<Node, Int, HeatNode> {
  override fun Int.plus(cost: Int) = this + cost

  override fun Node.withCost(cost: Int) = HeatNode(this, cost)

  override fun minCost(): Int = 0

  override fun maxCost(): Int = Int.MAX_VALUE

  inner class HeatNode(val node: Node, private val heatLoss: Int) : DijkstraNodeWithCost<Node, Int> {
    override fun neighbors() = neighborsGenerator(lava, node).map { HeatNode(it.first, it.second) }

    override fun node(): Node = node

    override fun cost(): Int = heatLoss

    override fun compareTo(other: DijkstraNodeWithCost<Node, Int>): Int = heatLoss.compareTo(other.cost())
  }
}
