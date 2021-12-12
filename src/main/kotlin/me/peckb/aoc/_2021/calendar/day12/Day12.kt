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

    val branchingNodes = tunnels.makeNewPaths(listOf(source)) { route, neighbor ->
      neighbor.id.first().isUpperCase() || !route.contains(neighbor)
    }

    branchingNodes.size
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day12) { input ->
    val tunnels = createMap(input)
    val source = Node(START_NODE)

    val branchingNodes = tunnels.makeNewPaths(listOf(source)) { route, neighbor ->
      val thisLowerCaseAllowed =
        route
          .filterNot { it.id == START_NODE || it.id == END_NODE || it.id.first().isUpperCase() }
          .groupBy { it.id }
          .values
          .count { it.size > 1 } == 0

      neighbor.id.first().isUpperCase() || thisLowerCaseAllowed || !route.contains(neighbor)
    }

    branchingNodes.size
  }

  private fun createMap(input: Sequence<Path>): Tunnels {
    val paths: MutableMap<Node, MutableList<Node>> = mutableMapOf<Node, MutableList<Node>>().withDefault { mutableListOf() }

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

  private fun Tunnels.makeNewPaths(currentPath: List<Node>, allowedToExplore: (Route, Node) -> Boolean): LinkedHashSet<MutableList<Node>> {
    val paths = linkedSetOf<MutableList<Node>>()

    val lastStep = currentPath.last()
    if(lastStep.id == END_NODE) {
      return paths
    }

    val neighbors = this[lastStep]!!
    neighbors.forEach { neighbor ->
      if (allowedToExplore(currentPath, neighbor)) {
        val newPath = mutableListOf<Node>().apply {
          addAll(currentPath)
          add(neighbor)
        }
        if (neighbor.id == END_NODE) paths.add(newPath)

        paths.addAll(
          makeNewPaths(newPath, allowedToExplore)
        )
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
