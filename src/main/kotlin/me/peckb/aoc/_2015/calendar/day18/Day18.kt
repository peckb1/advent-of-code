package me.peckb.aoc._2015.calendar.day18

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day18 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val roof = input.map { row ->
      row.toCharArray()
    }.toList().toTypedArray()

    repeat(TURNS) {
      val indicesToToggle = mutableListOf<Pair<Int, Int>>()
      roof.indices.forEach { y ->
        roof.indices.forEach { x ->
          val ul = roof.findPoint(y - 1, x - 1)
          val u = roof.findPoint(y - 1, x)
          val ur = roof.findPoint(y - 1, x + 1)

          val l = roof.findPoint(y, x - 1)
          val me = roof.findPoint(y, x)
          val r = roof.findPoint(y, x + 1)

          val ll = roof.findPoint(y + 1, x - 1)
          val d = roof.findPoint(y + 1, x)
          val lr = roof.findPoint(y + 1, x + 1)

          val onSpaces = listOf(ul, u, ur, l, r, ll, d, lr).count { it == '#' }

          if (me == '#') {
            if (onSpaces != 2 && onSpaces != 3) {
              indicesToToggle.add(y to x)
            }
          } else {
            if (onSpaces == 3) {
              indicesToToggle.add(y to x)
            }
          }
        }
      }
      indicesToToggle.forEach {
        if (roof[it.first][it.second] == '#') {
          roof[it.first][it.second] = '.'
        } else {
          roof[it.first][it.second] = '#'
        }
      }
    }

    roof.sumOf { row -> row.count { it == '#' } }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val roof = input.map { row ->
      row.toCharArray()
    }.toList().toTypedArray()

    repeat(TURNS) {
      roof[0][0] = '#'
      roof[0][roof.size - 1] = '#'
      roof[roof.size - 1][0] = '#'
      roof[roof.size - 1][roof.size - 1] = '#'

      val indicesToToggle = mutableListOf<Pair<Int, Int>>()
      roof.indices.forEach { y ->
        roof.indices.forEach { x ->
          val ul = roof.findPoint(y - 1, x - 1)
          val u = roof.findPoint(y - 1, x)
          val ur = roof.findPoint(y - 1, x + 1)

          val l = roof.findPoint(y, x - 1)
          val me = roof.findPoint(y, x)
          val r = roof.findPoint(y, x + 1)

          val ll = roof.findPoint(y + 1, x - 1)
          val d = roof.findPoint(y + 1, x)
          val lr = roof.findPoint(y + 1, x + 1)

          val onSpaces = listOf(ul, u, ur, l, r, ll, d, lr).count { it == '#' }

          if (me == '#') {
            if (onSpaces != 2 && onSpaces != 3) {
              indicesToToggle.add(y to x)
            }
          } else {
            if (onSpaces == 3) {
              indicesToToggle.add(y to x)
            }
          }
        }
      }
      indicesToToggle.forEach {
        if (roof[it.first][it.second] == '#') {
          roof[it.first][it.second] = '.'
        } else {
          roof[it.first][it.second] = '#'
        }
      }
    }

    roof[0][0] = '#'
    roof[0][roof.size - 1] = '#'
    roof[roof.size - 1][0] = '#'
    roof[roof.size - 1][roof.size - 1] = '#'

    roof.sumOf { row -> row.count { it == '#' } }
  }

  companion object {
    const val TURNS = 100
  }

}

private fun Array<CharArray>.findPoint(y: Int, x: Int): Char {
  return this.getOrNull(y)?.getOrNull(x) ?: '.'
}
