package me.peckb.aoc._2020.calendar.day03

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day03 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val area = input.toList()

    countTreesHit(area, 3, 1)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val area = input.toList()

    val a = countTreesHit(area, 1, 1)
    val b = countTreesHit(area, 3, 1)
    val c = countTreesHit(area, 5, 1)
    val d = countTreesHit(area, 7, 1)
    val e = countTreesHit(area, 1, 2)

    a.toLong() * b * c * d * e
  }

  private fun countTreesHit(area: List<String>, deltaX: Int, deltaY: Int): Int {
    var x = 0
    var y = 0
    var treesHit = 0

    while(y < area.size) {
      if (area[y][x] == '#') treesHit++

      x += deltaX
      x %= area[y].length
      y += deltaY
    }
    return treesHit
  }
}
