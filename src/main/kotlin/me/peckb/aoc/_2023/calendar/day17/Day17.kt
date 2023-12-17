package me.peckb.aoc._2023.calendar.day17

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max

class Day17 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    runDijkstra(input, minSteps = 1) { node ->
      mutableListOf<Direction>().apply {
        addAll(node.directionTraveling.turnDirections())
        if (node.stepsInDirection < 3) { add(node.directionTraveling) }
      }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    runDijkstra(input, minSteps = 4) { node ->
      mutableListOf<Direction>().apply {
        if (node.stepsInDirection >= 4) { addAll(node.directionTraveling.turnDirections()) }
        if (node.stepsInDirection < 10) { add(node.directionTraveling) }
      }
    }
  }

  private fun runDijkstra(input: Sequence<String>, minSteps: Int, neighbors: (Node) -> List<Direction>): Int {
    val lavaPool = mutableListOf<MutableList<Int>>().apply {
      input.forEach { row -> add(row.map { it.digitToInt() }.toMutableList()) }
    }

    val start = Node(0, 0)
    val end = Node(lavaPool.lastIndex, lavaPool[0].lastIndex)

    val dijkstra = LavaPoolDijkstra(lavaPool) { map, node ->
      val directionsToTravel = neighbors(node)

      directionsToTravel.mapNotNull { direction ->
        node.move(direction, map)?.let { it to map[it.row][it.col] }
      }
    }

    val paths = dijkstra.solve(start, end) { searchNode, goalNode ->
      when (val colCompare = searchNode.col.compareTo(goalNode.col)) {
        0 -> when (val rowCompare = searchNode.row.compareTo(goalNode.row)) {
          0 -> max(minSteps - searchNode.stepsInDirection, 0)
          else -> rowCompare
        }
        else -> colCompare
      }
    }

    return paths
      .filter { it.key.row == end.row && it.key.col == end.col }
      .filter { it.key.stepsInDirection >= minSteps }
      .minOf { it.value }
  }
}
