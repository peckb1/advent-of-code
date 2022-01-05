package me.peckb.aoc._2016.calendar.day03

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day03 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::rowTriangle) { input ->
    input.count { it != null }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val triangles = mutableListOf<Triangle>()

    input.chunked(3).forEach { threeRows ->
      val threeByThree = threeRows.map { line -> line.chunked(5).map { it.trim().toInt() } }
      val (a1, b1, c1) = listOf(threeByThree[0][0], threeByThree[1][0], threeByThree[2][0]).sorted()
      val (a2, b2, c2) = listOf(threeByThree[0][1], threeByThree[1][1], threeByThree[2][1]).sorted()
      val (a3, b3, c3) = listOf(threeByThree[0][2], threeByThree[1][2], threeByThree[2][2]).sorted()

      if (a1 + b1 > c1) triangles.add(Triangle(a1, b1, c1))
      if (a2 + b2 > c2) triangles.add(Triangle(a2, b2, c2))
      if (a3 + b3 > c3) triangles.add(Triangle(a3, b3, c3))
    }

    triangles.count()
  }

  private fun rowTriangle(line: String): Triangle? {
    val (a, b, c) = line.chunked(5).map { it.trim().toInt() }.sorted()
    return if (a + b > c) Triangle(a, b, c) else null
  }

  data class Triangle(val a: Int, val b: Int, val c: Int)
}
