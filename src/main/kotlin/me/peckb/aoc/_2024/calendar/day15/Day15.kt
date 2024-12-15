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
            '#' -> row.add(Room.WALL)
            '.' -> row.add(Room.EMPTY)
            'O' -> row.add(Room.BOX)
            else -> {
              row.add(Room.GUARD)
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
      while (boxEnd == Room.BOX) {
        boxes++
        boxEnd = area[gy + (yDelta * boxes)][gx + (xDelta * boxes)]
      }
      boxes--
      if (boxEnd == Room.EMPTY) {
        while (boxes > 0) {
          area[gy + (yDelta * (boxes + 1))][gx + (xDelta * (boxes + 1))] = Room.BOX
          boxes--
        }
        area[gy][gx] = Room.EMPTY
        gx += xDelta
        gy += yDelta
        area[gy][gx] = Room.GUARD
      }
    }

    area.withIndex().sumOf { (y, row) ->
      row.withIndex().sumOf { (x, room) ->
        when (room) {
          Room.BOX -> (100 * y) + x
          else -> 0
        }
      }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    var guard: WideRoom = WideRoom.Guard(-1, -1)
    val area = mutableListOf<MutableList<WideRoom>>()
    val directions = mutableListOf<Direction>()

    var setupArea = true
    input.forEachIndexed setup@ { y, line ->
      if (line.isEmpty()) {
        setupArea = false
        return@setup
      }
      if (setupArea) {
        val row = mutableListOf<WideRoom>()
        line.forEachIndexed { x, c ->
          val x1 = x*2
          val x2 = x*2 + 1
          when (c) {
            '#' -> {
              row.add(WideRoom.Wall(y, x1))
              row.add(WideRoom.Wall(y, x2))
            }
            '.' -> {
              row.add(WideRoom.Empty(y, x1))
              row.add(WideRoom.Empty(y, x2))
            }
            'O' -> {
              row.add(WideRoom.LeftBox(y, x1))
              row.add(WideRoom.RightBox(y, x2))
            }
            else -> {
              row.add(WideRoom.Guard(y, x1).also { guard = it })
              row.add(WideRoom.Empty(y, x2))
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
      if (direction == Direction.W || direction == Direction.E) {
        val xDelta = when (direction) {
          Direction.E -> 1
          Direction.W -> -1
          else -> throw IllegalStateException("can't move N/S in an E/W block")
        }

        var boxes = 1
        var boxEnd = area[guard.y][guard.x + (xDelta * boxes)]
        while (boxEnd is WideRoom.RightBox || boxEnd is WideRoom.LeftBox) {
          boxes++
          boxEnd = area[guard.y][guard.x + (xDelta * boxes)]
        }
        boxes--
        if (boxEnd is WideRoom.Empty) {
          while (boxes > 0) {
            val x = guard.x + (xDelta * (boxes + 1))
            area[guard.y][x] = area[guard.y][guard.x + (xDelta * boxes)].also { it.x = x }
            boxes--
          }
          area[guard.y][guard.x] = WideRoom.Empty(guard.y, guard.x)
          guard.x += xDelta
          area[guard.y][guard.x] = guard
        }
      } else { // N/S Block!
        val yDelta = when (direction) {
          Direction.N -> -1
          Direction.S -> 1
          else -> throw IllegalStateException("can't move N/S in an E/W block")
        }

        var allCanMove = true
        var doneSearching = false
        val areasToMove = mutableMapOf<Int, Set<WideRoom>>()
        areasToMove[guard.y] = setOf(area[guard.y][guard.x])
        var currentY = guard.y
        while(allCanMove && !doneSearching) {
          areasToMove[currentY]?.forEach { room ->
            when (val nextArea = area[room.y + yDelta][room.x]) {
              is WideRoom.Wall -> allCanMove = false
              is WideRoom.LeftBox -> {
                areasToMove.merge(currentY + yDelta, setOf(nextArea)) { a, b -> a + b }
                areasToMove.merge(currentY + yDelta, setOf(area[room.y + yDelta][room.x + 1])) { a, b -> a + b }
              }
              is WideRoom.RightBox -> {
                areasToMove.merge(currentY + yDelta, setOf(nextArea)) { a, b -> a + b }
                areasToMove.merge(currentY + yDelta, setOf(area[room.y + yDelta][room.x - 1])) { a, b -> a + b }
              }
              else -> { /* ignore empty rooms */ }
            }
          } ?: run { doneSearching = true }
          currentY += yDelta
        }

        if (allCanMove) {
          val sortedEntries = areasToMove.entries.sortedBy { it.key }
            .let { if (direction == Direction.S) { it.reversed() } else { it } }

          sortedEntries.forEach { (y, wideRooms) ->
            wideRooms.forEach { room ->
              if (areasToMove[y - yDelta]?.contains(area[room.y - yDelta][room.x]) != true) {
                area[room.y][room.x] = WideRoom.Empty(room.y - yDelta, room.x)
              }
              room.y += yDelta
              area[room.y][room.x] = room
            }
          }
        }
      }
    }

    area.withIndex().sumOf { (y, row) ->
      row.withIndex().sumOf { (x, room) ->
        when (room) {
          is WideRoom.LeftBox -> (100 * y) + x
          else -> 0
        }
      }
    }
  }
}

enum class Room(val c: Char) {
  EMPTY('.'), WALL('#'), BOX('O'), GUARD('@');

  override fun toString(): String { return c.toString() }
}

sealed class WideRoom(val c: Char, var y: Int, var x: Int) {
  class Empty(y: Int, x: Int)    : WideRoom('.', y, x)
  class Guard(y: Int, x: Int)    : WideRoom('@', y, x)
  class Wall(y: Int, x: Int)     : WideRoom('#', y, x)
  class LeftBox(y: Int, x: Int)  : WideRoom('[', y, x)
  class RightBox(y: Int, x: Int) : WideRoom(']', y, x)

  override fun toString(): String { return c.toString() }
}

enum class Direction { N,E,S,W }
