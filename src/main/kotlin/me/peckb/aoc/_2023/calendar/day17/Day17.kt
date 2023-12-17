package me.peckb.aoc._2023.calendar.day17

import me.peckb.aoc._2023.calendar.day17.Direction.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day17 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    runDijkstra(input) { node ->
      mutableListOf<Direction>().apply {
        addAll(node.directionTraveling.turnDirections())
        if (node.stepsInDirection < 3) { add(node.directionTraveling) }
      }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    runDijkstra(input) { node ->
      mutableListOf<Direction>().apply {
        if (node.stepsInDirection >= 4) { addAll(node.directionTraveling.turnDirections()) }
        if (node.stepsInDirection < 10) { add(node.directionTraveling) }
      }
    }
  }

  private fun runDijkstra(input: Sequence<String>, neighbors: (Node) -> List<Direction>): Int {
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

    val paths = dijkstra.solve(start, end) { n1, n2 ->
      when (val colCompare = n1.col.compareTo(n2.col)) {
        0 -> n1.row.compareTo(n2.row)
        else -> colCompare
      }
    }

    return paths.filter { it.key.row == end.row && it.key.col == end.col }.minOf { it.value }
  }
}
