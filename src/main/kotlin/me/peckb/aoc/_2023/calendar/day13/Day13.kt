package me.peckb.aoc._2023.calendar.day13

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max
import kotlin.math.min

typealias Pattern = List<String>

class Day13 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::day13) { input ->
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

    val summaries = patterns.mapIndexed { patternIndex, pattern ->
      val horizontalItems1 = mutableMapOf<String, List<Int>>()
      val horizontalItems2 = mutableMapOf<Int, String>()

      val verticalItems1 = mutableMapOf<String, List<Int>>()
      val verticalItems2 = mutableMapOf<Int, String>()

      pattern.forEachIndexed { index, s ->
        horizontalItems1.merge(s, listOf(index)) { a, b -> a + b }
        horizontalItems2[index] = s
      }
      (0 until pattern[0].length).forEach { index ->
        val s = pattern.map { it[index] }.joinToString("")
        verticalItems1.merge(s, listOf(index)) { a, b -> a + b }
        verticalItems2[index] = s
      }

      val horizontalMirror = (0 until pattern.size).firstNotNullOfOrNull { horizontalIndex ->
        val matches = horizontalItems1[horizontalItems2[horizontalIndex]]!!
        val lowIndex = 0
        val highIndex = pattern.size - 1

        if (matches.size >= 2 && (matches.contains(lowIndex) || matches.contains(highIndex))) {
          val zeroIndexMatch = if (matches.contains(lowIndex)) {
            matches.minus(lowIndex).firstOrNull { upperEdge ->
              (lowIndex .. upperEdge).withIndex().all { (i, index) ->
                val expectedPair = listOf(lowIndex + i, upperEdge - i)
                horizontalItems1[horizontalItems2[index]]!!.containsAll(expectedPair)
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
                horizontalItems1[horizontalItems2[index]]!!.containsAll(expectedPair)
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

      val verticalMirror = (0 until pattern[0].length).firstNotNullOfOrNull { verticalIndex ->
        val lowIndex = 0
        val highIndex = pattern[0].length - 1

        val matches = verticalItems1[verticalItems2[verticalIndex]]!!
        if (matches.size >= 2 && (matches.contains(lowIndex) || matches.contains(highIndex))) {
          val zeroIndexMatch = if (matches.contains(lowIndex)) {
            matches.minus(lowIndex).firstOrNull { upperEdge ->
              (lowIndex .. upperEdge).withIndex().all { (i, index) ->
                val expectedPair = listOf(0 + i, upperEdge - i)
                verticalItems1[verticalItems2[index]]!!.containsAll(expectedPair)
              }
            }
          } else {
            null
          }?.takeIf {
            (highIndex - it) %2 == 1
          }

          val highIndexMatch = if (matches.contains(highIndex)) {
            matches.minus(highIndex).firstOrNull { lowerEdge ->
              (lowerEdge..highIndex).withIndex().all { (i, index) ->
                val expectedPair = listOf(lowerEdge + i, highIndex - i)
                verticalItems1[verticalItems2[index]]!!.containsAll(expectedPair)
              }
            }?.takeIf {
              (highIndex - it) %2 == 1
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

      if (horizontalMirror != null) {
        val (lowEnd, highEnd) = horizontalMirror
        val mid = (highEnd - lowEnd + 1)
        100 * ((mid / 2) + lowEnd)
      } else if (verticalMirror != null) {
        val (lowEnd, highEnd) = verticalMirror
        val mid = (highEnd - lowEnd + 1)
        (mid / 2) + lowEnd
      } else {
        throw IllegalArgumentException("No Mirror Found for index $patternIndex + $pattern")
      }
    }

    summaries.sum()
  }

  fun partTwo(filename: String) =
      generatorFactory.forFile(filename).readAs(::day13) { input ->
    -1
  }

  private fun day13(line: String) = line
}
