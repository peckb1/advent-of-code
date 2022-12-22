package me.peckb.aoc._2022.calendar.day22

import me.peckb.aoc._2022.calendar.day22.CubeFace.Companion.FACE_ONE
import me.peckb.aoc._2022.calendar.day22.CubeFace.Companion.FACE_TWO
import me.peckb.aoc._2022.calendar.day22.CubeFace.Companion.FACE_THREE
import me.peckb.aoc._2022.calendar.day22.CubeFace.Companion.FACE_FOUR
import me.peckb.aoc._2022.calendar.day22.CubeFace.Companion.FACE_FIVE
import me.peckb.aoc._2022.calendar.day22.CubeFace.Companion.FACE_SIX
import me.peckb.aoc._2022.calendar.day22.CubeFace.Companion.downToDown
import me.peckb.aoc._2022.calendar.day22.CubeFace.Companion.downToLeft
import me.peckb.aoc._2022.calendar.day22.CubeFace.Companion.leftToDown
import me.peckb.aoc._2022.calendar.day22.CubeFace.Companion.leftToRight
import me.peckb.aoc._2022.calendar.day22.CubeFace.Companion.rightToLeft
import me.peckb.aoc._2022.calendar.day22.CubeFace.Companion.rightToUp
import me.peckb.aoc._2022.calendar.day22.CubeFace.Companion.upToRight
import me.peckb.aoc._2022.calendar.day22.CubeFace.Companion.upToUp
import me.peckb.aoc._2022.calendar.day22.CubeFace.FaceTransition
import me.peckb.aoc._2022.calendar.day22.Day22.Area.*
import me.peckb.aoc._2022.calendar.day22.Direction.*
import me.peckb.aoc._2022.calendar.day22.Day22.Movement.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalArgumentException

class Day22 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (caves, movements) = loadArea(input)

    val xRanges = mutableMapOf<Int, Pair<Int, Int>>()
    caves.indices.forEach { y ->
      xRanges[y] = caves[y].indexOfFirst { it != VOID } to caves[y].indexOfLast { it != VOID }
    }

    val yRanges = mutableMapOf<Int, Pair<Int, Int>>()
    caves[0].indices.forEach { x ->
      yRanges[x] = caves.indices.first { caves[it][x] != VOID } to caves.indices.last { caves[it][x] != VOID }
    }

    var y = 0
    var x = caves[y].indexOf(EMPTY)
    var direction = RIGHT

    movements.forEach { movement ->
      when (movement) {
        LeftTurn -> direction = direction.turnLeft()
        RightTurn -> direction = direction.turnRight()
        is Walk -> {
          repeat(movement.steps) {
            val dx = direction.dX(x)
            val dy = direction.dY(y)
            val isVoid by lazy { caves[dy][dx] == VOID }

            val (newX, newY) = when (direction) {
              LEFT  -> (xRanges[dy]!!.second to dy).takeIf { dx < 0               || isVoid }
              RIGHT -> (xRanges[dy]!!.first  to dy).takeIf { dx >= caves[dy].size || isVoid }
              UP    -> (dx to yRanges[dx]!!.second).takeIf { dy < 0               || isVoid }
              DOWN  -> (dx to yRanges[dx]!!.first ).takeIf { dy >= caves.size     || isVoid }
            } ?: (dx to dy)

            if (caves[newY][newX] == EMPTY) {
              x = newX
              y = newY
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
            val transition: FaceTransition = when (direction) {
              LEFT  -> moveLeft( caves, x, y, direction, cubeFace)
              RIGHT -> moveRight(caves, x, y, direction, cubeFace)
              UP    -> moveUp(   caves, x, y, direction, cubeFace)
              DOWN  -> moveDown( caves, x, y, direction, cubeFace)
            }
            if (caves[transition.newY][transition.newX] == EMPTY) {
              x = transition.newX
              y = transition.newY
              direction = transition.newDirection
              cubeFace = transition.newFace
            }
          }
        }
      }
    }

    findPassword(x, y, direction)
  }

  private fun moveLeft(caves: List<List<Area>>, x: Int, y: Int, direction: Direction, cubeFace: CubeFace): FaceTransition {
    var transition = FaceTransition(x - 1, y, direction, cubeFace)
    if (transition.newX < 0 || caves[transition.newY][transition.newX] == VOID) {
      when (cubeFace) {
        FACE_ONE   -> transition = leftToRight(y, cubeFace, FACE_FOUR)
        FACE_THREE -> transition = leftToDown( y, cubeFace, FACE_FOUR)
        FACE_FOUR  -> transition = leftToRight(y, cubeFace, FACE_ONE)
        FACE_SIX   -> transition = leftToDown( y, cubeFace, FACE_ONE)
      }
    } else {
      when (cubeFace) {
        FACE_TWO  -> if (transition.newX < cubeFace.minX) transition.newFace = FACE_ONE
        FACE_FIVE -> if (transition.newX < cubeFace.minX) transition.newFace = FACE_FOUR
      }
    }
    return transition
  }

  private fun moveRight(caves: List<List<Area>>, x: Int, y: Int, direction: Direction, cubeFace: CubeFace): FaceTransition {
    var transition = FaceTransition(x + 1, y, direction, cubeFace)
    if (transition.newX >= caves[transition.newY].size || caves[transition.newY][transition.newX] == VOID) {
      when (cubeFace) {
        FACE_TWO   -> transition = rightToLeft(y, cubeFace, FACE_FIVE)
        FACE_THREE -> transition = rightToUp(  y, cubeFace, FACE_TWO)
        FACE_FIVE  -> transition = rightToLeft(y, cubeFace, FACE_TWO)
        FACE_SIX   -> transition = rightToUp(  y, cubeFace, FACE_FIVE)
      }
    } else {
      when (cubeFace) {
        FACE_ONE  -> if (transition.newX > cubeFace.maxX) transition.newFace = FACE_TWO
        FACE_FOUR -> if (transition.newX > cubeFace.maxX) transition.newFace = FACE_FIVE
      }
    }
    return transition
  }

  private fun moveUp(caves: List<List<Area>>, x: Int, y: Int, direction: Direction, cubeFace: CubeFace): FaceTransition {
    var transition = FaceTransition(x, y - 1, direction, cubeFace)
    if (transition.newY < 0 || caves[transition.newY][transition.newX] == VOID) {
      when (cubeFace) {
        FACE_ONE  -> transition = upToRight(x, cubeFace, FACE_SIX)
        FACE_TWO  -> transition = upToUp(x, cubeFace, FACE_SIX)
        FACE_FOUR -> transition = upToRight(x, cubeFace, FACE_THREE)
      }
    } else {
      when (cubeFace) {
        FACE_THREE -> if (transition.newY < cubeFace.minY) transition.newFace = FACE_ONE
        FACE_FIVE  -> if (transition.newY < cubeFace.minY) transition.newFace = FACE_THREE
        FACE_SIX   -> if (transition.newY < cubeFace.minY) transition.newFace = FACE_FOUR
      }
    }
    return transition
  }

  private fun moveDown(caves: List<List<Area>>, x: Int, y: Int, direction: Direction, cubeFace: CubeFace): FaceTransition {
    var transition = FaceTransition(x, y + 1, direction, cubeFace)
    if (transition.newY >= caves.size || caves[transition.newY][transition.newX] == VOID) {
      when (cubeFace) {
        FACE_TWO  -> transition = downToLeft(x, cubeFace, FACE_THREE)
        FACE_FIVE -> transition = downToLeft(x, cubeFace, FACE_SIX)
        FACE_SIX  -> transition = downToDown(x, cubeFace, FACE_TWO)
      }
    } else {
      when (cubeFace) {
        FACE_ONE   -> if (transition.newY > cubeFace.maxY) transition.newFace = FACE_THREE
        FACE_THREE -> if (transition.newY > cubeFace.maxY) transition.newFace = FACE_FIVE
        FACE_FOUR  -> if (transition.newY > cubeFace.maxY) transition.newFace = FACE_SIX
      }
    }
    return transition
  }

  private fun loadArea(input: Sequence<String>): Pair<List<List<Area>>, MutableList<Movement>> {
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

  private fun findPassword(x: Int, y: Int, direction: Direction) = (1000 * (y + 1)) + (4 * (x + 1)) + direction.score

  enum class Area { WALL, EMPTY, VOID }

  sealed class Movement {
    object LeftTurn : Movement()
    object RightTurn : Movement()
    data class Walk(val steps: Int) : Movement()
  }
}
