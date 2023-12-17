package me.peckb.aoc._2023.calendar.day17

import arrow.core.compareTo
import me.peckb.aoc._2023.calendar.day17.Day17.Direction.*
import me.peckb.aoc._2023.calendar.day17.Day17.LavaPoolDijkstra.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.Dijkstra
import me.peckb.aoc.pathing.DijkstraNodeWithCost
import kotlin.math.abs

class Day17 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  class LavaPoolDijkstra(val map: List<List<Int>>) : Dijkstra<Block, Path, LavaPoolDijkstra.BlockWithPath> {
    val rowBounds = map.indices
    val colBounds = 0 until map[0].size

    override fun Path.plus(cost: Path): Path = cost

    override fun Block.withCost(cost: Path): BlockWithPath = BlockWithPath(this, cost)
    override fun minCost(): Path = Path(map, emptyList())
    override fun maxCost(): Path = Path(map, emptyList(), Int.MAX_VALUE)

    inner class BlockWithPath(val block: Block, val path: Path) : DijkstraNodeWithCost<Block, Path> {
      override fun compareTo(other: DijkstraNodeWithCost<Block, Path>): Int {
        return cost().cost.compareTo(other.cost().cost)
      }

      override fun neighbors(): List<DijkstraNodeWithCost<Block, Path>> {
        val lastThree = block.lastDirections.takeLast(3)
        val lastStep = lastThree.lastOrNull()

        val threeInARow = (lastThree.size == 3 && lastThree.toSet().size == 1)

        val options = when (lastStep) {
          NORTH, SOUTH -> mutableListOf(EAST, WEST).also   { if (!threeInARow) it.add(lastStep) }
          EAST,  WEST  -> mutableListOf(NORTH, SOUTH).also { if (!threeInARow) it.add(lastStep) }
          null         -> mutableListOf(NORTH, SOUTH, EAST, WEST)
        }

        val directionToTravel = options.mapNotNull { directionToTravel ->
          when (directionToTravel) {
            NORTH -> Triple(-1, 0, directionToTravel)
            SOUTH -> Triple(1,0, directionToTravel)
            EAST  -> Triple(0,1, directionToTravel)
            WEST  -> Triple(0,-1, directionToTravel)
          }
        }

        return directionToTravel.mapNotNull { (dr, dc, d) ->
          val r = block.row + dr
          val c = block.col + dc
          if (rowBounds.contains(r) && colBounds.contains(c)) {
            val newBlock = Block(r, c, block.lastDirections.takeLast(2).plus(d), map[r][c])
            val newPath = Path(map, path.steps.plus(newBlock))
            BlockWithPath(newBlock, newPath)
          } else {
            null
          }
        }
      }

      override fun node(): Block = block
      override fun cost(): Path = path
    }
  }

  data class Block(val row: Int, val col: Int, val lastDirections: List<Direction>, val cost: Int) : Comparable<Block> {
    override fun compareTo(other: Block): Int {
      return when (val rowCompare = row.compareTo(other.row)) {
        0 -> when (val colCompare = col.compareTo(other.col)) {
          0 -> {
            val myLast = lastDirections.lastOrNull()
            val theirLast = other.lastDirections.lastOrNull()
            lastDirections.filter { it == myLast }.compareTo(other.lastDirections.filter { it == theirLast })
          }
          else -> colCompare
        }
        else -> rowCompare
      }
    }
  }

  enum class Direction { NORTH, SOUTH, EAST, WEST }

  data class Path(val map: List<List<Int>>, val steps: List<Block>, val cost: Int = steps.sumOf { it.cost }) : Comparable<Path> {
    override fun compareTo(other: Path): Int {
      return when(val costCompare =  cost.compareTo(other.cost)) {
        0 -> {
          val myDistance = steps.lastOrNull()?.let { (row, col, _, _) ->
            abs((map.size - 1) - row) + abs((map[0].size - 1) - col)
          }
          val theirDistance = other.steps.lastOrNull()?.let { (row, col, _, _) ->
            abs((map.size - 1) - row) + abs((map[0].size - 1) - col)
          }

          return when {
            myDistance == null -> 1
            theirDistance == null -> -1
            else -> myDistance.compareTo(theirDistance)
          }
        }
        else -> costCompare
      }
    }
  }

  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val lavaPool = arrayListOf<ArrayList<Int>>()
    input.forEach { row -> lavaPool.add(row.map { it.digitToInt() }.toCollection(ArrayList())) }

    val start = Block(0, 0, emptyList(), 0)

    val paths = LavaPoolDijkstra(lavaPool)
      .solve(
        start = start,
        end = Block(lavaPool.size - 1, lavaPool[0].size - 1, emptyList(), 0)
      ) { b1, b2 ->
        when (val rowCompare = b1.row.compareTo(b2.row)) {
          0 -> b1.col.compareTo(b2.col)
          else -> rowCompare
        }
      }

    paths.filter { it.key.row == lavaPool.size - 1 && it.key.col == lavaPool[0].size - 1 }.map { it.value.cost }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read {
    -1
  }
}
