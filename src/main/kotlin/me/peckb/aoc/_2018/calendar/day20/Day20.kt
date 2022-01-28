package me.peckb.aoc._2018.calendar.day20

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.GenericIntDijkstra
import me.peckb.aoc.pathing.GenericIntDijkstra.DijkstraNode

class Day20 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { regex ->
    val (path, _) = findLongestRoute(regex, 0)

    path.length
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { regex ->
    val rooms = mutableSetOf<Point>()
    val doors = mutableSetOf<Point>()

    val source = Point(0, 0)
    rooms.add(source)
    popualteRoomsAndDoors(regex, 0, rooms, doors, source)

    data class Node(val y: Int, val x: Int) : DijkstraNode<Node> {
      override fun neighbors(): Map<Node, Int> {
        val n = Point(y - 1, x)
        val e = Point(y, x + 1)
        val s = Point(y + 1, x)
        val w = Point(y, x - 1)

        val neighbors = listOfNotNull(
          if (doors.contains(n)) { Point(y - 2, x) to 1 } else null,
          if (doors.contains(e)) { Point(y, x + 2) to 1 } else null,
          if (doors.contains(s)) { Point(y + 2, x) to 1 } else null,
          if (doors.contains(w)) { Point(y, x - 2) to 1 } else null
        )

        return neighbors.associate { Node(it.first.y, it.first.x) to it.second }
      }
    }

    val paths =  object : GenericIntDijkstra<Node>(){}.solve(Node(0, 0))

    paths.count { it.value >= 1000 }
  }

  private fun findLongestRoute(regex: String, startIndex: Int): Pair<String, Int> {
    val paths = mutableListOf<String>()
    var index = startIndex
    var path = StringBuilder()

    var done = false
    while(!done && index < regex.length) {
      when (val c = regex[index]) {
        'N', 'S', 'E', 'W' -> path.append(c)
        '(' -> {
          val (longestSubPath, newIndex) = findLongestRoute(regex, index + 1)
          path.append(longestSubPath)
          index = newIndex - 1
        }
        '|' -> {
          paths.add(path.toString())
          path = StringBuilder()
        }
        ')' -> {
          paths.add(path.toString())
          path = StringBuilder()
          done = true
        }
      }
      index++
    }

    if (paths.isEmpty()) {
      paths.add(path.toString())
    }

    return paths.map { it.removeBackSteps() }.maxByOrNull { it.length }!! to index
  }

  private fun String.removeBackSteps(): String {
    val result = StringBuilder()

    fun alterResult(me: Char, opposite: Char) {
      if (result.last() == opposite) {
        result.deleteCharAt(result.length - 1)
      } else {
        result.append(me)
      }
    }

    forEach { c ->
      if(result.isEmpty()) {
        result.append(c)
      } else {
        when (c) {
          'E' -> alterResult(c, 'W')
          'W' -> alterResult(c, 'E')
          'N' -> alterResult(c, 'S')
          'S' -> alterResult(c, 'N')
        }
      }
    }
    return result.toString()
  }

  private fun popualteRoomsAndDoors(regex: String, i: Int, rooms: MutableSet<Point>, doors: MutableSet<Point>, source: Point): Int {
    var current = source.copy()

    var index = i
    while(index < regex.length) {
      when (val c = regex[index]) {
        'N', 'E', 'W', 'S' -> {
          val (room, door) = when (c) {
            'N' -> current.moveUp()
            'S' -> current.moveDown()
            'E' -> current.moveRight()
            'W' -> current.moveLeft()
            else -> throw IllegalStateException("Unknown direction $c")
          }
          rooms.add(room)
          doors.add(door)
        }
        '(' -> { index = popualteRoomsAndDoors(regex, index + 1, rooms, doors, current) }
        '|' -> { current = source.copy() }
        ')' -> { return index }
      }
      index++
    }

    return index
  }
}
