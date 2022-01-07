package me.peckb.aoc._2021.calendar.day23

import me.peckb.aoc.Dijkstra
import me.peckb.aoc.generators.InputGenerator
import javax.inject.Inject
import kotlin.Int.Companion.MAX_VALUE

class Day23 @Inject constructor(private val generatorFactory: InputGenerator.InputGeneratorFactory) {
  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val startLayout = Layout.fromInput(input.toList())
    val dijkstra = AmphipodDijkstra()
    val costs = dijkstra.solve(startLayout)

    costs.entries.first { (layout, _) -> layout.hallway.all { it == '.' } }.value
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val rawInput = input.toList()
    val startLayout = Layout.fromInput(rawInput.take(3) + "  #D#C#B#A#  " + "  #D#B#A#C#  " + rawInput.takeLast(2))
    val dijkstra = AmphipodDijkstra()
    val costs = dijkstra.solve(startLayout)

    costs.entries.first { (layout, _) -> layout.hallway.all { it == '.' } }.value
  }
}

class AmphipodDijkstra : Dijkstra<Layout, Int, LayoutWithCost> {
  override fun minCost() = 0
  override fun maxCost() = MAX_VALUE
  override fun Int.plus(cost: Int) = this + cost
  override fun Layout.withCost(cost: Int) = LayoutWithCost(this, cost)
}
