package me.peckb.aoc._2024.calendar.day15

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day15 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    var gx = -1
    var gy = -1
    val area = mutableListOf<MutableList<Room>>()
    val directions = mutableListOf<Direction>()

    var setupArea = true
    input.forEachIndexed setup@ { yIndex, line ->
      if (line.isEmpty()) {
        setupArea = false
        return@setup
      }
      if (setupArea) {
        val row = mutableListOf<Room>()
        line.forEachIndexed { xIndex, c ->
          when (c) {
            '#' -> row.add(Room.WALL())
            '.' -> row.add(Room.EMPTY())
            'O' -> row.add(Room.BOX())
            else -> {
              row.add(Room.GUARD())
              gx = xIndex
              gy = yIndex
            }
          }
        }
        area.add(row)
      } else {
        line.forEach { c ->
          when (c) {
            '<' -> directions.add(Direction.W)
            '^' -> directions.add(Direction.N)
            '>' -> directions.add(Direction.E)
            else -> directions.add(Direction.S)
          }
        }
      }
    }

    directions.forEach { direction ->
      val (yDelta, xDelta) = when (direction) {
        Direction.N -> -1 to 0
        Direction.E -> 0 to 1
        Direction.S -> 1 to 0
        Direction.W -> 0 to -1
      }

      var boxes = 1
      var boxEnd = area[gy + (yDelta * boxes)][gx + (xDelta * boxes)]
      while (boxEnd is Room.BOX) {
        boxes++
        boxEnd = area[gy + (yDelta * boxes)][gx + (xDelta * boxes)]
      }
      boxes--
      if (boxEnd is Room.EMPTY) {
        while (boxes > 0) {
          area[gy + (yDelta * (boxes + 1))][gx + (xDelta * (boxes + 1))] = Room.BOX()
          boxes--
        }
        area[gy][gx] = Room.EMPTY()
        gx += xDelta
        gy += yDelta
        area[gy][gx] = Room.GUARD()
      }
    }

    area.withIndex().sumOf { (y, row) ->
      row.withIndex().sumOf { (x, room) ->
        when (room) {
          is Room.BOX -> (100 * y) + x
          else -> 0
        }
      }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->

  }

  private fun MutableList<MutableList<Room>>.print() {
    forEach { row ->
      println(row.joinToString(""))
    }
    println()
  }
}

sealed class Room(val c: Char) {
  class EMPTY : Room('.')
  class WALL : Room('#')
  class BOX : Room('O')
  class BOX_L : Room('[')
  class BOX_R : Room(']')
  class GUARD : Room('@')

  var n: Room? = null
  var s: Room? = null
  var e: Room? = null
  var w: Room? = null

  override fun toString(): String {
    return c.toString()
  }
}

enum class Direction {
  N,E,S,W
}