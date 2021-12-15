package me.peckb.aoc._2021.calendar.day15

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import java.util.PriorityQueue
import javax.inject.Inject
import kotlin.Int.Companion.MAX_VALUE

class Day15 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun smallPath(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val graph = input.map { row ->
      row.map(Character::getNumericValue).map { risk ->
        Vertex(risk.toLong())
      }
    }.toList()

    setupEdges(graph)
    dijkstra(graph)
  }

  fun largePath(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val growthSize = 5

    val data = input.toList()
    val maxY = data.size * growthSize
    val maxX = data[data.size - 1].length * growthSize
    val defaultVertex = Vertex(-1)
    val graph = MutableList(maxY) { MutableList(maxX) { defaultVertex } }

    (0 until growthSize).forEach { yLoop ->
      (0 until growthSize).forEach { xLoop ->
        data.forEachIndexed { y, row ->
          row.forEachIndexed { x, riskChar ->
            val realY = (yLoop * data.size) + y
            val realX = (xLoop * row.length) + x
            var r = Character.getNumericValue(riskChar) + yLoop + xLoop
            while (r > 9) { r -= 9 }

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
        graph[y].getOrNull(x + 1)?.let { left ->
          left.addNeighbor(vertex)
          vertex.addNeighbor(left)
        }
        graph.getOrNull(y + 1)?.get(x)?.let { down ->
          down.addNeighbor(vertex)
          vertex.addNeighbor(down)
        }
      }
    }
  }

  private fun dijkstra(graph: List<List<Vertex>>): Long? {
    val source = graph[0][0]
    val maxY = graph.size - 1
    val maxX = graph[maxY].size - 1
    val destination = graph[maxY][maxX]

    val distances = mutableMapOf(source to 0L).withDefault { MAX_VALUE.toLong() }
    val previous = mutableMapOf<Vertex, Vertex>()
    val queue = PriorityQueue(compareBy<Vertex> { distances.getValue(it) }).apply {
      add(source)
    }

    while (queue.isNotEmpty()) {
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

  class Vertex(val risk: Long, val neighbors: MutableList<Vertex> = mutableListOf()) {
    fun addNeighbor(vertex: Vertex) {
      neighbors.add(vertex)
    }
  }
}
