package me.peckb.aoc._2021.calendar.day11

import me.peckb.aoc._2021.calendar.day11.Day11.Octopus
import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

typealias Octopi = List<List<Octopus>>

class Day11 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    const val STEPS = 100
    const val NEVER_STOP_STEPPING = Int.MAX_VALUE
  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::octopusRow) { input ->
    val octopi = input.toList()

    (1..STEPS).sumOf { octopi.countFlashers(it) }
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::octopusRow) { input ->
    val octopi = input.toList()
    val allOctopi = octopi.size * octopi.size

    (1..NEVER_STOP_STEPPING).first { octopi.countFlashers(it) == allOctopi }
  }

  private fun Octopi.countFlashers(step: Int): Int {
    var flashes = 0
    repeat(this.size) { y ->
      repeat(this[y].size) { x ->
        if (this[y][x].gatherEnergy(step)) {
          flashes += 1 + flashNeighbors(y, x, step)
        }
      }
    }
    return flashes
  }

  private fun Octopi.flashNeighbors(y: Int, x: Int, step: Int): Int {
    var flashes = 0
    (y - 1..y + 1).forEach { yNeighbor ->
      (x - 1..x + 1).forEach { xNeighbor ->
        if ((yNeighbor in this.indices) && (xNeighbor in (0 until this[yNeighbor].size))) {
          if (this[yNeighbor][xNeighbor].gatherEnergy(step)) {
            flashes += 1 + flashNeighbors(yNeighbor, xNeighbor, step)
          }
        }
      }
    }
    return flashes
  }

  private fun octopusRow(line: String) = line.map { Octopus(Character.getNumericValue(it)) }

  data class Octopus(var energy: Int, var lastFlashStep: Int = 0) {
    fun gatherEnergy(stepNumber: Int): Boolean {
      if (lastFlashStep != stepNumber) {
        energy++
        if (energy > 9) {
          lastFlashStep = stepNumber
          energy = 0
          return true
        }
      }
      return false
    }
  }
}
