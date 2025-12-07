package me.peckb.aoc._2025.calendar.day07

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.text.indexOf
import kotlin.text.toCharArray

class Day07 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val beamIndices = mutableSetOf<Int>()
    val manifold = mutableListOf<MutableList<Char>>()

    input.forEachIndexed { index, line ->
      if (index == 0) { beamIndices.add(line.indexOf('S')) }
      manifold.add(line.toCharArray().toMutableList())
    }

    var depth = 1
    var splits = 0
    while (depth < manifold.size) {
      val line = manifold[depth]

      beamIndices.toList().also { beamIndices.clear() }.forEach { beamIndex ->
        if (line[beamIndex] == '^') {
          splits++
          beamIndices.add(beamIndex - 1)
          beamIndices.add(beamIndex + 1)
        } else {
          beamIndices.add(beamIndex)
        }
      }

      depth++
    }

    splits
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val beams = mutableMapOf<Location, Long>().withDefault { 0 }
    val manifold = mutableListOf<MutableList<Char>>()

    input.forEachIndexed { index, line ->
      if (index == 0) { beams[Location(0, line.indexOf('S'))] = 1 }
      manifold.add(line.toCharArray().toMutableList())
    }

    var depth = 1
    while (depth < manifold.size) {
      val line = manifold[depth]

      beams.toList().also { beams.clear() }.forEach { (location, count) ->
        if (location.depth != depth - 1) {
          throw IllegalStateException("We should only be tracking the last row.")
        }

        // if we need to split then the indices of our location need to shift and split
        // otherwise we just stay at the same index we're at
        val indexDeltas = if (line[location.index] == '^') listOf(-1, 1) else listOf(0)

        indexDeltas.forEach { delta ->
          Location(depth, location.index + delta).let { loc ->
            beams[loc] = beams.getValue(loc) + count
          }
        }
      }

      depth++
    }

    beams.values.sum()
  }
}

data class Location(val depth: Int, val index: Int) {
  override fun toString(): String {
    return "($depth,$index)"
  }
}
