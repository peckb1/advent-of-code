package me.peckb.aoc._2022.calendar.day22

import me.peckb.aoc._2022.calendar.day22.Day22.Area.EMPTY
import me.peckb.aoc._2022.calendar.day22.Day22.Area.WALL
import me.peckb.aoc._2022.calendar.day22.Day22.Area.VOID
import me.peckb.aoc._2022.calendar.day22.Day22.Direction.*
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
    var direction = RIGHT

    movements.forEach { movement ->
      when (movement) {
        LeftTurn -> direction = direction.turnLeft()
        RightTurn -> direction = direction.turnRight()
        is Movement.Walk -> {
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

    val faceOne = CubeFace(50, 99, 0, 49)
    val faceTwo = CubeFace(100, 149, 0, 49)
    val faceThree = CubeFace(50, 99, 50, 99)
    val faceFour = CubeFace(0, 49, 100, 149)
    val faceFive = CubeFace(50, 99, 100, 149)
    val faceSix = CubeFace(0, 49, 150, 199)

    var y = 0
    var x = caves[y].indexOf(EMPTY)
    var direction = RIGHT
    var cubeFace = faceOne

    movements.forEach { movement ->
      when (movement) {
        LeftTurn -> direction = direction.turnLeft()
        RightTurn -> direction = direction.turnRight()

        is Movement.Walk -> {
          repeat(movement.steps) {
            when (direction) {
              LEFT -> {
                var transition = FaceTransition(x - 1, y, direction, cubeFace)
                if (transition.newX < 0 || caves[transition.newY][transition.newX] == VOID) {
                  when (cubeFace) {
                    faceOne -> transition = leftToRight(y, cubeFace, faceFour)
                    faceThree -> transition = leftToDown(y, cubeFace, faceFour)
                    faceFour -> transition = leftToRight(y, cubeFace, faceOne)
                    faceSix -> transition = leftToDown(y, faceSix, faceOne)
                  }
                } else {
                  when (cubeFace) {
                    faceTwo -> if (transition.newX < cubeFace.minX) transition = transition.copy(newFace = faceOne)
                    faceFive -> if (transition.newX < cubeFace.minX) transition = transition.copy(newFace = faceFour)
                  }
                }
                if (caves[transition.newY][transition.newX] == EMPTY) {
                  x = transition.newX
                  y = transition.newY
                  direction = transition.newDirection
                  cubeFace = transition.newFace
                }
              }

              RIGHT -> {
                var wantedX = x + 1
                var wantedY = y
                var wantedRelativeDirection = direction
                var wantedCubeNumber = cubeFace
                if (wantedX >= caves[wantedY].size || caves[wantedY][wantedX] == VOID) {
                  when (cubeFace) {
                    faceTwo -> {
                      // going from 2 -> 5
                      wantedX = faceFive.maxX
                      wantedY = faceFive.maxY - (y - faceTwo.minY)
                      wantedRelativeDirection = LEFT
                      wantedCubeNumber = faceFive
                    }

                    faceThree -> {
                      // going from 3 -> 2
                      wantedX = faceTwo.minX + (y - faceThree.minY)
                      wantedY = faceTwo.maxY
                      wantedRelativeDirection = UP
                      wantedCubeNumber = faceTwo
                    }

                    faceFive -> {
                      // going from 5 -> 2
                      wantedX = faceTwo.maxX
                      wantedY = faceTwo.maxY - (y - faceFive.minY)
                      wantedRelativeDirection = LEFT
                      wantedCubeNumber = faceTwo
                    }

                    faceSix -> {
                      // going from 6 -> 5
                      wantedX = faceFive.minX + (y - faceSix.minY)
                      wantedY = faceFive.maxY
                      wantedRelativeDirection = UP
                      wantedCubeNumber = faceFive
                    }
                  }
                } else {
                  when (cubeFace) {
                    faceOne -> if (wantedX > faceOne.maxX) wantedCubeNumber = faceTwo
                    faceFour -> if (wantedX > faceFour.maxX) wantedCubeNumber = faceFive
                  }
                }
                if (caves[wantedY][wantedX] == EMPTY) {
                  x = wantedX
                  y = wantedY
                  direction = wantedRelativeDirection
                  cubeFace = wantedCubeNumber
                }
              }

              UP -> {
                var wantedX = x
                var wantedY = y - 1
                var wantedRelativeDirection = direction
                var wantedCubeNumber = cubeFace
                if (wantedY < 0 || caves[wantedY][wantedX] == VOID) {
                  when (cubeFace) {
                    faceOne -> {
                      // going from 1 -> 6
                      wantedX = faceSix.minX
                      wantedY = faceSix.minY + (x - faceOne.minX)
                      wantedRelativeDirection = RIGHT
                      wantedCubeNumber = faceSix
                    }

                    faceTwo -> {
                      // going from 2 -> 6
                      wantedX = faceSix.minX + (x - faceTwo.minX)
                      wantedY = faceSix.maxY
                      wantedRelativeDirection = UP
                      wantedCubeNumber = faceSix
                    }

                    faceFour -> {
                      // going from 4 -> 3
                      wantedX = faceThree.minX
                      wantedY = faceThree.minY + (x - faceFour.minX)
                      wantedRelativeDirection = RIGHT
                      wantedCubeNumber = faceThree
                    }
                  }
                } else {
                  when (cubeFace) {
                    faceThree -> if (wantedY < faceThree.minY) wantedCubeNumber = faceOne
                    faceFive -> if (wantedY < faceFive.minY) wantedCubeNumber = faceThree
                    faceSix -> if (wantedY < faceSix.minY) wantedCubeNumber = faceFour
                  }
                }
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
                    faceTwo -> {
                      // going from 2 -> 3
                      wantedX = faceThree.maxX
                      wantedY = faceThree.minY + (x - faceTwo.minX)
                      wantedRelativeDirection = LEFT
                      wantedCubeNumber = faceThree
                    }

                    faceFive -> {
                      // going from 5 -> 6
                      wantedX = faceSix.maxX
                      wantedY = faceSix.minY + (x - faceFive.minX)
                      wantedRelativeDirection = LEFT
                      wantedCubeNumber = faceSix
                    }

                    faceSix -> {
                      // going from 6 -> 2
                      wantedX = faceTwo.minX + (x - faceSix.minX)
                      wantedY = faceTwo.minY
                      wantedRelativeDirection = DOWN
                      wantedCubeNumber = faceTwo
                    }
                  }
                } else {
                  when (cubeFace) {
                    faceOne -> if (wantedY > faceOne.maxY) wantedCubeNumber = faceThree
                    faceThree -> if (wantedY > faceThree.maxY) wantedCubeNumber = faceFive
                    faceFour -> if (wantedY > faceFour.maxY) wantedCubeNumber = faceSix
                  }
                }
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

  private fun findPassword(x: Int, y: Int, direction: Direction) = (1000 * (y + 1)) + (4 * (x + 1)) + direction.score

  data class FaceTransition(val newX: Int, val newY: Int, val newDirection: Direction, val newFace: CubeFace)

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
}
