package me.peckb.aoc._2017.calendar.day19

import me.peckb.aoc._2017.calendar.day19.Day19.Direction.DOWN
import me.peckb.aoc._2017.calendar.day19.Day19.Direction.LEFT
import me.peckb.aoc._2017.calendar.day19.Day19.Direction.RIGHT
import me.peckb.aoc._2017.calendar.day19.Day19.Direction.UP
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day19 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val pipes = input.toList()
    val maxY = pipes.size
    val maxX = pipes.maxOf { it.length }

    val sewer = Array(maxY + 1) { Array(maxX + 1) { ' ' } }
    pipes.forEachIndexed { y, row -> row.forEachIndexed { x, c -> sewer[y][x] = c } }

    val lettersFound = mutableListOf<Char>()

    var y = 0
    var x = sewer[y].indexOf('|')
    var done = false
    var direction = DOWN

    while(!done) {
      when (direction) {
        DOWN -> {
          y++
          when (val c = sewer[y][x]) {
            '|', '-' -> { /* ignore and keep going */ }
            '+' -> {
              val l = sewer[y][x - 1]
              val r = sewer[y][x + 1]
              direction = if (l == ' ') { RIGHT } else if (r == ' ') { LEFT } else {
                throw IllegalStateException("Unknown turn $c ($y, $x)")
              }
            }
            else -> {
              if (c == ' ') {
                done = true
              } else {
                lettersFound.add(c)
              }
            }
          }
        }
        RIGHT -> {
          x++
          when (val c = sewer[y][x]) {
            '|', '-' -> { /* ignore and keep going */ }
            '+' -> {
              val u = sewer[y - 1][x]
              val d = sewer[y + 1][x]
              direction = if (u == ' ') { DOWN } else if (d == ' ') { UP } else {
                throw IllegalStateException("Unknown turn $c ($y, $x)")
              }
            }
            else -> {
              if (c == ' ') {
                done = true
              } else {
                lettersFound.add(c)
              }
            }
          }
        }
        LEFT -> {
          x--
          when (val c = sewer[y][x]) {
            '|', '-' -> { /* ignore and keep going */ }
            '+' -> {
              val u = sewer[y - 1][x]
              val d = sewer[y + 1][x]
              direction = if (u == ' ') { DOWN } else if (d == ' ') { UP } else {
                throw IllegalStateException("Unknown turn $c ($y, $x)")
              }
            }
            else -> {
              if (c == ' ') {
                done = true
              } else {
                lettersFound.add(c)
              }
            }
          }
        }
        UP -> {
          y--
          when (val c = sewer[y][x]) {
            '|', '-' -> { /* ignore and keep going */ }
            '+' -> {
              val l = sewer[y][x - 1]
              val r = sewer[y][x + 1]
              direction = if (l == ' ') { RIGHT } else if (r == ' ') { LEFT } else {
                throw IllegalStateException("Unknown turn $c ($y, $x)")
              }
            }
            else -> {
              if (c == ' ') {
                done = true
              } else {
                lettersFound.add(c)
              }
            }
          }
        }
      }
    }

    lettersFound.joinToString("")
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    -1
  }

  enum class Direction { DOWN, RIGHT, LEFT, UP }
}
