package me.peckb.aoc._2024.calendar.day21

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.util.LinkedList
import kotlin.math.sign

class Day21 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    solve(input, indirections = 3)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    solve(input, indirections = 26)
  }

  private fun solve(input: Sequence<String>, indirections: Int) : Long {
    return input.sumOf { code ->
      val length = getLength(NUMERIC_PAD, code, indirections)
      val size = code.take(3).toLong()

      length * size
    }
  }

  private fun getLength(keyPad: Map<Char, Pair<Int, Int>>, code: String, indirections: Int): Long {
    val lengthKey = LengthKey(keyPad.size, code, indirections)

    if (LENGTH_CACHE.containsKey(lengthKey)) {
      return LENGTH_CACHE[lengthKey]!!
    }

    if (indirections == 0) {
      return code.length.toLong().also { LENGTH_CACHE[lengthKey] = it }
    }

    val minLength = "A${code}".toList()
      .windowed(2)
      .sumOf { (s, e) ->
        paths(keyPad, s, e).minOf { getLength(DIRECTION_PAD, "${it}A", indirections - 1) }
      }

    return minLength.also { LENGTH_CACHE[lengthKey] = it }
  }

  private fun paths(keyPad: Map<Char, Pair<Int, Int>>, start: Char, end: Char): List<String> {
    val pathKey = PathKey(keyPad.size, start, end)

    if (PATHS_CACHE.containsKey(pathKey)) {
      return PATHS_CACHE[pathKey]!!
    }

    val paths = mutableListOf<String>()
    val partialPaths = LinkedList<Pair<Pair<Int, Int>, String>>().apply {
      add(keyPad[start]!! to "")
    }

    pathGeneration@ while(partialPaths.isNotEmpty()) {
      val (position, path) = partialPaths.poll()

      val target = keyPad[end]!!
      if (position == target) {
        paths.add(path)
        continue@pathGeneration
      }

      val yDelta = target.second - position.second
      if (yDelta != 0) {
        val newPoint = position.first to position.second + yDelta.sign
        if (newPoint in keyPad.values) {
          val dir = if (yDelta > 0) { ">" } else { "<" }
          partialPaths.add(newPoint to "$path$dir")
        }
      }

      val xDelta = target.first - position.first
      if (xDelta != 0) {
        val newPoint = position.first + xDelta.sign to position.second
        if (newPoint in keyPad.values) {
          val dir = if (xDelta > 0) { "v" } else { "^" }
          partialPaths.add(newPoint to "$path$dir")
        }
      }
    }

    return paths.also { PATHS_CACHE[pathKey] = it }
  }

  companion object {
    val NUMERIC_PAD = mapOf(
      '7' to (0 to 0), '8' to (0 to 1), '9' to (0 to 2),
      '4' to (1 to 0), '5' to (1 to 1), '6' to (1 to 2),
      '1' to (2 to 0), '2' to (2 to 1), '3' to (2 to 2),
                       '0' to (3 to 1), 'A' to (3 to 2),
    )

    val DIRECTION_PAD = mapOf(
                       '^' to (0 to 1), 'A' to (0 to 2),
      '<' to (1 to 0), 'v' to (1 to 1), '>' to (1 to 2)
    )

    val LENGTH_CACHE = mutableMapOf<LengthKey, Long>()

    val PATHS_CACHE = mutableMapOf<PathKey, List<String>>()
  }
}

data class LengthKey(val padIndicator: Int, val pattern: String, val robots: Int)

data class PathKey(val padIndicator: Int, val start: Char, val end: Char)
