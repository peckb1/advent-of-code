package me.peckb.aoc._2023.calendar.day13

import arrow.core.Tuple4
import arrow.core.firstOrNone
import arrow.core.flatten
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

typealias Pattern = MutableList<MutableList<Char>>

class Day13 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val patterns = findPatterns(input)

    val summaries = patterns.mapIndexed { patternIndex, pattern ->
      val (h1, h2, v1, v2) = createMaps(pattern)

      val horizontalMirror = findMirror(h1, h2, pattern.size - 1).firstOrNull()
      val verticalMirror = findMirror(v1, v2, pattern[0].size - 1).firstOrNull()

      findSummary(horizontalMirror, verticalMirror)
    }

    summaries.sum()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val patterns = findPatterns(input)

    val summaries = patterns.mapIndexed thing@ { patternIndex, pattern ->
      val (h1, h2, v1, v2) = createMaps(pattern)
      val horizontalMirror = findMirror(h1, h2, pattern.size - 1).firstOrNull()
      val verticalMirror = findMirror(v1, v2, pattern[0].size - 1).firstOrNull()
      val originalMirrorInformation = (horizontalMirror to verticalMirror)

      (0 until pattern.size).forEach { row ->
        (0 until pattern[row].size).forEach { col ->
          pattern[row][col] = switch(pattern[row][col])
          val (h3, h4, v3, v4) = createMaps(pattern)

//          println("${v3.map { it.value.size }} $row $col ")

          /*
[0, 1, 12]
[0, 1, 12]
[2, 11]
[3, 10]
[4, 9]
[5, 8]
[6, 7]
[6, 7]
[5, 8]
[4, 9]
[3, 10]
[2, 11]
[0, 1, 12]

           */

//          println()
//            (0 until pattern[0].size).map { i ->
//              v3[v4[i]]
//            }.forEach {
//              println(it)
//            }
//          println()

          if (v3[v4[2]] == listOf(2, 11)) {
            // row = 4
            // col = 2
            -1
          }

          val horizontalMirror2 = findMirror(h3, h4, pattern.size - 1).minus(horizontalMirror)
          val verticalMirror2 = findMirror(v3, v4, pattern[0].size - 1).minus(verticalMirror)

          pattern[row][col] = switch(pattern[row][col])

          if (horizontalMirror2.size >= 2 || verticalMirror2.size >= 2) {
            throw IllegalStateException("We should not have more than one match found (that isn't the original")
          }

          if (horizontalMirror2.isNotEmpty() && verticalMirror2.isNotEmpty()) {
            throw IllegalStateException("We should not find both matches")
          }

          val newMirrorInformation = horizontalMirror2.firstOrNull() to verticalMirror2.firstOrNull()

          if (newMirrorInformation != null to null) {
            -1
          }

          if (originalMirrorInformation != newMirrorInformation && (newMirrorInformation != null to null)) {
            return@thing findSummary(newMirrorInformation.first, newMirrorInformation.second)
          }
        }
      }

      throw IllegalStateException("We should have found something for $patternIndex")
    }

    summaries.sum()
  }

  private fun switch(c: Char): Char {
    return when (c) {
      '.' -> '#'
      '#' -> '.'
      else -> throw IllegalArgumentException("Invalid mirror mark $c")
    }
  }

  private fun createMaps(pattern: List<List<Char>>): Tuple4<
    MutableMap<List<Char>, List<Int>>,
    MutableMap<Int, List<Char>>,
    MutableMap<List<Char>, List<Int>>,
    MutableMap<Int, List<Char>>
    > {
    val h1 = mutableMapOf<List<Char>, List<Int>>()
    val h2 = mutableMapOf<Int, List<Char>>()

    val v1 = mutableMapOf<List<Char>, List<Int>>()
    val v2 = mutableMapOf<Int, List<Char>>()

    pattern.forEachIndexed { index, s ->
      h1.merge(s, listOf(index)) { a, b -> a + b }
      h2[index] = s
    }
    (0 until pattern[0].size).forEach { index ->
      val s = pattern.map { it[index] }//.joinToString("")
      v1.merge(s, listOf(index)) { a, b -> a + b }
      v2[index] = s
    }

    return Tuple4(h1, h2, v1, v2)
  }

  private fun findPatterns(input: Sequence<String>): List<Pattern> {
    val patterns = mutableListOf<Pattern>()

    val nextPattern = mutableListOf<MutableList<Char>>()
    input.forEach { s ->
      if (s.isNotEmpty()) {
        nextPattern.add(s.toMutableList())
      } else {
        patterns.add(nextPattern.toMutableList())
        nextPattern.clear()
      }
    }
    patterns.add(nextPattern.toMutableList())

    return patterns
  }

  private fun findMirror(
    outerMap: MutableMap<List<Char>, List<Int>>,
    innerMap: MutableMap<Int, List<Char>>,
    highIndex: Int
  ): Set<Pair<Int, Int>> {
    return (0..highIndex).mapNotNull { horizontalIndex ->
      val matches = outerMap[innerMap[horizontalIndex]]!!
      val lowIndex = 0

      if (matches.size >= 2 && (matches.contains(lowIndex) || matches.contains(highIndex))) {
        val zeroIndexMatch = if (matches.contains(lowIndex)) {
          matches.minus(lowIndex).firstOrNull { upperEdge ->
            (lowIndex..upperEdge).withIndex().all { (i, index) ->
              val expectedPair = listOf(lowIndex + i, upperEdge - i)
              outerMap[innerMap[index]]!!.containsAll(expectedPair)
            }
          }
        } else {
          null
        }?.takeIf {
          (highIndex - it) % 2 == 1
        }

        val highIndexMatch = if (matches.contains(highIndex)) {
          matches.minus(highIndex).firstOrNull { lowerEdge ->
            (lowerEdge..highIndex).withIndex().all { (i, index) ->
              val expectedPair = listOf(lowerEdge + i, highIndex - i)
              outerMap[innerMap[index]]!!.containsAll(expectedPair)
            }
          }
        } else {
          null
        }?.takeIf {
          (highIndex - it) % 2 == 1
        }

        listOfNotNull(
          zeroIndexMatch?.let { 0 to it },
          highIndexMatch?.let { it to highIndex }
        )

//        zeroIndexMatch?.let { 0 to it } ?: highIndexMatch?.let { it to highIndex }
      } else {
        null
      }
    }.flatten().toSet()
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
