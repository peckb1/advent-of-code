package me.peckb.aoc._2022.calendar.day22

import me.peckb.aoc._2022.calendar.day22.Day22.Area.EMPTY
import me.peckb.aoc._2022.calendar.day22.Day22.Area.WALL
import me.peckb.aoc._2022.calendar.day22.Day22.Area.VOID
import me.peckb.aoc._2022.calendar.day22.Day22.Movement.LeftTurn
import me.peckb.aoc._2022.calendar.day22.Day22.Movement.RightTurn
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalArgumentException

class Day22 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (caves, movements) = loadArea(input)

    var y = 0
    var x = caves[y].indexOf(EMPTY)
    var direction = Direction.RIGHT

    movements.forEach { movement ->
      when (movement) {
        LeftTurn -> direction = direction.turnLeft()
        RightTurn -> direction = direction.turnRight()
        is Movement.Walk -> {
          repeat(movement.steps) {
            when (direction) {
              Direction.LEFT -> {
                var wantedX = x - 1
                if (wantedX < 0 || caves[y][wantedX] == VOID) {
                  // loop around
                  wantedX = caves[y].indexOfLast { it != VOID }
                }
                if (caves[y][wantedX] == EMPTY) {
                  x = wantedX
                }
              }
              Direction.RIGHT -> {
                var wantedX = x + 1
                if (wantedX >= caves[y].size || caves[y][wantedX] == VOID) {
                  // loop around
                  wantedX = caves[y].indexOfFirst { it != VOID }
                }
                if (caves[y][wantedX] == EMPTY) {
                  x = wantedX
                }
              }
              Direction.UP -> {
                var wantedY = y - 1
                if (wantedY < 0 || caves[wantedY][x] == VOID) {
                  // loop around
                  wantedY = (caves.size - 1 downTo 1).first { caves[it][x] != VOID }
                }
                if (caves[wantedY][x] == EMPTY) {
                  y = wantedY
                }
              }
              Direction.DOWN -> {
                var wantedY = y + 1
                if (wantedY >= caves.size || caves[wantedY][x] == VOID) {
                  // loop around
                  wantedY = (0 until caves.size - 1).first { caves[it][x] != VOID }
                }
                if (caves[wantedY][x] == EMPTY) {
                  y = wantedY
                }
              }
            }
          }
        }
      }
    }

    // DEV NOTE: index things start at (1, 1)
    (1000 * (y + 1)) +
      (4 * (x + 1)) +
      direction.score
  }

  private fun loadArea(input: Sequence<String>): Pair<MutableList<MutableList<Area>>, MutableList<Movement>> {
    var map = true

    val caves = mutableListOf<MutableList<Area>>()
    val movements = mutableListOf<Movement>()

    input.forEachIndexed { y, line ->
      if (line.isEmpty()) {
        map = false
        return@forEachIndexed
      }

      if (map) {
        val row = mutableListOf<Area>()
        line.forEachIndexed { x, c ->
          when (c) {
            '.' -> row.add(EMPTY)
            '#' -> row.add(WALL)
            else -> row.add(VOID)
          }
        }
        caves.add(row)
      } else {
        var currentIndex = 0
        var nextTurn = line.drop(currentIndex).indexOfFirst { it == 'L' || it == 'R' }
        while(nextTurn != -1) {
          movements.add(Movement.Walk(line.substring(currentIndex, nextTurn).toInt()))
          when (line[nextTurn]) {
            'L' -> movements.add(LeftTurn)
            'R' -> movements.add(RightTurn)
            else -> throw IllegalArgumentException("Invalid movement [$nextTurn] ${line[nextTurn]}")
          }
          currentIndex = nextTurn + 1
          nextTurn = line.drop(currentIndex).indexOfFirst { it == 'L' || it == 'R' }.let {
            if (it != -1) it + currentIndex else it
          }
        }
        movements.add(Movement.Walk(line.substring(currentIndex).toInt()))
      }
    }

    return caves to movements
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::day22) { input ->
    -1
  }

  private fun day22(line: String) = 4

  enum class Area(private val representation: String) {
    WALL("#"), EMPTY("."), VOID(" ");

    override fun toString(): String {
      return representation
    }
  }

  sealed class Movement {
    object LeftTurn : Movement()
    object RightTurn : Movement()
    data class Walk(val steps: Int) : Movement()
  }

  enum class Direction(val score: Int) {
    LEFT(2) {
      override fun turnLeft() = DOWN
      override fun turnRight() = UP
    },
    RIGHT(0) {
      override fun turnLeft() = UP
      override fun turnRight() = DOWN
    },
    UP(3) {
      override fun turnLeft() = LEFT
      override fun turnRight() = RIGHT
    },
    DOWN(1) {
      override fun turnLeft() = RIGHT
      override fun turnRight() = LEFT
    };

    abstract fun turnLeft(): Direction
    abstract fun turnRight(): Direction
  }
}
