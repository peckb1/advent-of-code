package me.peckb.aoc._2021.calendar.day23

import java.util.PriorityQueue

interface Dijkstra<Node, Cost : Comparable<Cost>, NodeWithCost: DijkstraNodeWithCost<Node, Cost>> {
  fun solve(start: Node): MutableMap<Node, Cost> {
    val toVisit = PriorityQueue<NodeWithCost>().apply { add(start.withCost(minCost())) }
    val visited = mutableSetOf<NodeWithCost>()
    val currentCosts = mutableMapOf<Node, Cost>().withDefault { maxCost() }

    while (toVisit.isNotEmpty()) {
      val current = toVisit.poll().also { visited.add(it) }
      current.neighbors().forEach { neighbor ->
        if (!visited.contains(neighbor)) {
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

  operator fun Cost.plus(cost: Cost): Cost
  fun Node.withCost(cost: Cost): NodeWithCost
  fun minCost(): Cost
  fun maxCost(): Cost
}

interface DijkstraNodeWithCost<Node, Cost> : Comparable<DijkstraNodeWithCost<Node, Cost>> {
  fun neighbors(): List<DijkstraNodeWithCost<Node, Cost>>
  fun node(): Node
  fun cost(): Cost
}
