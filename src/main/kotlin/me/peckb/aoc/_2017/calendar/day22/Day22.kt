package me.peckb.aoc._2017.calendar.day22

import me.peckb.aoc._2017.calendar.day22.Day22.Direction.DOWN
import me.peckb.aoc._2017.calendar.day22.Day22.Direction.LEFT
import me.peckb.aoc._2017.calendar.day22.Day22.Direction.RIGHT
import me.peckb.aoc._2017.calendar.day22.Day22.Direction.UP
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day22 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val start = input.toList()
    val map = setupMap(start)
    val bursts = PART_ONE_BURSTS

    var wormX = INFINITE / 2
    var wormY = INFINITE / 2
    var direction = UP
    var infectionsCount = 0

    repeat(bursts) {
      map[wormY][wormX] = when(map[wormY][wormX]) {
        '.' -> { direction = direction.turnLeft(); infectionsCount++; '#' }
        '#' -> { direction = direction.turnRight(); '.' }
        else -> throw IllegalStateException("Unknown state: ${map[wormY][wormX]}")
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
    val start = input.toList()
    val map = setupMap(start)
    val bursts = PART_TWO_BURSTS

    var wormX = INFINITE / 2
    var wormY = INFINITE / 2
    var direction = UP
    var infectionsCount = 0

    repeat(bursts) {
      map[wormY][wormX] = when(map[wormY][wormX]) {
        '.' -> { direction = direction.turnLeft(); 'W' }
        'W' -> { infectionsCount++; '#' }
        '#' -> { direction = direction.turnRight(); 'F' }
        'F' -> { direction = direction.reverse(); '.' }
        else -> throw IllegalStateException("Unknown state: ${map[wormY][wormX]}")
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

  private fun setupMap(start: List<String>): Array<Array<Char>> {
    val newWorldSize = INFINITE
    val map = Array(newWorldSize) { Array(newWorldSize) { '.' } }

    start.indices.forEach { y ->
      start.indices.forEach { x ->
        if (start[y][x] == '#') {
          val yy = (newWorldSize / 2) - (start.size / 2) + y
          val xx = (newWorldSize / 2) - (start.size / 2) + x
          map[yy][xx] = '#'
        }
      }
    }

    return map
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

  companion object {
    const val INFINITE = 501
    const val PART_ONE_BURSTS = 10_000
    const val PART_TWO_BURSTS = 10_000_000
  }
}
