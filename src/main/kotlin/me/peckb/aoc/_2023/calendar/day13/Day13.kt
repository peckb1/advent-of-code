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

      val verticalEdgeMatch = (0 until pattern[0].length).firstOrNull { verticalIndex ->
        val mirrorDataOfIndex = verticalItems1[verticalItems2[verticalIndex]] ?: emptyList()
        (
          mirrorDataOfIndex.size >= 2 &&
          (mirrorDataOfIndex.contains(0) || mirrorDataOfIndex.contains(pattern[0].length - 1))
        )
      }

      val verticalMirror = verticalEdgeMatch?.let { edgeIndex ->
        val pair = verticalItems1[verticalItems2[edgeIndex]]!!
        val minEdge = max(0, pair.minOf { it })
        val maxEdge = min(pattern[0].length - 1, pair.maxOf { it })
        (minEdge .. maxEdge).withIndex().all { (i, index) ->
          val expectedPair = listOf(minEdge + i, maxEdge - i)
          verticalItems1[verticalItems2[index]]!!.containsAll(expectedPair)
        }
      }

      val horizontalEdgeMatch = (0 until pattern.size).firstOrNull { horizontalIndex ->
        val mirrorDataOfIndex = horizontalItems1[horizontalItems2[horizontalIndex]] ?: emptyList()

        (
          mirrorDataOfIndex.size >= 2 &&
          (mirrorDataOfIndex.contains(0) || mirrorDataOfIndex.contains(pattern.size - 1))
        )
      }

      val horizontalMirror = horizontalEdgeMatch?.let { edgeIndex ->
        val pair = horizontalItems1[horizontalItems2[edgeIndex]]!!
        val minEdge = max(0, pair.minOf { it })
        val maxEdge = min(pattern.size - 1, pair.maxOf { it })
        (minEdge .. maxEdge).withIndex().all { (i, index) ->
          val expectedPair = listOf(minEdge + i, maxEdge - i)
          horizontalItems1[horizontalItems2[index]]!!.containsAll(expectedPair)
        }
      }

      if (horizontalMirror == true) {
        100 * ((pattern.size - horizontalEdgeMatch) / 2 + horizontalEdgeMatch)
      } else if (verticalMirror == true) {
        ((pattern[0].length - verticalEdgeMatch) / 2 + verticalEdgeMatch)
      } else {
        throw IllegalArgumentException("No Mirror Found")
      }
    }

    -1

    summaries.sum()
  }

  fun partTwo(filename: String) =
      generatorFactory.forFile(filename).readAs(::day13) { input ->
    -1
  }

  private fun day13(line: String) = line
}
