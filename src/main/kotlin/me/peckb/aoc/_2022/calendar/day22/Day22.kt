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
                if (wantedX < 0 || caves[y][wantedX] == VOID) {
                  wantedX = caves[y].indexOfLast { it != VOID }
                }
                if (caves[y][wantedX] == EMPTY) {
                  x = wantedX
                }
              }

              RIGHT -> {
                var wantedX = x + 1
                if (wantedX >= caves[y].size || caves[y][wantedX] == VOID) {
                  wantedX = caves[y].indexOfFirst { it != VOID }
                }
                if (caves[y][wantedX] == EMPTY) {
                  x = wantedX
                }
              }

              UP -> {
                var wantedY = y - 1
                if (wantedY < 0 || caves[wantedY][x] == VOID) {
                  // loop around
                  wantedY = (caves.size - 1 downTo 1).first { caves[it][x] != VOID }
                }
                if (caves[wantedY][x] == EMPTY) {
                  y = wantedY
                }
              }

              DOWN -> {
                var wantedY = y + 1
                if (wantedY >= caves.size || caves[wantedY][x] == VOID) {
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

    findPassword(x, y, direction)
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
    var cubeFaceNumber = 1

    // TODO: first debug/cleanup step is to put the walk over faces data into functions taking in the two sides as input
    // TODO: second debug/cleanup is to throw exceptions from states that should not be
    //       such as having a VOID when going from 3 -> 1

    movements.forEach { movement ->
      when (movement) {
        LeftTurn -> direction = direction.turnLeft()
        RightTurn -> direction = direction.turnRight()

        is Movement.Walk -> {
          repeat(movement.steps) {
            when (direction) {
              LEFT -> {
                var wantedX = x - 1
                var wantedY = y
                var wantedDirection = direction
                var wantedFace = cubeFaceNumber
                if (wantedX < 0 || caves[wantedY][wantedX] == VOID) {
                  when (cubeFaceNumber) {
                    1 -> {
                      // going from 1 -> 4
                      wantedX = faceFour.minX
                      wantedY = faceFour.maxY - (y - faceOne.minY)
                      wantedDirection = RIGHT
                      wantedFace = 4
                    }

                    2 -> {
                      // going from 2 -> 1
                      wantedFace = 1
                    }

                    3 -> {
                      // going from 3 -> 4
                      wantedX = faceFour.minX + (y - faceThree.minY)
                      wantedY = faceFour.minY
                      wantedDirection = DOWN
                      wantedFace = 4
                    }

                    4 -> {
                      // going from 4 -> 1
                      wantedX = faceOne.minX
                      wantedY = faceOne.maxY - (y - faceFour.minY)
                      wantedDirection = RIGHT
                      wantedFace = 1
                    }

                    5 -> {
                      // going from 5 -> 4
                      wantedFace = 4
                    }

                    6 -> {
                      // going from 6 -> 1
                      wantedX = faceOne.minX + (y - faceSix.minY)
                      wantedY = faceOne.minY
                      wantedDirection = DOWN
                      wantedFace = 1
                    }
                  }
                } else {
                  when (cubeFaceNumber) {
                    2 -> if (wantedX < faceTwo.minX) wantedFace = 1
                    5 -> if (wantedX < faceFive.minX) wantedFace = 4
                  }
                }
                if (caves[wantedY][wantedX] == EMPTY) {
                  x = wantedX
                  y = wantedY
                  direction = wantedDirection
                  cubeFaceNumber = wantedFace
                }
              }

              RIGHT -> {
                var wantedX = x + 1
                var wantedY = y
                var wantedRelativeDirection = direction
                var wantedCubeNumber = cubeFaceNumber
                if (wantedX >= caves[wantedY].size || caves[wantedY][wantedX] == VOID) {
                  when (cubeFaceNumber) {
                    1 -> {
                      // going from 1 -> 2
                      wantedCubeNumber = 2
                    }

                    2 -> {
                      // going from 2 -> 5
                      wantedX = faceFive.maxX
                      wantedY = faceFive.maxY - (y - faceTwo.minY)
                      wantedRelativeDirection = LEFT
                      wantedCubeNumber = 5
                    }

                    3 -> {
                      // going from 3 -> 2
                      wantedX = faceTwo.minX + (y - faceThree.minY)
                      wantedY = faceTwo.maxY
                      wantedRelativeDirection = UP
                      wantedCubeNumber = 2
                    }

                    4 -> {
                      // going from 4 -> 5
                      wantedCubeNumber = 5
                    }

                    5 -> {
                      // going from 5 -> 2
                      wantedX = faceTwo.maxX
                      wantedY = faceTwo.maxY - (y - faceFive.minY)
                      wantedRelativeDirection = LEFT
                      wantedCubeNumber = 2
                    }

                    6 -> {
                      // going from 6 -> 5
                      wantedX = faceFive.minX + (y - faceSix.minY)
                      wantedY = faceFive.maxY
                      wantedRelativeDirection = UP
                      wantedCubeNumber = 5
                    }
                  }
                } else {
                  when (cubeFaceNumber) {
                    1 -> if (wantedX > faceOne.maxX) wantedCubeNumber = 2
                    4 -> if (wantedX > faceFour.maxX) wantedCubeNumber = 5
                  }
                }
                if (caves[wantedY][wantedX] == EMPTY) {
                  x = wantedX
                  y = wantedY
                  direction = wantedRelativeDirection
                  cubeFaceNumber = wantedCubeNumber
                }
              }

              UP -> {
                var wantedX = x
                var wantedY = y - 1
                var wantedRelativeDirection = direction
                var wantedCubeNumber = cubeFaceNumber
                if (wantedY < 0 || caves[wantedY][wantedX] == VOID) {
                  when (cubeFaceNumber) {
                    1 -> {
                      // going from 1 -> 6
                      wantedX = faceSix.minX
                      wantedY = faceSix.minY + (x - faceOne.minX)
                      wantedRelativeDirection = RIGHT
                      wantedCubeNumber = 6
                    }

                    2 -> {
                      // going from 2 -> 6
                      wantedX = faceSix.minX + (x - faceTwo.minX)
                      wantedY = faceSix.maxY
                      wantedRelativeDirection = UP
                      wantedCubeNumber = 6
                    }

                    3 -> {
                      // going from 3 -> 1
                      wantedCubeNumber = 1
                    }

                    4 -> {
                      // going from 4 -> 3
                      wantedX = faceThree.minX
                      wantedY = faceThree.minY + (x - faceFour.minX)
                      wantedRelativeDirection = RIGHT
                      wantedCubeNumber = 3
                    }

                    5 -> {
                      // going from 5 -> 3
                      wantedCubeNumber = 3
                    }

                    6 -> {
                      // going from 6 -> 4
                      wantedCubeNumber = 4
                    }
                  }
                } else {
                  when (cubeFaceNumber) {
                    3 -> if (wantedY < faceThree.minY) wantedCubeNumber = 1
                    5 -> if (wantedY < faceFive.minY) wantedCubeNumber = 3
                    6 -> if (wantedY < faceSix.minY) wantedCubeNumber = 4
                  }
                }
                if (caves[wantedY][wantedX] == EMPTY) {
                  x = wantedX
                  y = wantedY
                  direction = wantedRelativeDirection
                  cubeFaceNumber = wantedCubeNumber
                }
              }

              DOWN -> {
                var wantedX = x
                var wantedY = y + 1
                var wantedRelativeDirection = direction
                var wantedCubeNumber = cubeFaceNumber
                if (wantedY >= caves.size || caves[wantedY][wantedX] == VOID) {
                  when (cubeFaceNumber) {
                    1 -> {
                      // going from 1 -> 3
                      wantedCubeNumber = 3
                    }

                    2 -> {
                      // going from 2 -> 3
                      wantedX = faceThree.maxX
                      wantedY = faceThree.minY + (x - faceTwo.minX)
                      wantedRelativeDirection = LEFT
                      wantedCubeNumber = 3
                    }

                    3 -> {
                      // going from 3 -> 5
                      wantedCubeNumber = 5
                    }

                    4 -> {
                      // going from 4 -> 6
                      wantedCubeNumber = 6
                    }

                    5 -> {
                      // going from 5 -> 6
                      wantedX = faceSix.maxX
                      wantedY = faceSix.minY + (x - faceFive.minX)
                      wantedRelativeDirection = LEFT
                      wantedCubeNumber = 6
                    }

                    6 -> {
                      // going from 6 -> 2
                      wantedX = faceTwo.minX + (x - faceSix.minX)
                      wantedY = faceTwo.minY
                      wantedRelativeDirection = DOWN
                      wantedCubeNumber = 2
                    }
                  }
                } else {
                  when (cubeFaceNumber) {
                    1 -> if (wantedY > faceOne.maxY) wantedCubeNumber = 3
                    3 -> if (wantedY > faceThree.maxY) wantedCubeNumber = 5
                    4 -> if (wantedY > faceFour.maxY) wantedCubeNumber = 6
                  }
                }
                if (caves[wantedY][wantedX] == EMPTY) {
                  x = wantedX
                  y = wantedY
                  direction = wantedRelativeDirection
                  cubeFaceNumber = wantedCubeNumber
                }
              }
            }
          }
        }
      }
    }

    findPassword(x, y, direction)
  }

  private fun findPassword(x: Int, y: Int, direction: Direction) = (1000 * (y + 1)) + (4 * (x + 1)) + direction.score

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
