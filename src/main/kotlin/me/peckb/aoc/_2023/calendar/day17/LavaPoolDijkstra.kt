package me.peckb.aoc._2023.calendar.day17

import me.peckb.aoc.pathing.Dijkstra
import me.peckb.aoc.pathing.DijkstraNodeWithCost

class LavaPoolDijkstra(
  val lava: List<List<Int>>,
  val neighborsGenerator: (List<List<Int>>, Node) -> List<Pair<Node, Int>>
) : Dijkstra<Node, Int, LavaPoolDijkstra.NodeWithCost> {
  override fun Int.plus(cost: Int) = this + cost

  override fun Node.withCost(cost: Int) = NodeWithCost(this, cost)

  override fun minCost(): Int = 0

  override fun maxCost(): Int = Int.MAX_VALUE

  inner class NodeWithCost(val node: Node, private val heatLoss: Int) : DijkstraNodeWithCost<Node, Int> {
    override fun neighbors() = neighborsGenerator(lava, node)
      .map { NodeWithCost(it.first, it.second) }

    override fun node(): Node = node

    override fun cost(): Int = heatLoss

    override fun compareTo(other: DijkstraNodeWithCost<Node, Int>): Int = heatLoss.compareTo(other.cost())
  }
}
