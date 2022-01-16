package me.peckb.aoc._2018.calendar.day03

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day03 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::pattern) { input ->
    val fabric = Array(FABRIC_SIZE) { Array(FABRIC_SIZE) { 0 } }

    input.forEach { pattern ->
      (pattern.startX..pattern.endX).forEach { x ->
        (pattern.startY..pattern.endY).forEach { y ->
          fabric[y][x]++
        }
      }
    }

    fabric.sumOf { row -> row.count { it >= 2 } }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::pattern) { input ->
    val patterns = input.toList()

    val patternWithNoOverlaps = patterns.withIndex().find { indexedPattern ->
      !(patterns.indices).any { patternIndex ->
        if (patternIndex == indexedPattern.index) {
          false
        } else {
          val overlap = indexedPattern.value.overlaps(patterns[patternIndex])
          overlap
        }
      }
    }

    patternWithNoOverlaps?.value?.id
  }

  private fun pattern(line: String): Pattern {
    // #1 @ 1,3: 4x4
    val parts = line.split(" ")
    val id = parts[0].drop(1).toInt()
    val startY = parts[2].substringAfter(",").dropLast(1).toInt()
    val startX = parts[2].substringBefore(",").toInt()
    val endY = startY + parts[3].substringAfter("x").toInt() - 1
    val endX = startX + parts[3].substringBefore("x").toInt() - 1

    return Pattern(id, startY, startX, endY, endX)
  }

  data class Pattern(val id: Int, val startY: Int, val startX: Int, val endY: Int, val endX: Int) {
    fun overlaps(other: Pattern): Boolean {
      return startX <= other.endX && endX >= other.startX && startY <= other.endY && endY >= other.startY
    }
  }

  companion object {
    const val FABRIC_SIZE = 1000
  }
}
