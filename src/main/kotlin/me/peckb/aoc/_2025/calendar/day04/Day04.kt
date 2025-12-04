package me.peckb.aoc._2025.calendar.day04

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max
import kotlin.math.min
import kotlin.text.forEach

class Day04 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val printingDepartment = mapPaper(input)

    var superCount = 0
    printingDepartment.indices.forEach { r ->
      val row = printingDepartment[r]

      row.indices.forEach { c ->
        var count = 0

        if (printingDepartment[r][c] == '@') {
          for (rIndex in (max(0, r - 1)..min(row.size - 1, r + 1))) {
            for (cIndex in (max(0, c - 1)..min(printingDepartment.size - 1, c + 1))) {
              if (printingDepartment[rIndex][cIndex] == '@' && !(rIndex == r && cIndex == c)) {
                count++
              }
            }
          }

          if (count < 4) {
            superCount++
          }
        }
      }
    }

    superCount
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val printingDepartment = mapPaper(input)

    var superDuperCount = 0
    var someWereRemoved = true
    while(someWereRemoved) {
      var superCount = 0
      val toRemove = mutableListOf<Pair<Int, Int>>()
      printingDepartment.indices.forEach { r ->
        val row = printingDepartment[r]


        row.indices.forEach { c ->
          var count = 0

          if (printingDepartment[r][c] == '@') {
            for (rIndex in (max(0, r - 1)..min(row.size - 1, r + 1))) {
              for (cIndex in (max(0, c - 1)..min(printingDepartment.size - 1, c + 1))) {
                if (printingDepartment[rIndex][cIndex] == '@' && !(rIndex == r && cIndex == c)) {
                  count++
                }
              }
            }

            if (count < 4) {
              toRemove.add(r to c)
              superCount++
            }
          }
        }
      }

      toRemove.forEach { (r, c) -> printingDepartment[r][c] = '.' }
      someWereRemoved = superCount != 0
      superDuperCount += superCount
    }

    superDuperCount
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
}
