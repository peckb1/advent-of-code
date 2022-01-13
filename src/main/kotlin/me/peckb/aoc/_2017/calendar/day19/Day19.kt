package me.peckb.aoc._2017.calendar.day19

import me.peckb.aoc._2017.calendar.day19.Day19.Direction.DOWN
import me.peckb.aoc._2017.calendar.day19.Day19.Direction.LEFT
import me.peckb.aoc._2017.calendar.day19.Day19.Direction.LOST
import me.peckb.aoc._2017.calendar.day19.Day19.Direction.RIGHT
import me.peckb.aoc._2017.calendar.day19.Day19.Direction.UP
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day19 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val sewer = createSewer(input)
    followPipes(sewer).first
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val sewer = createSewer(input)
    followPipes(sewer).second
  }

  private fun createSewer(input: Sequence<String>): Array<Array<Char>> {
    val pipes = input.toList()
    val maxY = pipes.size
    val maxX = pipes.maxOf { it.length }

    val sewer = Array(maxY + 1) { Array(maxX + 1) { ' ' } }
    pipes.forEachIndexed { y, row -> row.forEachIndexed { x, c -> sewer[y][x] = c } }

    return sewer
  }

  private fun followPipes(sewer: Array<Array<Char>>): Pair<String, Int> {
    val lettersFound = mutableListOf<Char>()

    var y = 0
    var x = sewer[y].indexOf('|')
    var done = false
    var direction = DOWN

    var moves = 0

    fun checkIfDone(c: Char) {
      if (c == ' ') {
        done = true
      } else {
        lettersFound.add(c)
      }
    }

    while(!done) {
      moves++
      when (direction) {
        DOWN, UP -> {
          if (direction == DOWN) y++ else y--
          when (val c = sewer[y][x]) {
            '|', '-' -> { /* ignore and keep going */ }
            '+' -> {
              val l = sewer[y][x - 1]
              val r = sewer[y][x + 1]
              direction = if (l == ' ') { RIGHT } else if (r == ' ') { LEFT } else { LOST }
            }
            else -> checkIfDone(c)
          }
        }
        RIGHT, LEFT -> {
          if (direction == RIGHT) x++ else x--
          when (val c = sewer[y][x]) {
            '|', '-' -> { /* ignore and keep going */ }
            '+' -> {
              val u = sewer[y - 1][x]
              val d = sewer[y + 1][x]
              direction = if (u == ' ') { DOWN } else if (d == ' ') { UP } else { LOST }
            }
            else -> checkIfDone(c)
          }
        }
        LOST -> throw IllegalStateException("Help, we got lost!")
      }
    }

    return lettersFound.joinToString("") to moves
  }

  enum class Direction { DOWN, RIGHT, LEFT, UP, LOST }
}
