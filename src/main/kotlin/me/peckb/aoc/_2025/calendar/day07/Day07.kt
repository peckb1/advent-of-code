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
        if (location.depth != depth - 1) { throw IllegalStateException("We should only be tracking the last row.") }

        if (line[location.index] == '^') {
          val left = Location(depth, location.index - 1)
          val right = Location(depth, location.index + 1)

          beams[left] = beams.getValue(left) + count
          beams[right] = beams.getValue(right) + count
        } else {
          val below = Location(depth, location.index)
          beams[below] = beams.getValue(below) + count
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
