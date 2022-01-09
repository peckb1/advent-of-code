package me.peckb.aoc._2016.calendar.day22

import arrow.core.foldLeft
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max

class Day22 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::node) { input ->
    val nodesByAvailability = sortedMapOf<Int, MutableList<Node>>()
    val nodesBySpaceUsed = sortedMapOf<Int, MutableList<Node>>()

    input.filterNotNull().forEach { node ->
      nodesByAvailability.merge(node.available, mutableListOf(node)) { existing, current ->
        existing.apply { addAll(current) }
      }
      nodesBySpaceUsed.merge(node.used, mutableListOf(node)) { existing, current ->
        existing.apply { addAll(current) }
      }
    }

    val viablePairs = nodesBySpaceUsed.foldLeft(0L) { usedAcc, (used, usedList) ->
      if (used == 0) {
        usedAcc
      } else {
        usedAcc + usedList.size * nodesByAvailability.foldLeft(0L) { availableAcc, (available, availableNodes) ->
          availableAcc + if (used <= available) {
            availableNodes.size.toLong()
          } else {
            availableAcc
          }
        }
      }
    }

    viablePairs
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::node) { input ->
    val data = input.filterNotNull().toList()
    var maxX = 0
    var maxY = 0
    var capacityTotal = 0
    lateinit var emptyNode: Node

    data.forEach { node ->
      if (node.used == 0) emptyNode = node
      capacityTotal += node.capacity
      maxX = max(maxX, node.x)
      maxY = max(maxY, node.y)
    }

    val averageCapacity = capacityTotal / (maxX * maxY)
    val averageRange = (averageCapacity / 2)..(averageCapacity * 1.5).toInt()
    val grid = Array(maxY + 1) { Array(maxX + 1) { ' ' } }

    data.forEach { node ->
      grid[node.y][node.x] = when (node.capacity) {
        in averageRange -> if (node.used != 0) '.' else '-'
        else -> '#'
      }
    }

    // find the wall
    val wall = grid.withIndex().maxByOrNull { it.value.count { space -> space == '#' } }!!
    val xValueOfHold = wall.value.indexOf('-')

    // find out how many Y movements to get to the wall
    ((emptyNode.y - 1) - wall.index) +
      // find out how many X movements to get to the hole
      ((emptyNode.x - 1) - wall.value.indexOf('-')) +
      // two moves cross the hole
      2 +
      // find out how many X movements to get to maxX
      ((maxX - 1) - xValueOfHold) +
      // find out how many Y movements to get to maxY
      (wall.index - 1) +
      // the loop-around costs 5, and we need maxX - 1 of them
      (5 * (maxX - 1))
  }

  private fun node(line: String): Node? {
    if (!line.startsWith("/dev/grid")) return null
    // Filesystem              Size  Used  Avail  Use%
    // /dev/grid/node-x0-y0     85T   67T    18T   78%
    val parts = line.split(WHITESPACE_REGEX)
    val x = parts[0].substringAfter("x").substringBefore("-").toInt()
    val y = parts[0].substringAfter("y").toInt()

    val capacity = parts[1].dropLast(1).toInt()
    val used = parts[2].dropLast(1).toInt()
    val available = parts[3].dropLast(1).toInt()

    return Node(x, y, capacity, used, available)
  }

  data class Node(val x: Int, val y: Int, val capacity: Int, val used: Int, val available: Int)

  companion object {
    private val WHITESPACE_REGEX = "\\s+".toRegex()
    private val NO_NODE = Node(-1, -1, 0, 0, 0)
  }
}
