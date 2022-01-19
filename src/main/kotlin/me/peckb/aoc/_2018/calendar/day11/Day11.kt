package me.peckb.aoc._2018.calendar.day11

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.Int.Companion.MIN_VALUE

class Day11 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val serialNumber = input.toInt()
    val fuelCells = Array(300) { Array(300) { 0 } }
    repeat(300) { y ->
      repeat(300) { x ->
        fuelCells[y][x] = calculatePowerLevel(x, y, serialNumber)
      }
    }

    var maxFuelCellArea: Pair<Pair<Int, Int>, Int> = (-1 to -1) to MIN_VALUE
    (1 until 299).forEach { y ->
      (1 until 299).forEach { x ->
        val sum = (y - 1..y + 1).sumOf { yy ->
          (x - 1..x + 1).sumOf { xx ->
            fuelCells[yy][xx]
          }
        }
        if (sum > maxFuelCellArea.second) maxFuelCellArea = (x - 1 to y - 1) to sum
      }
    }

    maxFuelCellArea.first
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val serialNumber = input.toInt()
    val summedAreaFuelCells = Array(300) { Array(300) { 0 } }
    repeat(300) { y ->
      repeat(300) { x ->
        val ul = summedAreaFuelCells.get(y - 1, x - 1) ?: 0
        val ur = summedAreaFuelCells.get(y - 1, x) ?: 0
        val ll = summedAreaFuelCells.get(y, x - 1) ?: 0
        val lr = calculatePowerLevel(x, y, serialNumber)

        summedAreaFuelCells[y][x] = lr + ll + ur - ul
      }
    }

    var maxFuelCellArea = Triple((-1 to -1), MIN_VALUE, 0)

    (1..300).forEach { gridSize ->
      (0 until 300 - gridSize).forEach { y ->
        (0 until 300 - gridSize).forEach { x ->
          val ul = summedAreaFuelCells[y][x]
          val ur = summedAreaFuelCells[y][x + gridSize]
          val ll = summedAreaFuelCells[y + gridSize][x]
          val lr = summedAreaFuelCells[y + gridSize][x + gridSize]

          val sum = lr + ul - ur - ll
          if (sum > maxFuelCellArea.second) maxFuelCellArea = Triple(x + 1 to y + 1, sum, gridSize)
        }
      }
    }

    Triple(maxFuelCellArea.first.first, maxFuelCellArea.first.second, maxFuelCellArea.third)
  }

  private fun calculatePowerLevel(x: Int, y: Int, serialNumber: Int): Int {
    val rackID = (x + 10)
    var powerLevel = rackID * y
    powerLevel += serialNumber
    powerLevel *= rackID
    val hundredsDigit = (powerLevel % 1000) / 100
    return hundredsDigit - 5
  }

  private fun <T> Array<Array<T>>.get(y: Int, x: Int): T? {
    return if (y in (indices) && x in (this[y].indices)) {
      this[y][x]
    } else {
      null
    }
  }
}
