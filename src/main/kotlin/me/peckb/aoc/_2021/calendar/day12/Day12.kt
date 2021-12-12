package me.peckb.aoc._2021.calendar.day12

import me.peckb.aoc._2021.calendar.day12.Day12.Node
import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

typealias Tunnels = Map<Node, List<Node>>
typealias Route = List<Node>

class Day12 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    const val START_NODE = "start"
    const val END_NODE = "end"
  }

  fun findPathsSingleSmallCave(fileName: String) = generatorFactory.forFile(fileName).readAs(::tunnel) { input ->
    val tunnels = createMap(input)
    val source = Node(START_NODE)

    tunnels.makeNewPaths(listOf(source)).size
  }

  fun findPathsOneDoubleSmallCave(fileName: String) = generatorFactory.forFile(fileName).readAs(::tunnel) { input ->
    val tunnels = createMap(input)
    val source = Node(START_NODE)

    val branchingNodes = tunnels.makeNewPaths(listOf(source)) bypass@{ route ->
      val counts = mutableMapOf<Node, Int>()

      route.forEach { node ->
        if (node.isLowerCase) {
          if (counts[node] == null) {
            counts[node] = 1
          } else {
            return@bypass false
          }
        }
      }

      return@bypass true
    }

    branchingNodes.size
  }

  private fun createMap(input: Sequence<Tunnel>): Tunnels {
    val paths = mutableMapOf<Node, MutableList<Node>>().withDefault { mutableListOf() }

    fun addData(source: Node, destination: Node) {
      if (!source.isEnd) {
        paths[source] = paths.getValue(source).apply {
          if (!destination.isStart) {
            add(destination)
          }
        }
      }
    }

    input.forEach {
      addData(it.node1, it.node2)
      addData(it.node2, it.node1)
    }

    return paths
  }

  private fun Tunnels.makeNewPaths(currentPath: List<Node>, secondaryBypass: ((Route) -> Boolean)? = null): List<List<Node>> {
    val paths = mutableListOf<List<Node>>()

    val lastStep = currentPath.last()
    val neighbors = this[lastStep] ?: emptyList()

    neighbors.forEach { neighbor ->
      if (neighbor.isUpperCase || !currentPath.contains(neighbor) || secondaryBypass?.invoke(currentPath) == true) {
        val newPath = currentPath.plus(neighbor)

        if (neighbor.isEnd) {
          paths.add(newPath)
        } else {
          paths.addAll(makeNewPaths(newPath, secondaryBypass))
        }
      }
    }

    return paths
  }

  private fun tunnel(line: String): Tunnel {
    val (n1, n2) = line.split("-").map(::Node)
    return Tunnel(n1, n2)
  }

  data class Tunnel(val node1: Node, val node2: Node)

  data class Node(private val id: String) {
    val isUpperCase = id.first().isUpperCase()
    val isLowerCase = id.first().isLowerCase()
    val isStart = id == START_NODE
    val isEnd = id == END_NODE
  }
}
