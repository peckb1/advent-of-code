package me.peckb.aoc._2023.calendar.day25

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import org.jgrapht.alg.StoerWagnerMinimumCut
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleWeightedGraph
import javax.inject.Inject


class Day25 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val graph = SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)

    input.forEach { line ->
      val (name, others) = line.split(": ")

      graph.addVertex(name)

      others.split(" ").forEach { other ->
        graph.addVertex(other)
        graph.addEdge(name, other)
      }
    }

    val oneSide = StoerWagnerMinimumCut(graph).minCut()

    (graph.vertexSet().size - oneSide.size) * oneSide.size
  }
}
