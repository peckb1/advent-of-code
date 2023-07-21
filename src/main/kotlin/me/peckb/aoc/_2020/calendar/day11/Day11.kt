

package me.peckb.aoc._2020.calendar.day11

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.util.*
import kotlin.math.max
import kotlin.math.min

@Suppress("LocalVariableName")
class Day11 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val seatingArea = setupSeats(input)

    runSeating(
      seatingArea,
      { y, x -> directNeighborsAreEmpty(seatingArea, y, x) },
      { y, x -> tooManyDirectNeighbors(seatingArea, y, x) },
    )

    countOccupiedSeats(seatingArea)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val seatingArea = setupSeats(input)

    runSeating(
      seatingArea,
      { y, x -> lineNeighborsAreEmpty(seatingArea, y, x) },
      { y, x -> tooManyLineNeighbors(seatingArea, y, x) },
    )

    countOccupiedSeats(seatingArea)
  }

  private fun setupSeats(input: Sequence<String>): Array<Array<Char>> {
    val data = input.toList()
    val seatingArea = Array(data.size) { Array(data.first().length) { FLOOR } }

    data.forEachIndexed { y, row ->
      row.forEachIndexed { x, location ->
        seatingArea[y][x] = location
      }
    }

    return seatingArea
  }

  private fun runSeating(
    seatingArea: Array<Array<Char>>,
    emptyCheck: (Int, Int) -> Boolean,
    occupiedCheck: (Int, Int) -> Boolean,
  ) {
    do {
      val seatsToOccupy = mutableListOf<Location>()
      val seatsToVacate = mutableListOf<Location>()

      seatingArea.forEachIndexed { y, row ->
        row.forEachIndexed { x, loc ->
          if (loc == EMPTY && emptyCheck(y, x)) {
            seatsToOccupy.add(Location(y, x))
          }
          if (loc == OCCUPIED && occupiedCheck(y, x)) {
            seatsToVacate.add(Location(y, x))
          }
        }
      }

      seatsToOccupy.forEach { (y, x) -> seatingArea[y][x] = OCCUPIED }
      seatsToVacate.forEach { (y, x) -> seatingArea[y][x] = EMPTY }

      val changesMade = seatsToOccupy.isNotEmpty() || seatsToVacate.isNotEmpty()
    } while(changesMade)
  }

  private fun countOccupiedSeats(seatingArea: Array<Array<Char>>): Int {
    return seatingArea.sumOf { row ->
      row.count {
        it == OCCUPIED
      }
    }
  }

  private fun directNeighborsAreEmpty(seatingArea: Array<Array<Char>>, y: Int, x: Int): Boolean {
    val (minY, maxY, minX, maxX) = ranges(seatingArea, y, x)

    return (minY..maxY).none { y1 ->
      (minX .. maxX).any { x1 ->
        seatingArea[y1][x1] == OCCUPIED
      }
    }
  }

  private fun tooManyDirectNeighbors(seatingArea: Array<Array<Char>>, y: Int, x: Int): Boolean {
    val (minY, maxY, minX, maxX) = ranges(seatingArea, y, x)

    val occupiedNeighbors = (minY..maxY).sumOf { y1 ->
      (minX .. maxX).count { x1 ->
        seatingArea[y1][x1] == OCCUPIED
      }
    } - 1 // we don't enter tooManyNeighbors unless the seat is occupied, so we'll be one extra seat over

    return occupiedNeighbors >= 4
  }

  private fun lineNeighborsAreEmpty(seatingArea: Array<Array<Char>>, y: Int, x: Int): Boolean {
    return findSeats(seatingArea, y, x).none { it == OCCUPIED }
  }

  private fun tooManyLineNeighbors(seatingArea: Array<Array<Char>>, y: Int, x: Int): Boolean {
    return findSeats(seatingArea, y, x).count { it == OCCUPIED } >= 5
  }

  private fun findSeats(seatingArea: Array<Array<Char>>, y: Int, x: Int): List<Char> {
    val N  = findFirstSeat(seatingArea, y, x, { yDelta -> yDelta - 1 }, { xDelta -> xDelta })
    val NE = findFirstSeat(seatingArea, y, x, { yDelta -> yDelta - 1 }, { xDelta -> xDelta + 1 })
    val E  = findFirstSeat(seatingArea, y, x, { yDelta -> yDelta },     { xDelta -> xDelta + 1 })
    val SE = findFirstSeat(seatingArea, y, x, { yDelta -> yDelta + 1 }, { xDelta -> xDelta + 1 })
    val S  = findFirstSeat(seatingArea, y, x, { yDelta -> yDelta + 1 }, { xDelta -> xDelta })
    val SW = findFirstSeat(seatingArea, y, x, { yDelta -> yDelta + 1 }, { xDelta -> xDelta - 1 })
    val W  = findFirstSeat(seatingArea, y, x, { yDelta -> yDelta },     { xDelta -> xDelta - 1 })
    val NW = findFirstSeat(seatingArea, y, x, { yDelta -> yDelta - 1 }, { xDelta -> xDelta - 1 })

    return listOfNotNull(N, NE, E, SE, S, SW, W, NW)
  }

  private fun findFirstSeat(
    seatingArea: Array<Array<Char>>,
    y: Int,
    x: Int,
    yDelta: (Int) -> Int,
    xDelta: (Int) -> Int
  ): Char? {
    var newY = yDelta(y)
    var newX = xDelta(x)
    while(newY in (seatingArea.indices) && newX in (0 until seatingArea[newY].size)) {
      if (seatingArea[newY][newX] != FLOOR) return seatingArea[newY][newX]
      newY = yDelta(newY)
      newX = xDelta(newX)
    }
    return null
  }

  private fun ranges(seatingArea: Array<Array<Char>>, y: Int, x: Int): Ranges {
    val minY = max(y - 1, 0)
    val maxY = min(y + 1, seatingArea.size - 1)
    val minX = max(x - 1, 0)
    val maxX = min(x + 1, seatingArea[y].size - 1)

    return Ranges(minY, maxY, minX, maxX)
  }

  data class Ranges(val minY: Int, val maxY: Int, val minX: Int, val maxX: Int)

  data class Location(val y: Int, val x: Int)

  companion object {
    private const val FLOOR = '.'
    private const val EMPTY = 'L'
    private const val OCCUPIED = '#'
  }

}
