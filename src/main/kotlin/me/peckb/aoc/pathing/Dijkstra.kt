package me.peckb.aoc.pathing

import java.util.PriorityQueue

/**
 * NOTE: whatever class you use for `Node` should be a `data class`
 */
interface Dijkstra<Node, Cost : Comparable<Cost>, NodeWithCost: DijkstraNodeWithCost<Node, Cost>> {
  fun solve(
    start: Node,
    end: Node? = null,
    comparator: Comparator<Node>? = null,
  ): MutableMap<Node, Cost> {
    val toVisit = PriorityQueue<NodeWithCost>().apply { add(start.withCost(minCost())) }
    val visited = linkedSetOf<Node>()
    val currentCosts = mutableMapOf<Node, Cost>()
      .withDefault { maxCost() }
      .apply { this[start] = minCost() }

    while (toVisit.isNotEmpty()) {
//      if (visited.size > 250_000) {
//        visited.remove(visited.elementAt(0))
//      }
//      println("${toVisit.size} ${visited.size}")
      val current: NodeWithCost = toVisit.poll().also { visited.add(it.node()) }

      val foundEnd: Boolean? = end?.let { node: Node ->
        comparator?.let { it.compare(current.node(), end) == 0 } ?: (current.node() == node)
      }

      if (foundEnd == true) return currentCosts

      current.neighbors().forEach { neighbor ->
        if (!visited.contains(neighbor.node())) {
          val newCost = current.cost() + neighbor.cost()
          if (newCost < currentCosts.getValue(neighbor.node())) {
            currentCosts[neighbor.node()] = newCost
            toVisit.add(neighbor.node().withCost(newCost))
          }
        }
      }
    }

    return currentCosts
  }

  infix operator fun Cost.plus(cost: Cost): Cost
  fun Node.withCost(cost: Cost): NodeWithCost
  fun minCost(): Cost
  fun maxCost(): Cost
}

interface DijkstraNodeWithCost<Node, Cost> : Comparable<DijkstraNodeWithCost<Node, Cost>> {
  fun neighbors(): List<DijkstraNodeWithCost<Node, Cost>>
  fun node(): Node
  fun cost(): Cost
}
