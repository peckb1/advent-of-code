package me.peckb.aoc._2021.calendar.day11

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day11 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val area = input.map { octopusRow ->
      octopusRow.map { Octopus(Character.getNumericValue(it)) }
    }.toList()

    var flashes = 0

    (1 .. 100).forEach { step ->
      repeat(10) { y ->
        repeat(10) { x->
          val didFlash = area[y][x].gatherEnergy(step)
          if (didFlash) {
            flashes++
            flashes += flashNeighbors(area, y, x, step)
          }
        }
      }
    }

    flashes
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val area = input.map { it.map { Octopus(Character.getNumericValue(it)) } }.toList()

    var allFlashStep = 0
    var step = 0
    while(allFlashStep == 0) {
      step++
      var stepFlashes = 0
      repeat(10) { y ->
        repeat(10) { x->
          val didFlash = area[y][x].gatherEnergy(step)
          if (didFlash) {
            stepFlashes++
            stepFlashes += flashNeighbors(area, y, x, step)
          }
        }
      }
      if (stepFlashes == 100) {
        allFlashStep = step
      }
    }

    allFlashStep
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
