package me.peckb.aoc._2021.calendar.day12

import me.peckb.aoc._2021.calendar.day12.Day12.Node
import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

typealias Tunnels = Map<Node, List<Node>>
typealias Route = List<Node>

class Day12 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::day12) { input ->
    val tunnels = createMap(input)

    val source = Node("start")
    val branchingNodes = tunnels.makeNewPaths(listOf(source)) { route, neighbor ->
      neighbor.id.first().isUpperCase() || !route.contains(neighbor)
    }.filter { it.last().id == "end" }

    branchingNodes.size
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day12) { input ->
    val tunnels = createMap(input)

    val source = Node("start")
    val branchingNodes = tunnels.makeNewPaths(listOf(source)) { route, neighbor ->
      val thisLowerCaseAllowed =
        route
          .filterNot { it.id == "start" || it.id == "end" || it.id.first().isUpperCase() }
          .groupBy { it.id }
          .values
          .count { it.size > 1 } == 0

      neighbor.id.first().isUpperCase() || thisLowerCaseAllowed || !route.contains(neighbor)
    }.filter { it.last().id == "end" }

    branchingNodes.size
  }

  private fun createMap(input: Sequence<Path>): Tunnels {
    val paths: MutableMap<Node, MutableList<Node>> = mutableMapOf<Node, MutableList<Node>>().withDefault { mutableListOf() }

    fun addData(source: Node, destination: Node) {
      if (source.id != "end") {
        paths[source] = paths.getValue(source).apply {
          if (destination.id != "start") {
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
    if(lastStep.id == "end") {
      return paths
    }

    val neighbors = this[lastStep]!!
    neighbors.forEach { neighbor ->
      if (allowedToExplore(currentPath, neighbor)) {
        val newPath = mutableListOf<Node>().apply {
          addAll(currentPath)
          add(neighbor)
        }
        paths.add(newPath)
        val someMorePaths = makeNewPaths(newPath, allowedToExplore).filter { it.last().id == "end" }
        paths.addAll(someMorePaths)
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
