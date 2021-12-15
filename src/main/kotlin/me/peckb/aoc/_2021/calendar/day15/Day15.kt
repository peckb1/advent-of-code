package me.peckb.aoc._2021.calendar.day15

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import java.util.PriorityQueue
import javax.inject.Inject
import kotlin.Int.Companion.MAX_VALUE

class Day15 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::day15) { input ->
    val graph = input.mapIndexed { y, row ->
      row.map(Character::getNumericValue).mapIndexed { x, risk ->
        Vertex(risk.toLong())
      }
    }.toList()

    setupEdges(graph)

    dijkstra(graph)
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day15) { input ->
    val data = input.toList()
    val maxX = data.size * 5
    val maxY = data[data.size - 1].length * 5

    val defaultVertex = Vertex(-1)

    val graph = MutableList(maxY) { MutableList(maxX) { defaultVertex } }

    (0 until 5).forEach { yLoop ->
      (0 until 5).forEach { xLoop ->
        data.forEachIndexed { y, row ->
          row.forEachIndexed { x, riskChar ->
            val realY = yLoop * data[y].length + y
            val realX = xLoop * row.length + x
            var r = Character.getNumericValue(riskChar) + yLoop + xLoop
            while(r > 9) { r -= 9 }

            graph[realY][realX] = Vertex(r.toLong())
          }
        }
      }
    }

    setupEdges(graph)
    dijkstra(graph)
  }

  private fun setupEdges(graph: List<List<Vertex>>) {
    graph.forEachIndexed { y, vertexRow ->
      vertexRow.forEachIndexed { x, vertex ->
        try {
          val left = graph[y][x + 1]
          left.addNeighbor(vertex)
          vertex.addNeighbor(left)
        } catch(e: IndexOutOfBoundsException) { }
        try {
          val down = graph[y+1][x]
          down.addNeighbor(vertex)
          vertex.addNeighbor(down)
        } catch(e: IndexOutOfBoundsException) { }
      }
    }
  }

  private fun dijkstra(graph: List<List<Vertex>>): Long? {
    val source = graph[0][0]
    val maxY = graph.size - 1
    val maxX = graph[maxY].size - 1
    val destination = graph[maxY][maxX]

    val nodes = graph.flatten()
    val distances = mutableMapOf(source to 0L).withDefault { MAX_VALUE.toLong() }
    val previous = mutableMapOf<Vertex, Vertex>()
    val queue = PriorityQueue(nodes.size, compareBy<Vertex> { distances.getValue(it) })

    nodes.forEach { v -> queue.add(v) }

    while(queue.isNotEmpty()) {
      val u = queue.remove()
      u.neighbors.forEach { v ->
        val alt = distances.getValue(u) + v.risk
        if (alt < distances.getValue(v)) {
          distances[v] = alt
          previous[v] = u
          queue.add(v)
        }
      }
    }

    return distances[destination]
  }

  private fun day15(line: String) = line

  class Vertex(val risk: Long, val neighbors: MutableList<Vertex> = mutableListOf()) {
    fun addNeighbor(vertex: Vertex) {
      neighbors.add(vertex)
    }
  }
}
