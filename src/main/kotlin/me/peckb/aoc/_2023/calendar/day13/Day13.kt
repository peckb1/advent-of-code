package me.peckb.aoc._2023.calendar.day13

import arrow.core.Tuple4
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

typealias Pattern = List<String>

class Day13 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val patterns = findPatterns(input)

    val summaries = patterns.mapIndexed { patternIndex, pattern ->
      val (h1, h2, v1, v2) = createMaps(pattern)

      val horizontalMirror = findMirror(h1, h2,pattern.size - 1 ).firstOrNull()
      val verticalMirror = findMirror(v1, v2, pattern[0].length - 1 ).firstOrNull()

      findSummary(horizontalMirror, verticalMirror)
    }

    summaries.sum()
  }

  private fun createMaps(pattern: List<String>): Tuple4<MutableMap<String, List<Int>>, MutableMap<Int, String>, MutableMap<String, List<Int>>, MutableMap<Int, String>> {
    val h1 = mutableMapOf<String, List<Int>>()
    val h2 = mutableMapOf<Int, String>()

    val v1 = mutableMapOf<String, List<Int>>()
    val v2 = mutableMapOf<Int, String>()

    pattern.forEachIndexed { index, s ->
      h1.merge(s, listOf(index)) { a, b -> a + b }
      h2[index] = s
    }
    (0 until pattern[0].length).forEach { index ->
      val s = pattern.map { it[index] }.joinToString("")
      v1.merge(s, listOf(index)) { a, b -> a + b }
      v2[index] = s
    }

    return Tuple4(h1, h2, v1, v2)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val patterns = findPatterns(input)

    val summaries = patterns.mapIndexed { patternIndex, pattern ->

    }
  }

  private fun findPatterns(input: Sequence<String>): List<Pattern> {
    val patterns = mutableListOf<Pattern>()

    val nextPattern = mutableListOf<String>()
    input.forEach { s ->
      if(s.isNotEmpty()) {
        nextPattern.add(s)
      } else {
        patterns.add(nextPattern.toList())
        nextPattern.clear()
      }
    }
    patterns.add(nextPattern.toList())

    return patterns
  }

  private fun findMirror(
    outerMap: MutableMap<String, List<Int>>,
    innerMap: MutableMap<Int, String>,
    highIndex: Int
  ) : List<Pair<Int, Int>> {
    return (0 .. highIndex).mapNotNull { horizontalIndex ->
      val matches = outerMap[innerMap[horizontalIndex]]!!
      val lowIndex = 0

      if (matches.size >= 2 && (matches.contains(lowIndex) || matches.contains(highIndex))) {
        val zeroIndexMatch = if (matches.contains(lowIndex)) {
          matches.minus(lowIndex).firstOrNull { upperEdge ->
            (lowIndex .. upperEdge).withIndex().all { (i, index) ->
              val expectedPair = listOf(lowIndex + i, upperEdge - i)
              outerMap[innerMap[index]]!!.containsAll(expectedPair)
            }
          }
        } else {
          null
        }?.takeIf {
          (highIndex - it) %2 == 1
        }

        val highIndexMatch = if (matches.contains(highIndex)) {
          matches.minus(highIndex).firstOrNull { lowerEdge ->
            (lowerEdge .. highIndex).withIndex().all { (i, index) ->
              val expectedPair = listOf(lowerEdge + i, highIndex - i)
              outerMap[innerMap[index]]!!.containsAll(expectedPair)
            }
          }
        } else {
          null
        }?.takeIf {
          (highIndex - it) %2 == 1
        }

        zeroIndexMatch?.let { 0 to it } ?: highIndexMatch?.let { it to highIndex}
      } else {
        null
      }
    }
  }

  private fun findSummary(horizontalMirror: Pair<Int, Int>?, verticalMirror: Pair<Int, Int>?): Int {
    return if (horizontalMirror != null) {
      val (lowEnd, highEnd) = horizontalMirror
      val mid = (highEnd - lowEnd + 1)
      100 * ((mid / 2) + lowEnd)
    } else if (verticalMirror != null) {
      val (lowEnd, highEnd) = verticalMirror
      val mid = (highEnd - lowEnd + 1)
      (mid / 2) + lowEnd
    } else {
      throw IllegalArgumentException("One mirror must be non-null")
    }
  }
}
