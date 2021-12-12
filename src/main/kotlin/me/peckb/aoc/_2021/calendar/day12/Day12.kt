package me.peckb.aoc._2021.calendar.day12

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day12 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {

  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::day12) { input ->
    val paths: MutableMap<Node, MutableList<Node>> = mutableMapOf<Node, MutableList<Node>>().withDefault { mutableListOf() }

    input.forEach {
      paths[it.node1] = paths.getValue(it.node1).apply { add(it.node2) }
      if(it.node2.id != "end") {
        paths[it.node2] = paths.getValue(it.node2).apply {
          if (it.node1.id != "start") {
            add(it.node1)
          }
        }
      }
    }

    val source = Node("start")
    val currentPath = mutableListOf(source)
    val branchingNodes = makeNewPathsOne(paths, currentPath).filter { it.last().id == "end" }


    branchingNodes.size
  }

  private fun makeNewPathsOne(map: MutableMap<Node, MutableList<Node>>, currentPath: MutableList<Node>): LinkedHashSet<MutableList<Node>> {
    val paths = linkedSetOf<MutableList<Node>>()

    val lastStep = currentPath.last()
    if(lastStep.id == "end") {
      return paths
    }

    val neighbors = map[lastStep]!!
    neighbors.forEach { neighbor ->
      if (neighbor.id.first().isUpperCase() || !currentPath.contains(neighbor)) {
        val newPath = mutableListOf<Node>().apply {
          addAll(currentPath)
          add(neighbor)
        }
        paths.add(newPath)
        val someMorePaths = makeNewPathsOne(map, newPath)
        paths.addAll(someMorePaths)
      }
    }

    return paths
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day12) { input ->
    val paths: MutableMap<Node, MutableList<Node>> = mutableMapOf<Node, MutableList<Node>>().withDefault { mutableListOf() }

    input.forEach {
      if (it.node1.id != "end") {
        paths[it.node1] = paths.getValue(it.node1).apply {
          if (it.node2.id != "start") {
            add(it.node2)
          }
        }
      }
      if(it.node2.id != "end") {
        paths[it.node2] = paths.getValue(it.node2).apply {
          if (it.node1.id != "start") {
            add(it.node1)
          }
        }
      }
    }

    val source = Node("start")
    val currentPath = mutableListOf(source)
    val branchingNodes = makeNewPathsTwo(paths, currentPath).filter { it.last().id == "end" }


    branchingNodes.size
  }

  private fun makeNewPathsTwo(map: MutableMap<Node, MutableList<Node>>, currentPath: MutableList<Node>): LinkedHashSet<MutableList<Node>> {
    val paths = linkedSetOf<MutableList<Node>>()

    val lastStep = currentPath.last()
    if(lastStep.id == "end") {
      return paths
    }

    val neighbors = map[lastStep]!!
    neighbors.forEach { neighbor ->
      val thisLowerCaseAllowed =
          currentPath
            .filterNot { it.id == "start" || it.id == "end" || it.id.first().isUpperCase() }
            .groupBy { it.id }
            .values
            .count { it.size > 1 } == 0

      if (neighbor.id.first().isUpperCase() || thisLowerCaseAllowed || !currentPath.contains(neighbor)) {
        val newPath = mutableListOf<Node>().apply {
          addAll(currentPath)
          add(neighbor)
        }
        paths.add(newPath)
        val someMorePaths = makeNewPathsTwo(map, newPath).filter { it.last().id == "end" }
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
