package me.peckb.aoc._2023.calendar.day17

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
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val lavaPool = arrayListOf<ArrayList<Int>>()
    input.forEach { row ->
      lavaPool.add(row.map { it.digitToInt() }.toCollection(ArrayList()))
    }

    var allowedEndNodes = 150

    val start = Block(0, 0, emptyList())
    val paths = LavaPoolDijkstra(lavaPool)
      .solve(
        start = start,
        end = Block(lavaPool.size - 1, lavaPool[0].size - 1, emptyList()),
        comparator = object : Comparator<Block> {
          override fun compare(o1: Block, o2: Block): Int {
            val actualComparison = when (o1.row.compareTo(o2.row)) {
              0  -> o1.col.compareTo(o2.col)
              1  -> 1
              -1 -> -1
              else -> throw IllegalStateException()
            }

            return if (actualComparison == 0 && allowedEndNodes-- < 0) {
              0
            } else {
              -1
            }
          }
        }
      )

    val endNodes = paths.filter {
      it.key.row == lavaPool.size - 1 && it.key.col == lavaPool[it.key.row].size - 1
    }.entries.sortedBy { it.value.heatCosts.sum() }

    endNodes.minOf { it.value.cost }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read {
    -1
  }

  class LavaPoolDijkstra(val map: List<List<Int>>) : Dijkstra<Block, Path, BlockWithPath> {

    inner class BlockWithPath(private val block: Block, private val path: Path) : DijkstraNodeWithCost<Block, Path> {
      override fun compareTo(other: DijkstraNodeWithCost<Block, Path>): Int {
        return cost().cost.compareTo(other.cost().cost)
      }

      override fun neighbors(): List<DijkstraNodeWithCost<Block, Path>> {
        val lastThree = path.steps.takeLast(3)
        val lastStep = lastThree.lastOrNull()

        val threeInARow = (lastThree.size == 3 && lastThree.toSet().size == 1)

        val options = when (lastStep) {
          NORTH, SOUTH -> mutableListOf(EAST, WEST).also   { if (!threeInARow) it.add(lastStep) }
          EAST,  WEST  -> mutableListOf(NORTH, SOUTH).also { if (!threeInARow) it.add(lastStep) }
          null         -> mutableListOf(NORTH, SOUTH, EAST, WEST)
        }

        return options.mapNotNull { directionToTravel ->
          val (row, col) = block
          when (directionToTravel) {
            NORTH -> {
              if (row > 0) {
                val newPath = Path(
                  lastBlock = Block(row - 1, col, lastThree),
                  heatCosts = path.heatCosts.plus(map[row - 1][col]),
                  steps = path.steps.plus(directionToTravel),
                  cost = path.cost + map[row - 1][col]
                )
                BlockWithPath(Block(row - 1, col, lastThree), newPath)
              } else {
                null
              }
            }
            SOUTH -> {
              if (row < map.size - 1) {
                val newPath = Path(
                  lastBlock = Block(row + 1, col, lastThree),
                  heatCosts = path.heatCosts.plus(map[row + 1][col]),
                  steps = path.steps.plus(directionToTravel),
                  cost = path.cost + map[row + 1][col]
                )
                BlockWithPath(Block(row + 1, col, lastThree), newPath)
              } else {
                null
              }
            }
            EAST -> {
              if (col < map[row].size - 1) {
                val newPath = Path(
                  lastBlock = Block(row, col + 1, lastThree),
                  heatCosts = path.heatCosts.plus(map[row][col + 1]),
                  steps = path.steps.plus(directionToTravel),
                  cost = path.cost + map[row][col + 1]
                )
                BlockWithPath(Block(row, col + 1, lastThree), newPath)
              } else {
                null
              }
            }
            WEST -> {
              if (col > 0) {
                val newPath = Path(
                  lastBlock = Block(row, col - 1, lastThree),
                  heatCosts = path.heatCosts.plus(map[row][col - 1]),
                  steps = path.steps.plus(directionToTravel),
                  cost = path.cost + map[row][col - 1]
                )
                BlockWithPath(Block(row, col - 1, lastThree), newPath)
              } else {
                null
              }
            }
          }
        }
      }

      override fun node(): Block = block

      override fun cost(): Path = path
    }

    override fun Block.withCost(cost: Path) = BlockWithPath(this, cost)
    override fun Path.plus(cost: Path): Path {
      return cost.copy(cost = cost.cost + (abs(cost.lastBlock.row - map.size) + abs(cost.lastBlock.col - map[0].size)))
    }

    override fun minCost() = Path(Block(0, 0, emptyList()), emptyList(), emptyList(), 0)
    override fun maxCost() = Path(Block(0, 0, emptyList()), emptyList(), emptyList(), Int.MAX_VALUE)
  }

  data class Block(val row: Int, val col: Int, val lastThree: List<Direction>)

  data class Path(
    val lastBlock: Block,
    val heatCosts: List<Int>,
    val steps: List<Direction>,
    val cost: Int = steps.size
  ) : Comparable<Path> {
    override fun compareTo(other: Path) = (cost + heatCosts.sum()).compareTo(other.cost + other.heatCosts.sum())
  }

  enum class Direction {
    NORTH, SOUTH, EAST, WEST
  }
}
