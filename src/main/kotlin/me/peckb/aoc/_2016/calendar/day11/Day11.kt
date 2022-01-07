package me.peckb.aoc._2016.calendar.day11

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.collections.MutableMap.MutableEntry

class Day11 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val initialLayout = loadLayout(input)
    val paths = RTGDijkstra().solve(initialLayout)

    val uniqueMachines = initialLayout.floors.flatMap { it.microchips }.size
    val numFloors = initialLayout.floors.size

    val solution = findCheapestPathToGoal(paths, numFloors, uniqueMachines)
    solution.value
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val initialLayout = loadLayout(input)
    val paths = RTGDijkstra().solve(initialLayout)

    val uniqueMachines = initialLayout.floors.flatMap { it.microchips }.size
    val numFloors = initialLayout.floors.size

    val solution = findCheapestPathToGoal(paths, numFloors, uniqueMachines)

    // adding any number of matching RTGs to the bottom floor doesn't change how long it takes
    // to solve the initial problem, and the increased cost is just
    // initialSolution + ((numFloors - 1) * numFloors) * (newPairs)
    val newPairs = 2
    solution.value + (((numFloors - 1) * numFloors) * newPairs)
  }

  private fun findCheapestPathToGoal(paths: MutableMap<FloorPlan, Int>, numFloors: Int, uniqueMachines: Int): MutableEntry<FloorPlan, Int> {
    return paths.entries.first { (fp, _) ->
      val topLevelIndex = numFloors - 1
      fp.elevatorIndex == topLevelIndex
        && fp.floors[topLevelIndex].generators.size == uniqueMachines
        && fp.floors[topLevelIndex].microchips.size == uniqueMachines
    }
  }

  private fun loadLayout(input: Sequence<String>): FloorPlan {
    val floors = input.map { line ->
      val generators = sortedSetOf<String>()
      val microchips = sortedSetOf<String>()

      val equipmentOnFloor = line.substringAfter("floor contains ")
        .split("a ")
        .filterNot { it.isEmpty() }
        .filterNot { it.contains("nothing") }

      val (generatorData, microchipData) = equipmentOnFloor.partition { it.contains("generator") }
      generatorData.forEach { generators.add(it.substring(0, 3)) }
      microchipData.forEach { microchips.add(it.substring(0, 3)) }

      Floor(generators, microchips)
    }
    return FloorPlan(floors.toList(), 0)
  }

  class RTGDijkstra : GenericIntDijkstra<FloorPlan>()
}
