package me.peckb.aoc._2023.calendar.day17

import me.peckb.aoc._2023.calendar.day17.Day17.Direction.*
import me.peckb.aoc._2023.calendar.day17.Day17.LavaPoolDijkstra.NodeWithCost
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.Dijkstra
import me.peckb.aoc.pathing.DijkstraNodeWithCost

class Day17 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  class LavaPoolDijkstra(
    val map: List<List<Int>>,
    val neighborsGenerator: (List<List<Int>>, Node) -> List<Pair<Node, Int>>
  ) : Dijkstra<Node, Int, NodeWithCost> {
    override fun Int.plus(cost: Int): Int {
      return this + cost
    }

    override fun Node.withCost(cost: Int): NodeWithCost {
      return NodeWithCost(this, cost)
    }

    override fun minCost(): Int = 0
    override fun maxCost(): Int = Int.MAX_VALUE

    inner class NodeWithCost(val node: Node, val heatLoss: Int) : DijkstraNodeWithCost<Node, Int> {
      override fun neighbors(): List<DijkstraNodeWithCost<Node, Int>> {
        return neighborsGenerator(map, node).map { NodeWithCost(it.first, it.second) }
      }

      override fun node(): Node = node
      override fun cost(): Int = heatLoss

      override fun compareTo(other: DijkstraNodeWithCost<Node, Int>): Int = heatLoss.compareTo(other.cost())
    }
  }

  data class Node(val row: Int, val col: Int, val directionTraveling: Direction, val stepsInDirection: Int) {
    fun move(directionToMove: Direction, map: List<List<Int>>): Node? {
      val (dr, dc) = when (directionToMove) {
        NORTH -> -1 to 0
        SOUTH -> 1 to 0
        EAST -> 0 to 1
        WEST -> 0 to -1
      }

      val r = row + dr
      val c = col + dc

      return if (map.indices.contains(r) && map[r].indices.contains(c)) {
        val newSteps = if (directionToMove == directionTraveling) stepsInDirection + 1 else 1
        Node(r, c, directionToMove, newSteps)
      } else {
        null
      }
    }
  }

  enum class Direction {
    NORTH, SOUTH, EAST, WEST
  }

  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    runDijkstra(input) { map, node ->
      val directionsToTravel = when (node.directionTraveling) {
        NORTH -> mutableListOf(EAST, WEST).also { if (node.stepsInDirection < 3) it.add(node.directionTraveling) }
        SOUTH -> mutableListOf(EAST, WEST).also { if (node.stepsInDirection < 3) it.add(node.directionTraveling) }
        EAST -> mutableListOf(NORTH, SOUTH).also { if (node.stepsInDirection < 3) it.add(node.directionTraveling) }
        WEST -> mutableListOf(NORTH, SOUTH).also { if (node.stepsInDirection < 3) it.add(node.directionTraveling) }
      }

      directionsToTravel.mapNotNull { directionToMove ->
        node.move(directionToMove, map)?.let {
          it to map[it.row][it.col]
        }
      }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read {
    -1
  }

  private fun runDijkstra(
    input: Sequence<String>,
    neighborsGenerator: (List<List<Int>>, Node) -> List<Pair<Node, Int>>
  ): Int {
    val lavaPool = arrayListOf<ArrayList<Int>>()
    input.forEach { row -> lavaPool.add(row.map { it.digitToInt() }.toCollection(ArrayList())) }

    val start = Node(0, 0, EAST, 0)

    val dijkstra = LavaPoolDijkstra(lavaPool) { map, node ->
      neighborsGenerator(map, node)
    }

    val paths = dijkstra.solve(
      start,
      end = Node(lavaPool.lastIndex, lavaPool[0].lastIndex, EAST, 0)
    ) { n1, n2 ->
      when (val c = n1.col.compareTo(n2.col)) {
        0 -> n1.row.compareTo(n2.row)
        else -> c
      }
    }

    return paths.filter { it.key.row == lavaPool.lastIndex && it.key.col == lavaPool[0].lastIndex }
      .minOf { it.value }
  }
}
