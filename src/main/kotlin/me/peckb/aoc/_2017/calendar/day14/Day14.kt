package me.peckb.aoc._2017.calendar.day14

import me.peckb.aoc._2017.calendar.day10.Day10
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.util.ArrayDeque

class Day14 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
  private val day10: Day10
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val grid = createGrid(input)
    grid.sumOf { row -> row.count { it == '1' } }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val grid = createGrid(input).map { it.toCharArray() }
    var groups = 0

    grid.indices.forEach { y ->
      grid.indices.forEach { x ->
        if (grid[y][x] == '1') {
          groups++
          grid.replaceGroup(y, x)
        }
      }
    }

    groups
  }

  private fun createGrid(input: String): List<String> {
    return (0 until 128).map { n ->
      val key = "$input-$n"
      val lengths = key.map { it.code }.plus(listOf(17, 31, 73, 47, 23))
      val data = day10.runInput(lengths, 64)
      data.chunked(16)
        .map { it.reduce { acc, next -> acc.xor(next) } }
        .joinToString("") { i ->
          val hex = i.toString(16).let { if (it.length < 2) "0$it" else it }
          "${CONVERSION[hex[0]]!!}${CONVERSION[hex[1]]!!}"
        }

    }
  }

  private fun List<CharArray>.replaceGroup(y: Int, x: Int) {
    val toCheck = ArrayDeque<Pair<Int, Int>>()
    toCheck.push(y to x)
    while (toCheck.isNotEmpty()) {
      val (yy, xx) = toCheck.pop()
      this[yy][xx] = '0'

      val u = find(yy - 1, xx, '0')
      val r = find(yy, xx + 1, '0')
      val d = find(yy + 1, xx, '0')
      val l = find(yy, xx - 1, '0')

      if (u == '1') toCheck.push(yy - 1 to xx)
      if (r == '1') toCheck.push(yy to xx + 1)
      if (d == '1') toCheck.push(yy + 1 to xx)
      if (l == '1') toCheck.push(yy to xx - 1)
    }
  }

  private fun List<CharArray>.find(y: Int, x: Int, default: Char): Char {
    return if (x in 0 until size && y in (0 until size)) {
      this[y][x]
    } else {
      default
    }
  }

  companion object {
    val CONVERSION = mapOf(
      '0' to "0000",
      '1' to "0001",
      '2' to "0010",
      '3' to "0011",
      '4' to "0100",
      '5' to "0101",
      '6' to "0110",
      '7' to "0111",
      '8' to "1000",
      '9' to "1001",
      'a' to "1010",
      'b' to "1011",
      'c' to "1100",
      'd' to "1101",
      'e' to "1110",
      'f' to "1111"
    )
  }
}
