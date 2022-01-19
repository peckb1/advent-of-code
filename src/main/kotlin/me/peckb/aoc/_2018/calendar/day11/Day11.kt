package me.peckb.aoc._2018.calendar.day11

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.Int.Companion.MIN_VALUE

class Day11 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val serialNumber = input.toInt()
    val fuelCells = createFuelCells(serialNumber)

    var maxFuelCellArea: Pair<Pair<Int, Int>, Int> = (-1 to -1) to MIN_VALUE
    (1 until 299).forEach { y ->
      (1 until 299).forEach { x ->
        val sum = (y - 1..y + 1).sumOf { yy ->
          (x - 1..x + 1).sumOf { xx ->
            fuelCells[yy][xx]
          }
        }
        if (sum > maxFuelCellArea.second) maxFuelCellArea = (x - 1 to y - 1) to sum.toInt()
      }
    }

    maxFuelCellArea.first
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val serialNumber = input.toInt()
    val fuelCells = createFuelCells(serialNumber)

    var maxFuelCellArea: Triple<Pair<Int, Int>, Long, Int> = Triple((-1 to -1), MIN_VALUE.toLong(), 0)
    // the original search in (3 .. 300) took about six minutes
    // in theory we could keep track of every m.n grid up to 1/2 the grid
    // and then use the sums of smaller squares to make larger squares.
    //
    // for example 159 has a prime factorization of (3, 53)
    // so if we take the smallest prime (3) we know we can use 3 squares of 53 (159 / 3)
    // those size 53 squares would have been calculated individually though as it is a prime number
    //
    // or 273 has a prime factorization of (3, 3, 31)
    // so if we take the smallest prime (3) we know we can use 3 squares of 93 (273 / 3)
    // those squares of size 93 would have been calculated using 3 squares of 31 (its factors are 3, 31)
    // and those squares of size 31 would have originally been calculated individually as 31 is prime
    //
    // But this would take a while to implement :P
    (14..16).forEach { gridSize ->
      (0 until 300 - gridSize).forEach { y ->
        (0 until 300 - gridSize).forEach { x ->
          val sum = (y..y + (gridSize - 1)).sumOf { yy ->
            (x..x + (gridSize - 1)).sumOf { xx ->
              fuelCells[yy][xx]
            }
          }
          if (sum > maxFuelCellArea.second) maxFuelCellArea = Triple(x to y, sum, gridSize)
        }
      }
    }

    Triple(maxFuelCellArea.first.first, maxFuelCellArea.first.second, maxFuelCellArea.third)
  }

  private fun createFuelCells(serialNumber: Int): Array<Array<Long>> {
    val fuelCells = Array(300) { Array(300) { 0L } }
    repeat(300) { y ->
      repeat(300) { x ->
        val rackID = (x + 10).toLong()
        var powerLevel = rackID * y
        powerLevel += serialNumber
        powerLevel *= rackID
        val hundredsDigit = (powerLevel % 1000) / 100
        fuelCells[y][x] = hundredsDigit - 5
      }
    }
    return fuelCells
  }
}
