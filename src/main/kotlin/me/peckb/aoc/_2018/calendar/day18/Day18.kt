package me.peckb.aoc._2018.calendar.day18

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day18 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    var forest = input.map { it.toList() }.toList()
    repeat(TIME) { forest = forest.evolve() }
    forest.woodCount()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    var forest = input.map { it.toList() }.toList()
    // our pattern starts its repeat on step 574
    repeat(573) {
      forest = forest.evolve()
    }
    // our pattern is 28 steps long before we hit the first value again
    val pattern = (0 until 28).map {
      forest = forest.evolve()
      forest.woodCount()
    }

    // find out how far into the pattern "FORVER" will be
    // then remove one to get our pattern index
    val indexInPattern = ((FOREVER - 573) % 28) - 1
    pattern[indexInPattern]
  }

  private fun List<List<Char>>.evolve(): List<List<Char>> {
    val forest = this

    return buildList {
      forest.forEachIndexed { y, row ->
        val newRow = buildList {
          row.forEachIndexed { x, self ->
            val (adjacentTrees, adjacentLumber) = forest.findNeighbors(y, x)
            when (self) {
              OPEN -> if (adjacentTrees >= 3) add(TREES) else add(OPEN)
              TREES -> if (adjacentLumber >= 3) add(LUMBER) else add(TREES)
              LUMBER -> if (adjacentTrees == 0 || adjacentLumber == 0) add(OPEN) else add(LUMBER)
            }
          }
        }
        add(newRow)
      }
    }
  }

  private fun List<List<Char>>.findNeighbors(myY: Int, myX: Int): Pair<Int, Int> {
    var adjacentTrees = 0
    var adjacentLumber = 0

    (myY - 1..myY + 1).forEach { y ->
      (myX - 1..myX + 1).forEach { x ->
        if (x != myX || y != myY) {
          if (y in indices) {
            if (x in 0 until this[y].size) {
              val adjacent = this[y][x]
              if (adjacent == TREES) {
                adjacentTrees++
              } else if (adjacent == LUMBER) {
                adjacentLumber++
              }
            }
          }
        }
      }
    }

    return adjacentTrees to adjacentLumber
  }

  private fun List<List<Char>>.woodCount(): Int {
    var treesCount = 0;
    var lumberCount = 0
    forEach { row ->
      row.forEach { c ->
        if (c == TREES) {
          treesCount++
        } else if (c == LUMBER) {
          lumberCount++
        }
      }
    }

    return treesCount * lumberCount
  }

  companion object {
    const val OPEN = '.'
    const val TREES = '|'
    const val LUMBER = '#'

    const val TIME = 10
    const val FOREVER = 1000000000
  }
}

