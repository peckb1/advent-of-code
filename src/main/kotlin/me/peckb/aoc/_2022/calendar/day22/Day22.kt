package me.peckb.aoc._2022.calendar.day22

import me.peckb.aoc._2022.calendar.day22.Day22.Area.*
import me.peckb.aoc._2022.calendar.day22.Day22.Direction.*
import me.peckb.aoc._2022.calendar.day22.Day22.Movement.*
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
    var direction = RIGHT

    movements.forEach { movement ->
      when (movement) {
        LeftTurn -> direction = direction.turnLeft()
        RightTurn -> direction = direction.turnRight()
        is Walk -> {
          repeat(movement.steps) {
            when (direction) {
              LEFT -> {
                var wantedX = x - 1
                if (wantedX < 0 || caves[y][wantedX] == VOID) wantedX = caves[y].indexOfLast { it != VOID }
                if (caves[y][wantedX] == EMPTY) x = wantedX
              }

              RIGHT -> {
                var wantedX = x + 1
                if (wantedX >= caves[y].size || caves[y][wantedX] == VOID) wantedX = caves[y].indexOfFirst { it != VOID }
                if (caves[y][wantedX] == EMPTY) x = wantedX
              }

              UP -> {
                var wantedY = y - 1
                if (wantedY < 0 || caves[wantedY][x] == VOID) wantedY = (caves.size - 1 downTo 1).first { caves[it][x] != VOID }
                if (caves[wantedY][x] == EMPTY) y = wantedY
              }

              DOWN -> {
                var wantedY = y + 1
                if (wantedY >= caves.size || caves[wantedY][x] == VOID) wantedY = (0 until caves.size - 1).first { caves[it][x] != VOID }
                if (caves[wantedY][x] == EMPTY) y = wantedY
              }
            }
          }
        }
      }
    }

    findPassword(x, y, direction)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (caves, movements) = loadArea(input)

    var y = 0
    var x = caves[y].indexOf(EMPTY)
    var direction = RIGHT
    var cubeFace = FACE_ONE

    movements.forEach { movement ->
      when (movement) {
        LeftTurn -> direction = direction.turnLeft()
        RightTurn -> direction = direction.turnRight()
        is Walk -> {
          repeat(movement.steps) {
            when (direction) {
              LEFT -> {
                var transition = FaceTransition(x - 1, y, direction, cubeFace)
                if (transition.newX < 0 || caves[transition.newY][transition.newX] == VOID) {
                  when (cubeFace) {
                    FACE_ONE -> transition = leftToRight(y, cubeFace, FACE_FOUR)
                    FACE_THREE -> transition = leftToDown(y, cubeFace, FACE_FOUR)
                    FACE_FOUR -> transition = leftToRight(y, cubeFace, FACE_ONE)
                    FACE_SIX -> transition = leftToDown(y, cubeFace, FACE_ONE)
                  }
                } else {
                  when (cubeFace) {
                    FACE_TWO -> if (transition.newX < cubeFace.minX) transition.newFace = FACE_ONE
                    FACE_FIVE -> if (transition.newX < cubeFace.minX) transition.newFace = FACE_FOUR
                  }
                }
                // TODO: move this to a common location
                if (caves[transition.newY][transition.newX] == EMPTY) {
                  x = transition.newX
                  y = transition.newY
                  direction = transition.newDirection
                  cubeFace = transition.newFace
                }
              }

              RIGHT -> {
                var transition = FaceTransition(x + 1, y, direction, cubeFace)
                if (transition.newX >= caves[transition.newY].size || caves[transition.newY][transition.newX] == VOID) {
                  when (cubeFace) {
                    FACE_TWO -> transition = rightToLeft(y, cubeFace, FACE_FIVE)
                    FACE_THREE -> transition = rightToUp(y, cubeFace, FACE_TWO)
                    FACE_FIVE -> transition = rightToLeft(y, cubeFace, FACE_TWO)
                    FACE_SIX -> transition = rightToUp(y, cubeFace, FACE_FIVE)
                  }
                } else {
                  when (cubeFace) {
                    FACE_ONE -> if (transition.newX > cubeFace.maxX) transition.newFace = FACE_TWO
                    FACE_FOUR -> if (transition.newX > cubeFace.maxX) transition.newFace = FACE_FIVE
                  }
                }
                // TODO: move this to a common location
                if (caves[transition.newY][transition.newX] == EMPTY) {
                  x = transition.newX
                  y = transition.newY
                  direction = transition.newDirection
                  cubeFace = transition.newFace
                }
              }

              UP -> {
                var wantedX = x
                var wantedY = y - 1
                var wantedRelativeDirection = direction
                var wantedCubeNumber = cubeFace
                if (wantedY < 0 || caves[wantedY][wantedX] == VOID) {
                  when (cubeFace) {
                    FACE_ONE -> {
                      // going from 1 -> 6
                      wantedX = FACE_SIX.minX
                      wantedY = FACE_SIX.minY + (x - FACE_ONE.minX)
                      wantedRelativeDirection = RIGHT
                      wantedCubeNumber = FACE_SIX
                    }

                    FACE_TWO -> {
                      // going from 2 -> 6
                      wantedX = FACE_SIX.minX + (x - FACE_TWO.minX)
                      wantedY = FACE_SIX.maxY
                      wantedRelativeDirection = UP
                      wantedCubeNumber = FACE_SIX
                    }

                    FACE_FOUR -> {
                      // going from 4 -> 3
                      wantedX = FACE_THREE.minX
                      wantedY = FACE_THREE.minY + (x - FACE_FOUR.minX)
                      wantedRelativeDirection = RIGHT
                      wantedCubeNumber = FACE_THREE
                    }
                  }
                } else {
                  when (cubeFace) {
                    FACE_THREE -> if (wantedY < FACE_THREE.minY) wantedCubeNumber = FACE_ONE
                    FACE_FIVE -> if (wantedY < FACE_FIVE.minY) wantedCubeNumber = FACE_THREE
                    FACE_SIX -> if (wantedY < FACE_SIX.minY) wantedCubeNumber = FACE_FOUR
                  }
                }
                // TODO: move this to a common location
                if (caves[wantedY][wantedX] == EMPTY) {
                  x = wantedX
                  y = wantedY
                  direction = wantedRelativeDirection
                  cubeFace = wantedCubeNumber
                }
              }

              DOWN -> {
                var wantedX = x
                var wantedY = y + 1
                var wantedRelativeDirection = direction
                var wantedCubeNumber = cubeFace
                if (wantedY >= caves.size || caves[wantedY][wantedX] == VOID) {
                  when (cubeFace) {
                    FACE_TWO -> {
                      // going from 2 -> 3
                      wantedX = FACE_THREE.maxX
                      wantedY = FACE_THREE.minY + (x - FACE_TWO.minX)
                      wantedRelativeDirection = LEFT
                      wantedCubeNumber = FACE_THREE
                    }

                    FACE_FIVE -> {
                      // going from 5 -> 6
                      wantedX = FACE_SIX.maxX
                      wantedY = FACE_SIX.minY + (x - FACE_FIVE.minX)
                      wantedRelativeDirection = LEFT
                      wantedCubeNumber = FACE_SIX
                    }

                    FACE_SIX -> {
                      // going from 6 -> 2
                      wantedX = FACE_TWO.minX + (x - FACE_SIX.minX)
                      wantedY = FACE_TWO.minY
                      wantedRelativeDirection = DOWN
                      wantedCubeNumber = FACE_TWO
                    }
                  }
                } else {
                  when (cubeFace) {
                    FACE_ONE -> if (wantedY > FACE_ONE.maxY) wantedCubeNumber = FACE_THREE
                    FACE_THREE -> if (wantedY > FACE_THREE.maxY) wantedCubeNumber = FACE_FIVE
                    FACE_FOUR -> if (wantedY > FACE_FOUR.maxY) wantedCubeNumber = FACE_SIX
                  }
                }
                // TODO: move this to a common location
                if (caves[wantedY][wantedX] == EMPTY) {
                  x = wantedX
                  y = wantedY
                  direction = wantedRelativeDirection
                  cubeFace = wantedCubeNumber
                }
              }
            }
          }
        }
      }
    }

    findPassword(x, y, direction)
  }

  private fun loadArea(input: Sequence<String>): Pair<MutableList<MutableList<Area>>, MutableList<Movement>> {
    var map = true

    val caves = mutableListOf<MutableList<Area>>()
    val movements = mutableListOf<Movement>()

    input.forEach {line ->
      if (line.isEmpty()) {
        map = false
        return@forEach
      }

      if (map) {
        val row = mutableListOf<Area>()
        line.forEach { c ->
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
        while (nextTurn != -1) {
          movements.add(Walk(line.substring(currentIndex, nextTurn).toInt()))
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
        movements.add(Walk(line.substring(currentIndex).toInt()))
      }
    }

    return caves to movements
  }

  private fun leftToRight(y: Int, source: CubeFace, destination: CubeFace): FaceTransition {
    val newX = destination.minX
    val newY = destination.maxY - (y - source.minY)
    val newDirection = RIGHT

    return FaceTransition(newX, newY, newDirection, destination)
  }

  private fun leftToDown(y: Int, source: CubeFace, destination: CubeFace): FaceTransition {
    val newX = destination.minX + (y - source.minY)
    val newY = destination.minY
    val newDirection = DOWN

    return FaceTransition(newX, newY, newDirection, destination)
  }

  private fun rightToLeft(y: Int, source: CubeFace, destination: CubeFace): FaceTransition {
    val newX = destination.maxX
    val newY = destination.maxY - (y - source.minY)
    val newDirection = LEFT

    return FaceTransition(newX, newY, newDirection, destination)
  }

  private fun rightToUp(y: Int, source: CubeFace, destination: CubeFace): FaceTransition {
    val newX = destination.minX + (y - source.minY)
    val newY = destination.maxY
    val newDirection = UP

    return FaceTransition(newX, newY, newDirection, destination)
  }

  private fun findPassword(x: Int, y: Int, direction: Direction) = (1000 * (y + 1)) + (4 * (x + 1)) + direction.score

  data class FaceTransition(val newX: Int, val newY: Int, val newDirection: Direction, var newFace: CubeFace)

  enum class Area { WALL, EMPTY, VOID }

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

  data class CubeFace(val minX: Int, val maxX: Int, val minY: Int, val maxY: Int)

  companion object {
    private val FACE_ONE = CubeFace(50, 99, 0, 49)
    private val FACE_TWO = CubeFace(100, 149, 0, 49)
    private val FACE_THREE = CubeFace(50, 99, 50, 99)
    private val FACE_FOUR = CubeFace(0, 49, 100, 149)
    private val FACE_FIVE = CubeFace(50, 99, 100, 149)
    private val FACE_SIX = CubeFace(0, 49, 150, 199)
  }
}
