package me.peckb.aoc._2021.calendar.day11

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day11 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    const val STEPS = 100
  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val area = input.map { octopusRow ->
      octopusRow.map { Octopus(Character.getNumericValue(it)) }
    }.toList()

    var flashes = 0

    (1 .. STEPS).forEach { step ->
      val flashesForStep = advance(area, step)
      flashes += flashesForStep
    }

    flashes
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val area = input.map { it.map { Octopus(Character.getNumericValue(it)) } }.toList()

    var allFlashStep = 0
    var step = 0
    while(allFlashStep == 0) {
      step++
      val flashesForStep = advance(area, step)
      if (flashesForStep == 100) {
        allFlashStep = step
      }
    }

    allFlashStep
  }

  private fun advance(area: List<List<Octopus>>, step: Int): Int {
    var flashes = 0
    repeat(area.size) { y ->
      repeat(area[y].size) { x->
        val didFlash = area[y][x].gatherEnergy(step)
        if (didFlash) {
          flashes += 1 + flashNeighbors(area, y, x, step)
        }
      }
    }
    return flashes
  }

  private fun flashNeighbors(area: List<List<Octopus>>, y: Int, x: Int, step: Int): Int {
    var flashes = 0

    (y - 1 .. y + 1).forEach { yNeighbor ->
      (x - 1 .. x + 1).forEach { xNeighbor ->
        if (x != xNeighbor || y != yNeighbor) {
          try {
            val newFlash = area[yNeighbor][xNeighbor].gatherEnergy(step)
            if (newFlash) {
              flashes++
              flashes += flashNeighbors(area, yNeighbor, xNeighbor, step)
            }
          } catch (e: IndexOutOfBoundsException) { /* ... */ }
        }
      }
    }

    return flashes
  }

  private data class Octopus(var energy: Int, var lastFlashStep: Int = -1) {
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

    override fun toString(): String {
      return "$energy"
    }
  }
}
