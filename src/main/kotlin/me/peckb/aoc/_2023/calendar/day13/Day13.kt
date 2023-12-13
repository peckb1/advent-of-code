package me.peckb.aoc._2023.calendar.day13

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

typealias Pattern = MutableList<MutableList<Char>>

class Day13 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    findPatterns(input)
      .map { findMirrors(it) }
      .sumOf { findSummary(it.horizontalMirrors.firstOrNull(), it.verticalMirrors.firstOrNull()) }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val patterns = findPatterns(input)

    val summaries = patterns.mapIndexed summaryLoop@ { patternIndex, pattern ->
      val originalMirrorData = findMirrors(pattern)
      val originalHorizontal = originalMirrorData.horizontalMirrors.firstOrNull()
      val originalVertical = originalMirrorData.verticalMirrors.firstOrNull()

      (0 until pattern.size).forEach { row ->
        (0 until pattern[row].size).forEach { col ->
          // swap
          pattern[row][col] = switch(pattern[row][col])

          val newMirrorData = findMirrors(pattern)
          val newHorizontal = newMirrorData.horizontalMirrors.minus(originalHorizontal).firstOrNull()
          val newVertical = newMirrorData.verticalMirrors.minus(originalVertical).firstOrNull()

          // swap back
          pattern[row][col] = switch(pattern[row][col])

          val originalPair = originalHorizontal to originalVertical
          val newPair = newHorizontal to newVertical
          if ((newPair != null to null) && originalPair != newPair) {
            return@summaryLoop findSummary(newPair.first, newPair.second)
          }
        }
      }

      throw IllegalStateException("We should have found a mirror for $patternIndex")
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

  private fun findMirrors(pattern: Pattern): MirrorData {
    val mapData = createMaps(pattern)
    val horizontalMirror = findMirror(mapData.rowToIndices, mapData.indexToRow, pattern.size - 1)
    val verticalMirror   = findMirror(mapData.colToIndices, mapData.indexToCol, pattern[0].size - 1)

    return MirrorData(horizontalMirror, verticalMirror)
  }

  private fun createMaps(pattern: List<List<Char>>): MirrorMaps {
    val rowToIndices = mutableMapOf<List<Char>, List<Int>>()
    val indexToRow = mutableMapOf<Int, List<Char>>()
    pattern.forEachIndexed { index, s ->
      rowToIndices.merge(s, listOf(index)) { a, b -> a + b }
      indexToRow[index] = s
    }

    val colToIndices = mutableMapOf<List<Char>, List<Int>>()
    val indexToCol = mutableMapOf<Int, List<Char>>()
    (0 until pattern[0].size).forEach { index ->
      val s = pattern.map { it[index] }
      colToIndices.merge(s, listOf(index)) { a, b -> a + b }
      indexToCol[index] = s
    }

    return MirrorMaps(rowToIndices, indexToRow, colToIndices, indexToCol)
  }

  private fun findMirror(
    dataToIndices: MutableMap<List<Char>, List<Int>>,
    indexToData: MutableMap<Int, List<Char>>,
    highIndex: Int,
    lowIndex: Int = 0
  ): Set<Mirror> {
    return (0..highIndex).flatMap { horizontalIndex ->
      val matches = dataToIndices[indexToData[horizontalIndex]]!!
      val foundPossibleMirror = matches.size >= 2

      val zeroIndexMatch = if (foundPossibleMirror && matches.contains(lowIndex)) {
        matches.minus(lowIndex).firstOrNull { upperEdge ->
          (lowIndex..upperEdge).withIndex().all { (i, index) ->
            val expectedPair = listOf(lowIndex + i, upperEdge - i)
            dataToIndices[indexToData[index]]!!.containsAll(expectedPair)
          }
        }
      } else null

      val highIndexMatch = if (foundPossibleMirror && matches.contains(highIndex)) {
        matches.minus(highIndex).firstOrNull { lowerEdge ->
          (lowerEdge..highIndex).withIndex().all { (i, index) ->
            val expectedPair = listOf(lowerEdge + i, highIndex - i)
            dataToIndices[indexToData[index]]!!.containsAll(expectedPair)
          }
        }
      } else null

      // we also only care about perfect mirrors, so clear those out
      listOfNotNull(
        zeroIndexMatch?.takeIf { (highIndex - it) % 2 == 1 }?.let { Mirror(lowIndex, it) },
        highIndexMatch?.takeIf { (highIndex - it) % 2 == 1 }?.let { Mirror(it, highIndex) }
      )
    }.toSet()
  }

  private fun findSummary(horizontalMirror: Mirror?, verticalMirror: Mirror?): Int {
    return when {
      horizontalMirror != null -> 100 * generalSummary(horizontalMirror)
      verticalMirror   != null ->       generalSummary(verticalMirror)
      else -> throw IllegalArgumentException("One mirror must be non-null")
    }
  }

  private fun generalSummary(mirror: Mirror): Int {
    val (lowEnd, highEnd) = mirror
    val mid = (highEnd - lowEnd + 1)
    return (mid / 2) + lowEnd
  }

  data class MirrorMaps(
    val rowToIndices: MutableMap<List<Char>, List<Int>>,
    val indexToRow: MutableMap<Int, List<Char>>,
    val colToIndices: MutableMap<List<Char>, List<Int>>,
    val indexToCol: MutableMap<Int, List<Char>>,
  )

  data class MirrorData(val horizontalMirrors: Set<Mirror>, val verticalMirrors: Set<Mirror>)

  data class Mirror(val low: Int, val high: Int)
}
