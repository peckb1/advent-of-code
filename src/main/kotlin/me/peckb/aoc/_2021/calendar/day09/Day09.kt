package me.peckb.aoc._2021.calendar.day09

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day09 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {

  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::day09) { input ->
    val floor = input.toList()

    val minValues = mutableListOf<Int>()

    repeat(floor[0].size) { x ->
      repeat(floor.size) { y ->
        val me = floor[y][x]
        val up = find(floor, y - 1, x)
        val down = find(floor, y + 1, x)
        val left = find(floor, y, x - 1)
        val right = find(floor, y, x + 1)

        if (me < up && me < down && me < left && me < right) {
          minValues.add(me)
        }
      }
    }

    minValues.sumOf { it + 1 }
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day09) { input ->
    val floor = input.toList()
    // [y,x] pairs
    val minLocations = mutableListOf<Pair<Int, Int>>()

    repeat(floor[0].size) { x ->
      repeat(floor.size) { y ->
        val me = floor[y][x]
        val up = find(floor, y - 1, x)
        val down = find(floor, y + 1, x)
        val left = find(floor, y, x - 1)
        val right = find(floor, y, x + 1)

        if (me < up && me < down && me < left && me < right) {
          minLocations.add(y to x)
        }
      }
    }

    val basins = minLocations.map { (y, x) ->
      val spacesInBasin = mutableSetOf<Pair<Int, Int>>(
        y to x
      )
      val spacesToExplore = mutableSetOf<Pair<Int, Pair<Int, Int>>>(
        floor[y][x] to (y - 1 to x),
        floor[y][x] to (y + 1 to x),
        floor[y][x] to (y to x - 1),
        floor[y][x] to (y to x + 1)
      )
      while(spacesToExplore.isNotEmpty()) {
        val spaceToExplore = spacesToExplore.first().also {
          spacesToExplore.remove(it)
        }

        val originalValue = spaceToExplore.first
        val (myY, myX) = spaceToExplore.second
        val valueOfMe = find(floor, myY, myX)

        if (valueOfMe in (originalValue + 1)..8) {
          spacesInBasin.add(myY to myX)
          spacesToExplore.addAll(
            listOf(
              valueOfMe to (myY - 1 to myX),
              valueOfMe to (myY + 1 to myX),
              valueOfMe to (myY to myX - 1),
              valueOfMe to (myY to myX + 1)
            )
          )
        }
      }

      spacesInBasin
    }

    basins
      .sortedByDescending { it.size }
      .take(3)
      .map { it.size }
      .fold(1) { acc, i ->
        i * acc
      }
  }

  private fun day09(line: String) = line.map(Character::getNumericValue)

private fun find(floor: List<List<Int>>, y: Int, x: Int): Int =
  try {
    floor[y][x]
  } catch(e: IndexOutOfBoundsException) {
    Int.MAX_VALUE
  }

}
