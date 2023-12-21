package me.peckb.aoc._2023.calendar.day21

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day21 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val map = mutableListOf<MutableList<Char>>()
    var startRow = -1
    var startCol = -1

    input.forEachIndexed { rowIndex, row ->
      val r = row.toCharArray().toMutableList()
      r.forEachIndexed { colIndex, c ->
        if (c == 'S') {
          startRow = rowIndex
          startCol = colIndex
        }
      }
      map.add(r)
    }


    val stepsToTake = 64

    val spacesICanStandOn = mutableSetOf<Pair<Int, Int>>().apply {
      add(startRow to startCol)
    }

    repeat(stepsToTake) {
      val newSteps = mutableSetOf<Pair<Int, Int>>()
      spacesICanStandOn.toList().forEach { step ->
        val (row, col) = step
        val n = (-1 to 0).takeIf { (dr, dc) ->
          (0..map.lastIndex).contains(row + dr) && (0..map[row + dr].lastIndex).contains(col + dc)
        }
        val s = (1 to 0).takeIf { (dr, dc) ->
          (0..map.lastIndex).contains(row + dr) && (0..map[row + dr].lastIndex).contains(col + dc)
        }
        val e = (0 to 1).takeIf { (dr, dc) ->
          (0..map.lastIndex).contains(row + dr) && (0..map[row + dr].lastIndex).contains(col + dc)
        }
        val w = (0 to -1).takeIf { (dr, dc) ->
          (0..map.lastIndex).contains(row + dr) && (0..map[row + dr].lastIndex).contains(col + dc)
        }

        val stepDeltas = listOfNotNull(n, s, e, w).filter { (dr, dc) -> map[row + dr][col + dc] != '#' }
        stepDeltas.forEach { (dr, dc) -> newSteps.add(row + dr to col + dc) }
      }
      spacesICanStandOn.clear()
      spacesICanStandOn.addAll(newSteps)
    }

    spacesICanStandOn.size
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val map = mutableListOf<MutableList<Location>>()
    var startRow = -1
    var startCol = -1

    input.forEachIndexed { rowIndex, row ->
      val newRow = mutableListOf<Location>()

      newRow.addAll(
        row.mapIndexed { colIndex, c ->
          when (c) {
            '#' -> Location.Wall(rowIndex, colIndex)
            '.' -> Location.Empty(rowIndex, colIndex)
            'S' -> {
              startRow = rowIndex
              startCol = colIndex
              Location.Empty(rowIndex, colIndex)
            }

            else -> throw IllegalStateException("Unknown Space: $c")
          }
        }
      )

      map.add(newRow)
    }

//    map.forEach { row ->
//      println(row.joinToString(""))
//    }

    val allLocationsInListForm = map.flatten()

    map[startRow][startCol].also {
      it.addPendingElf(Dimension(0, 0))
      it.applyPendingElves()
    }

    repeat(1000) { round ->
      allLocationsInListForm.filter { it.hasAnElf() }.forEach { locationToMoveElf ->
        val locationsWithElves = locationToMoveElf.elfNeighbors(map)

        locationsWithElves.forEach { (loc, dim) ->
          loc.addPendingElf(dim)
        }
      }

      allLocationsInListForm.forEach {
        it.applyPendingElves()
      }

//      println("After Round ${round + 1}")
//      allLocationsInListForm.filter { it.elfCount() > 0 }.forEach { loc ->
//        println("\t${loc.row},${loc.col} has ${loc.elfCount()} elves.")
//      }
//      println()
    }

    allLocationsInListForm.sumOf { loc ->  loc.elfCount() }
  }

  abstract class Location {
    private val pendingElfDimensions = mutableSetOf<Dimension>()

    private val elfDimensions = mutableSetOf<Dimension>()

    fun addPendingElf(dimension: Dimension) {
      pendingElfDimensions.add(dimension)
    }

    fun applyPendingElves() {
      elfDimensions.clear()
      elfDimensions.addAll(pendingElfDimensions)
      pendingElfDimensions.clear()
    }

    fun hasAnElf(): Boolean {
      return elfDimensions.isNotEmpty()
    }

    fun elfNeighbors(map: List<List<Location>>): List<Pair<Location, Dimension>> {
      return elfDimensions.flatMap { myDimension ->
        listOf(
          -1 to  0,
           1 to  0,
           0 to  1,
           0 to -1
        ).mapNotNull { (dr, dc) ->
          if (row + dr < 0) {
            // going off the north dimension
            map.last()[col + dc] to Dimension(myDimension.x, myDimension.y - 1)
          } else if (row + dr >= map.size) {
            // going off the south dimension
            map.first()[col + dc] to Dimension(myDimension.x, myDimension.y + 1)
          } else if (col + dc < 0) {
            // going off the west dimension
            map[row + dr].last() to Dimension(myDimension.x - 1, myDimension.y)
          } else if (col + dc >= map[row].size) {
            // going off the east dimension
            map[row + dr].first() to Dimension(myDimension.x + 1, myDimension.y)
          } else {
            // we're still in this dimension
            if (map[row + dr][col + dc] is Wall) {
              null
            } else {
              map[row + dr][col + dc] to myDimension
            }
          }
        }
//      }.also { neighborList ->
//        println("$row,$col reporting ${neighborList.map { it.first.loc() to it.second }} as neighbors")
      }
    }

    fun elfCount() = elfDimensions.count()

    private fun loc(): String = "[$row,$col]"

    abstract val row: Int

    abstract val col: Int

    data class Wall(val r: Int, val c: Int) : Location() {
      override val row = r
      override val col = c

      override fun toString() = "#"
    }

    data class Empty(val r: Int, val c: Int) : Location() {
      override val row = r
      override val col = c

      override fun toString() = "."
    }
  }

  data class Dimension(val x: Int, val y: Int)
}
