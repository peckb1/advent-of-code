package me.peckb.aoc._2025.calendar.day04

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.text.forEach

class Day04 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val printingDepartment = mapPaper(input)

    var removable = 0

    printingDepartment.indices.forEach { r ->
      printingDepartment[r].indices.forEach { c ->
        if (printingDepartment[r][c] == '@' && printingDepartment.paperNeighbors(r, c) < 4) { removable++ }
      }
    }

    removable
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val printingDepartment = mapPaper(input)

    var totalRemovedCount = 0

    do {
      val toRemove = mutableListOf<Pair<Int, Int>>()

      printingDepartment.indices.forEach { r ->
        printingDepartment[r].indices.forEach { c ->
          if (printingDepartment[r][c] == '@' && printingDepartment.paperNeighbors(r, c) < 4) { toRemove.add(r to c) }
        }
      }

      toRemove.forEach { (r, c) -> printingDepartment[r][c] = '.' }
      totalRemovedCount += toRemove.size
    } while (toRemove.isNotEmpty())

    totalRemovedCount
  }

  private fun mapPaper(input: Sequence<String>): MutableList<MutableList<Char>> {
    val workshop = mutableListOf<MutableList<Char>>()

    input.forEach { r ->
      val row = mutableListOf<Char>()
      r.forEach { c -> row.add(c) }
      workshop.add(row)
    }

    return workshop
  }

  private fun MutableList<MutableList<Char>>.paperNeighbors(r: Int, c: Int): Int {
    return (-1 .. 1).sumOf { rDelta ->
      // ensure row bounds
      val rIndex = rDelta + r
      if (rIndex !in 0 until size) return@sumOf 0

      (-1 .. 1).count { cDelta ->
        // don't count ourselves
        if (rDelta == 0 && cDelta == 0) return@count false
        // ensure column bounds
        val cIndex = cDelta + c
        if (cIndex !in 0 until this[rIndex].size) return@count false

        this[rIndex][cIndex] == '@'
      }
    }
  }
}
