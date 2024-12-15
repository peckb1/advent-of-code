package me.peckb.aoc._2024.calendar.day15

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day15 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    var gx = -1
    var gy = -1
    val (area, directions) = setupArea(input) { c, y, x ->
      listOf(
        when (c) {
          '#'  -> Room.WALL
          '.'  -> Room.EMPTY
          'O'  -> Room.BOX
          else -> Room.GUARD.also { gx = x; gy = y }
        }
      )
    }

    directions.forEach { direction ->
      val (yDelta, xDelta) = when (direction) {
        Direction.N -> -1 to  0
        Direction.E ->  0 to  1
        Direction.S ->  1 to  0
        Direction.W ->  0 to -1
      }

      var boxEnd: Room
      var boxes = 0
      do {
        boxes++
        boxEnd = area[gy + (yDelta * boxes)][gx + (xDelta * boxes)]
      } while (boxEnd == Room.BOX)
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

    area.sumBoxes { it == Room.BOX }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    lateinit var guard: WideRoom
    val (area, directions) = setupArea(input) { c, y, x ->
      val x1 = x*2
      val x2 = x1 + 1
      when (c) {
        '#'  -> listOf(WideRoom.Wall   (y, x1), WideRoom.Wall    (y, x2))
        '.'  -> listOf(WideRoom.Empty  (y, x1), WideRoom.Empty   (y, x2))
        'O'  -> listOf(WideRoom.LeftBox(y, x1), WideRoom.RightBox(y, x2))
        else -> listOf(WideRoom.Guard  (y, x1), WideRoom.Empty   (y, x2)).also { guard = it.first() }
      }
    }

    directions.forEach { direction ->
      if (direction == Direction.W || direction == Direction.E) {
        val xDelta = when (direction) {
          Direction.E -> 1
          Direction.W -> -1
          else -> throw IllegalStateException("can't move N/S in an E/W block")
        }

        val guardRow = area[guard.y]
        var boxEnd: WideRoom
        var boxes = 0
        do {
          boxes++
          boxEnd = guardRow[guard.x + (xDelta * boxes)]
        } while (boxEnd is WideRoom.RightBox || boxEnd is WideRoom.LeftBox)
        boxes--

        if (boxEnd is WideRoom.Empty) {
          while (boxes > 0) {
            val x = guard.x + (xDelta * (boxes + 1))
            guardRow[x] = guardRow[guard.x + (xDelta * boxes)].also { it.x = x }
            boxes--
          }
          guardRow[guard.x] = WideRoom.Empty(guard.y, guard.x)
          guard.x += xDelta
          guardRow[guard.x] = guard
        }
      } else { // N/S Block!
        val yDelta = when (direction) {
          Direction.N -> -1
          Direction.S -> 1
          else -> throw IllegalStateException("can't move N/S in an E/W block")
        }

        var allCanMove = true
        var doneSearching = false
        val areasToMove = mutableMapOf<Int, Set<WideRoom>>().apply {
          put(guard.y, setOf(area[guard.y][guard.x]))
        }

        var currentY = guard.y
        while(allCanMove && !doneSearching) {
          areasToMove[currentY]?.forEach { room ->
            val searchY = currentY + yDelta
            when (val nextArea = area[searchY][room.x]) {
              is WideRoom.Wall -> allCanMove = false
              is WideRoom.LeftBox -> {
                areasToMove.merge(searchY, setOf(nextArea)) { a, b -> a + b }
                areasToMove.merge(searchY, setOf(area[searchY][room.x + 1])) { a, b -> a + b }
              }
              is WideRoom.RightBox -> {
                areasToMove.merge(searchY, setOf(nextArea)) { a, b -> a + b }
                areasToMove.merge(searchY, setOf(area[searchY][room.x - 1])) { a, b -> a + b }
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
              val nextY = room.y - yDelta
              if (areasToMove[y - yDelta]?.contains(area[nextY][room.x]) != true) {
                area[room.y][room.x] = WideRoom.Empty(nextY, room.x)
              }
              room.y += yDelta
              area[room.y][room.x] = room
            }
          }
        }
      }
    }

    area.sumBoxes { it is WideRoom.LeftBox }
  }

  private fun <T> MutableList<MutableList<T>>.sumBoxes(roomCheck: (T) -> Boolean): Int {
    return withIndex().sumOf { (y, row) ->
      row.withIndex().sumOf { (x, room) ->
        if (roomCheck(room)) { (100 * y) + x } else { 0 }
      }
    }
  }

  private fun <RoomType> setupArea(
    input: Sequence<String>,
    roomCreator: (Char, Int, Int) -> List<RoomType>
  ): Pair<MutableList<MutableList<RoomType>>, MutableList<Direction>> {
    val area = mutableListOf<MutableList<RoomType>>()
    val directions = mutableListOf<Direction>()

    var setupArea = true
    input.forEachIndexed setup@ { y, line ->
      if (line.isEmpty()) {
        setupArea = false
        return@setup
      }
      if (setupArea) {
        val row = mutableListOf<RoomType>()
        line.forEachIndexed { x, c -> roomCreator(c, y, x).forEach(row::add) }
        area.add(row)
      } else {
        line.forEach { c ->
          when (c) {
            '<' -> directions.add(Direction.W)
            '^' -> directions.add(Direction.N)
            '>' -> directions.add(Direction.E)
            'v' -> directions.add(Direction.S)
          }
        }
      }
    }

    return area to directions
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
