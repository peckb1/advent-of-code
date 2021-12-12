package me.peckb.aoc._2021.calendar.day12

import me.peckb.aoc._2021.calendar.day12.Day12.Node
import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

typealias Tunnels = Map<Node, List<Node>>
typealias Route = MutableMap<Node, Int>

class Day12 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    const val START_NODE = "start"
    const val END_NODE = "end"
  }

  fun findPathsSingleSmallCave(fileName: String) = generatorFactory.forFile(fileName).readAs(::tunnel) { input ->
    val tunnels = createMap(input)
    val source = Node(START_NODE)

    tunnels.countPaths(source, mutableMapOf(source to 1))
  }

  fun findPathsOneDoubleSmallCave(fileName: String) = generatorFactory.forFile(fileName).readAs(::tunnel) { input ->
    val tunnels = createMap(input)
    val source = Node(START_NODE)

    tunnels.countPaths(source, mutableMapOf(source to 1)) { route ->
      !route.entries.any { (node, count) -> node.isLowerCase && count > 1 }
    }
  }

  private fun createMap(input: Sequence<Tunnel>): Tunnels {
    val paths = mutableMapOf<Node, MutableList<Node>>()

    fun addData(source: Node, destination: Node) {
      if (!source.isEnd) {
        paths.compute(source) { _, list ->
          list?.also { if (!destination.isStart) it.add(destination) }
            ?: mutableListOf(destination)
        }
      }
    }

    input.forEach {
      addData(it.node1, it.node2)
      addData(it.node2, it.node1)
    }

    return paths
  }

  private fun Tunnels.countPaths(newNeighbor: Node, visitCounts: MutableMap<Node, Int>, secondaryBypass: ((Route) -> Boolean)? = null): Int {
    var paths = 0

    val neighbors = this[newNeighbor] ?: emptyList()

    neighbors.forEach { neighbor ->
      if (neighbor.isUpperCase || !visitCounts.contains(neighbor) || secondaryBypass?.invoke(visitCounts) == true) {
        if (neighbor.isEnd) {
          paths += 1
        } else {
          val count = visitCounts.compute(neighbor) { _, c-> (c ?: 0) + 1 }!!
          paths += countPaths(neighbor, visitCounts, secondaryBypass)
          if (count == 1) {
            visitCounts.remove(neighbor)
          } else {
            visitCounts[neighbor] = count - 1
          }
        }
      }
    }

    return paths
  }

  private fun tunnel(line: String) = line
    .split("-")
    .map(::Node)
    .let { (n1, n2) -> Tunnel(n1, n2) }

  data class Tunnel(val node1: Node, val node2: Node)

  data class Node(private val id: String) {
    val isUpperCase = id.first().isUpperCase()
    val isLowerCase = id.first().isLowerCase()
    val isStart = id == START_NODE
    val isEnd = id == END_NODE
  }
}
