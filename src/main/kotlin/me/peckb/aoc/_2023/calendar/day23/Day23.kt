package me.peckb.aoc._2023.calendar.day23

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max

class Day23 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val grid = input.map { row -> row.toList() }.toList()

    val start = Location(0, grid[0].indexOf('.'))
    val goal = Location(grid.lastIndex, grid[grid.lastIndex].indexOf('.'))

    dfs(grid, start, goal)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val grid = input.map { row -> row.toList() }.toList()
    val adjacencyGraph = makeAdjacencies(grid)

    val start = Location(0, grid[0].indexOf('.'))
    val goal = Location(grid.lastIndex, grid[grid.lastIndex].indexOf('.'))

    dfsWithAdjacencies(adjacencyGraph, start, goal)
  }

  private fun dfs(
    grid: List<List<Char>>,
    current: Location,
    goal: Location,
    visited: MutableSet<Location> = mutableSetOf(),
    steps: Int = 0
  ): Int {
    if (current == goal) { return steps }

    visited.add(current)

    val theirSteps = getNeighbors(grid, current)
      .filter { it !in visited }
      .map { dfs(grid, it, goal, visited, steps + 1) }
      .maxOfOrNull { it }
      ?: -1

    visited.remove(current)
    
    return max(theirSteps, steps)
  }

  private fun makeAdjacencies(grid: List<List<Char>>): Map<Location, Map<Location, Int>> {
    // create a list of every location, and their direct neighbors
    val adjacencies = grid.indices.flatMap { rowIndex ->
      grid[rowIndex].indices.filter { grid[rowIndex][it] != '#' }.map { colIndex ->
        val neighbors = DIRECTIONS_WITH_SLOPE
          .map { (deltas, _) -> rowIndex + deltas.first to colIndex + deltas.second }
          .filter { (row, col) -> row in grid.indices && col in grid[0].indices && grid[row][col] != '#' }
          .associateTo(mutableMapOf()) { (row, col) -> Location(row, col) to 1 }

        Location(rowIndex, colIndex) to neighbors
      }
    }.toMap(mutableMapOf())

    // collapse all the nodes that have "two neighbors" (not a branching location)
    adjacencies.keys.toList().forEach { location ->
      adjacencies[location]?.takeIf { it.size == 2 }?.let { neighbors ->
        val first = neighbors.keys.first()
        val last = neighbors.keys.last()
        val totalSteps = neighbors[first]!! + neighbors[last]!!

        adjacencies.getOrPut(first) { mutableMapOf() }.merge(last, totalSteps, ::maxOf)
        adjacencies.getOrPut(last) { mutableMapOf() }.merge(first, totalSteps, ::maxOf)

        listOf(first, last).forEach { adjacencies[it]?.remove(location) }

        adjacencies.remove(location)
      }
    }

    return adjacencies
  }

  private fun dfsWithAdjacencies(
    adjacencyGraph: Map<Location, Map<Location, Int>>,
    current: Location,
    goal: Location,
    visited: MutableMap<Location, Int> = mutableMapOf()
  ): Int {
    if (current == goal) { return visited.values.sum() }

    var myBest = 0

    (adjacencyGraph[current] ?: emptyMap()).forEach { (neighbor, steps) ->
      if (neighbor !in visited) {
        visited[neighbor] = steps
        val neighborsBest = dfsWithAdjacencies(adjacencyGraph, neighbor, goal, visited)
        myBest = max(myBest, neighborsBest)

        visited.remove(neighbor)
      }
    }

    return myBest
  }

  private fun getNeighbors(grid: List<List<Char>>, current: Location): List<Location> {
    val cur = grid[current.row][current.col]
    return DIRECTIONS_WITH_SLOPE
      .map { (deltas, slope) -> (deltas.first + current.row to deltas.second + current.col) to slope }
      .filter { (coords, slope) ->
        val (row, col) = coords
        row in grid.indices && col in grid[0].indices && grid[row][col] != '#' && (cur == '.' || cur == slope)
      }.map { (coors, _) -> Location(coors.first, coors.second) }
  }

  data class Location(val row: Int, val col: Int)

  companion object {
    val NORTH = (-1 to  0) to '^'
    val SOUTH = ( 1 to  0) to 'v'
    val EAST  = ( 0 to  1) to '>'
    val WEST  = ( 0 to -1) to '<'

    val DIRECTIONS_WITH_SLOPE = listOf(NORTH, SOUTH, EAST, WEST)
  }
}
