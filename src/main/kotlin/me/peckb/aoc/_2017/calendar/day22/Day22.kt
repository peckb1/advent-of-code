package me.peckb.aoc._2017.calendar.day22

import me.peckb.aoc._2017.calendar.day22.Day22.Direction.DOWN
import me.peckb.aoc._2017.calendar.day22.Day22.Direction.LEFT
import me.peckb.aoc._2017.calendar.day22.Day22.Direction.RIGHT
import me.peckb.aoc._2017.calendar.day22.Day22.Direction.UP
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day22 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val growth = 100

    val center = input.toList()
    val newWorldSize = (center.size * growth) + 1
    val map = Array(newWorldSize) { Array(newWorldSize) { '.' } }

    center.indices.forEach { y ->
      center.indices.forEach { x ->
        if (center[y][x] == '#') {
          val yy = (newWorldSize / 2) - (center.size / 2) + y
          val xx = (newWorldSize / 2) - (center.size / 2) + x
          map[yy][xx] = '#'
        }
      }
    }

    val bursts = 10_000
    var wormX = newWorldSize / 2
    var wormY = newWorldSize / 2
    var direction = UP

    var infectionsCount = 0

    repeat(bursts) {
      if (map[wormY][wormX] == '.') {
        direction = direction.turnLeft()
        map[wormY][wormX] = '#'
        infectionsCount++
      } else {
        direction = direction.turnRight()
        map[wormY][wormX] = '.'
      }
      when (direction) {
        UP -> wormY--
        RIGHT -> wormX++
        DOWN -> wormY++
        LEFT -> wormX--
      }
    }

    infectionsCount
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val growth = 100

    val center = input.toList()
    val newWorldSize = (center.size * growth) + 1
    val map = Array(newWorldSize) { Array(newWorldSize) { '.' } }

    center.indices.forEach { y ->
      center.indices.forEach { x ->
        if (center[y][x] == '#') {
          val yy = (newWorldSize / 2) - (center.size / 2) + y
          val xx = (newWorldSize / 2) - (center.size / 2) + x
          map[yy][xx] = '#'
        }
      }
    }

    val bursts = 10_000_000
    var wormX = newWorldSize / 2
    var wormY = newWorldSize / 2
    var direction = UP

    var infectionsCount = 0

    repeat(bursts) {
      if (map[wormY][wormX] == '.') {
        direction = direction.turnLeft()
        map[wormY][wormX] = 'W'
      } else if (map[wormY][wormX] == 'W') {
        map[wormY][wormX] = '#'
        infectionsCount++
      } else if (map[wormY][wormX] == '#') {
        direction = direction.turnRight()
        map[wormY][wormX] = 'F'
      } else if (map[wormY][wormX] == 'F') {
        direction = direction.reverse()
        map[wormY][wormX] = '.'
      } else {
        throw IllegalStateException("Unknown state: ${map[wormY][wormX]}")
      }
      when (direction) {
        UP -> wormY--
        RIGHT -> wormX++
        DOWN -> wormY++
        LEFT -> wormX--
      }
    }

    infectionsCount
  }

  enum class Direction { UP, RIGHT, DOWN, LEFT;
    fun turnLeft(): Direction {
      return when (this) {
        UP -> LEFT
        LEFT -> DOWN
        DOWN -> RIGHT
        RIGHT -> UP
      }
    }

    fun turnRight(): Direction {
      return when (this) {
        UP -> RIGHT
        RIGHT -> DOWN
        DOWN -> LEFT
        LEFT -> UP
      }
    }

    fun reverse(): Direction {
      return when (this) {
        UP -> DOWN
        RIGHT -> LEFT
        DOWN -> UP
        LEFT -> RIGHT
      }
    }
  }
}
