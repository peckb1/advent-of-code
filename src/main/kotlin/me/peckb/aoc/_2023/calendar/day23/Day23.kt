package me.peckb.aoc._2023.calendar.day23

import arrow.core.fold
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
    location: Location,
    goal: Location,
    visited: MutableSet<Location> = mutableSetOf(),
    steps: Int = 0
  ): Int {
    if (location == goal) {
      return steps
    }

    visited.add(location)

    val distanceToEnd = getNeighbors(grid, location)
      .filter { it !in visited }
      .map { dfs(grid, it, goal, visited, steps + 1) }
      .maxOfOrNull { it }
      ?: steps

    visited.remove(location)

    return distanceToEnd
  }

  private fun makeAdjacencies(grid: List<List<Char>>): Map<Location, Map<Location, Int>> {
    // create a list of every location, and their direct neighbors
    val adjacencies = grid.indices.flatMap { rowIndex ->
      grid[rowIndex].indices.filter { grid[rowIndex][it] != '#' }.map { colIndex ->
        val neighbors = DIRECTIONS_WITH_SLOPE
          .map { (dr, dc, _) -> rowIndex + dr to colIndex + dc }
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
    location: Location,
    goal: Location,
    visited: MutableMap<Location, Int> = mutableMapOf()
  ): Int {
    if (location == goal) { return visited.values.sum() }

    return (adjacencyGraph[location] ?: emptyMap()).fold(0) { best, adjacency ->
      val (neighbor, steps) = adjacency

      if (neighbor in visited) { best } else {
        visited.whileVisiting(neighbor, steps) {
          max(best, dfsWithAdjacencies(adjacencyGraph, neighbor, goal, visited))
        }
      }
    }
  }

  private inline fun <K, V, R>  MutableMap<K, V>.whileVisiting(key: K, value: V, action: () -> R): R {
    put(key, value)
    return action().also { remove(key) }
  }

  private fun getNeighbors(grid: List<List<Char>>, location: Location): List<Location> {
    val spot = grid[location.row][location.col]
    return DIRECTIONS_WITH_SLOPE
      .map { (dr, dc, slope) -> location.move(dr, dc) to slope }
      .filter { (l, _) -> l.inGrid(grid) }
      .filter { (l, slope) -> grid[l.row][l.col] != '#' && (spot == '.' || spot == slope) }
      .map { (l, _) -> l }
  }

  data class Location(val row: Int, val col: Int) {
    fun move(dr: Int, dc: Int) = Location(row + dr, col + dc)

    fun inGrid(grid: List<List<Char>>) = row in grid.indices && col in grid[0].indices
  }

  data class MovementWithSlope(val rowDelta: Int, val colDelta: Int, val slope: Char)

  companion object {
    val NORTH = MovementWithSlope(-1,  0, '^')
    val SOUTH = MovementWithSlope( 1,  0, 'v')
    val EAST  = MovementWithSlope( 0,  1, '>')
    val WEST  = MovementWithSlope( 0, -1, '<')

    val DIRECTIONS_WITH_SLOPE = listOf(NORTH, SOUTH, EAST, WEST)
  }
}
