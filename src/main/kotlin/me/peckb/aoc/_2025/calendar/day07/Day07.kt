package me.peckb.aoc._2025.calendar.day07

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.text.indexOf
import kotlin.text.toCharArray

class Day07 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    var beamIndices = mutableSetOf<Int>()
    val manifold = mutableListOf<MutableList<Char>>()

    input.forEachIndexed { index, line ->
      if (index == 0) beamIndices.add(line.indexOf('S'))
      manifold.add(line.toCharArray().toMutableList())
    }

    var depth = 1
    var splits = 0
    while (depth < manifold.size) {
      val updateBeamIndices = mutableSetOf<Int>()
      val line = manifold[depth]

      beamIndices.forEach { beamIndex ->
        if (line[beamIndex] == '^') {
          splits++
          updateBeamIndices.add(beamIndex - 1)
          updateBeamIndices.add(beamIndex + 1)
        } else {
          updateBeamIndices.add(beamIndex)
        }
      }

      beamIndices = updateBeamIndices
      depth++
    }

    splits
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    var beams = mutableMapOf<Location, Long>()
    val manifold = mutableListOf<MutableList<Char>>()

    input.forEachIndexed { index, line ->
      if (index == 0) {
        beams[Location(0, line.indexOf('S'))] = 1
      }
      manifold.add(line.toCharArray().toMutableList())
    }

    var depth = 1
    while (depth < manifold.size) {
      val newBeams = mutableMapOf<Location, Long>().withDefault { 0 }
      val line = manifold[depth]

      beams.forEach { (location, count) ->
        if (location.depth != depth - 1) { throw IllegalStateException("We should only be tracking the last row.") }

        if (line[location.index] == '^') {
          val left = Location(depth, location.index - 1)
          val right = Location(depth, location.index + 1)

          newBeams[left] = newBeams.getValue(left) + count
          newBeams[right] = newBeams.getValue(right) + count
        } else {
          val below = Location(depth, location.index)
          newBeams[below] = newBeams.getValue(below) + count
        }
      }

      beams = newBeams
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
