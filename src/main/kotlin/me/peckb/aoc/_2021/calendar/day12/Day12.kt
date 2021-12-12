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

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::day12) { input ->
    val tunnels = createMap(input)
    val source = Node(START_NODE)

    tunnels.makeNewPaths(listOf(source)).size
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day12) { input ->
    val tunnels = createMap(input)
    val source = Node(START_NODE)

    val branchingNodes = tunnels.makeNewPaths(listOf(source)) { route->
      route
        .filterNot { it.id.first().isUpperCase() }
        .groupBy { it.id }
        .values
        .count { it.size > 1 } == 0
    }

    branchingNodes.size
  }

  private fun createMap(input: Sequence<Path>): Tunnels {
    val paths = mutableMapOf<Node, MutableList<Node>>().withDefault { mutableListOf() }

    fun addData(source: Node, destination: Node) {
      if (source.id != END_NODE) {
        paths[source] = paths.getValue(source).apply {
          if (destination.id != START_NODE) {
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
    val neighbors = this[lastStep]

    neighbors?.forEach { neighbor ->
      if (neighbor.id.first().isUpperCase() || !currentPath.contains(neighbor) || secondaryBypass?.invoke(currentPath) == true) {
        val newPath = currentPath.plus(neighbor)

        if (neighbor.id == END_NODE) {
          paths.add(newPath)
        } else {
          paths.addAll(makeNewPaths(newPath, secondaryBypass))
        }
      }
    }

    return paths
  }

  private fun day12(line: String) : Path {
    val (n1, n2) = line.split("-").map(::Node)
    return Path(n1, n2)
  }

  data class Path(val node1: Node, val node2: Node)

  data class Node(val id: String)
}
