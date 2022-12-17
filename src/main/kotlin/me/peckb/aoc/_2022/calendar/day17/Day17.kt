package me.peckb.aoc._2022.calendar.day17

import me.peckb.aoc._2022.calendar.day17.Day17.Direction.LEFT
import me.peckb.aoc._2022.calendar.day17.Day17.Direction.RIGHT
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.min

class Day17 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    dropRocks(input, 2022).size
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val rocks = 10000
    val cavesWithProbableCycle = dropRocks(input, rocks)
    var cycle: List<String> = emptyList()
    (5000 downTo 1500).any { possibleCycleSize ->
      (0 until (rocks/possibleCycleSize)).any { cycleStartPosition ->
        val cavesToExplore = cavesWithProbableCycle.drop(cycleStartPosition)
        cavesToExplore.chunked(possibleCycleSize).windowed(2).any { (first, last) ->
          (first == last).also { if (it) { cycle = first } }
        }
      }
    }

    val minRocksForCycle = (3000 until 10000).first { rocksToDrop ->
      val caves = dropRocks(input, rocksToDrop)
      caves.takeLast(cycle.size) == cycle
    }
    val nextRocksForCycle = ((minRocksForCycle + 1) until 10000).first { rocksToDrop ->
      val caves = dropRocks(input, rocksToDrop)
      caves.takeLast(cycle.size) == cycle
    }

    val rocksCycleLength = nextRocksForCycle - minRocksForCycle
    val sizeAtFirstCycle = dropRocks(input, minRocksForCycle).size
    val sizeAtSecondCycle = dropRocks(input, nextRocksForCycle).size

    val heightGrownEachCycle = sizeAtSecondCycle - sizeAtFirstCycle

    val numberOfCycles = (1000000000000 - minRocksForCycle) / rocksCycleLength
    val rockCountBeforeACyclePushesUsOver = minRocksForCycle + (numberOfCycles * rocksCycleLength)
    val rocksRemainingToPlace = 1000000000000 - rockCountBeforeACyclePushesUsOver

    val cavesAfterAddingRemainingRocks = dropRocks(input, minRocksForCycle + rocksRemainingToPlace.toInt())
    val heightAdded = cavesAfterAddingRemainingRocks.size - sizeAtFirstCycle

    sizeAtFirstCycle.toLong() + (heightGrownEachCycle * numberOfCycles) + heightAdded
  }

  private fun dropRocks(input: String, rocksToDrop: Int): List<String> {
    val jetPushes = asJetDirections(input)
    val rocks = listOf(
      Rock(1, 4, listOf("1111")),
      Rock(3, 3, listOf(".2.", "222", ".2.")),
      Rock(3, 3, listOf("..3", "..3", "333")),
      Rock(4, 1, listOf("4", "4", "4", "4")),
      Rock(2, 2, listOf("55", "55")),
    )

    var rockIndex = 0
    var jetPushesIndex = 0
    val cavern = mutableListOf<String>()

    repeat(rocksToDrop) { rockCounter ->
      val nextRock = rocks[rockIndex]
      val rockEdges = RockEdges(
        leftEdgeIndexOfRock = 2,
        bottomEdgeOfRock = cavern.size + 3, // three blank spaces above the top of the current cavern
        rightEdgeIndexOfRock = 2 + (nextRock.width - 1)
      )

      var rockHadSettled = false
      while(!rockHadSettled) {
        // try and push the rock
        when (jetPushes[jetPushesIndex]) {
          LEFT -> tryMoveRockLeft(cavern, nextRock, rockEdges)
          RIGHT -> tryMoveRockRight(cavern, nextRock, rockEdges)
        }

        rockHadSettled = checkForDropCollision(cavern, nextRock, rockEdges)

        // if so, we have a collision and merge it into our cave system
        if (rockHadSettled) {
          if (rockEdges.bottomEdgeOfRock == cavern.size) { // we landed just above the bottom do just add ourselves to the cavern
            landRockOnTop(cavern, nextRock, rockEdges)
          } else { // we landed "inside" the cavern and need to merge our data
            landRockInsideCavern(cavern, nextRock, rockEdges)
          }
        } else { // if not, we should drop the rock by one index
          rockEdges.bottomEdgeOfRock--
        }

        jetPushesIndex = (jetPushesIndex + 1) % jetPushes.size
      }

      rockIndex = (rockIndex + 1) % rocks.size
    }

    return cavern
  }

  private fun landRockInsideCavern(cavern: MutableList<String>, nextRock: Rock, rockEdges: RockEdges) {
    val numRowsToMerge = min(cavern.size - rockEdges.bottomEdgeOfRock, nextRock.height)

    val rockRowsToMerge = nextRock.data.takeLast(numRowsToMerge)
    rockRowsToMerge.forEachIndexed { rowOffset, rockRow ->
      val emptyBefore = ".".repeat(rockEdges.leftEdgeIndexOfRock)
      val emptyAfter = ".".repeat(MAX_CHAMBER_INDEX - rockEdges.rightEdgeIndexOfRock)
      val myRow = "$emptyBefore$rockRow$emptyAfter"
      val cavernIndex = (rockEdges.bottomEdgeOfRock + (numRowsToMerge - 1) - rowOffset)
      val cavernRow = cavern[cavernIndex]
      val newRow = myRow.zip(cavernRow).map { (me, them) ->
        if (me == '.') them else me
      }.joinToString("")
      cavern[cavernIndex] = newRow
    }
    nextRock.data.dropLast(numRowsToMerge).reversed().forEach { rockRow ->
      val emptyBefore = ".".repeat(rockEdges.leftEdgeIndexOfRock)
      val emptyAfter = ".".repeat(MAX_CHAMBER_INDEX - rockEdges.rightEdgeIndexOfRock)
      cavern.add("$emptyBefore$rockRow$emptyAfter")
    }
  }

  private fun landRockOnTop(cavern: MutableList<String>, nextRock: Rock, rockEdges: RockEdges) {
    nextRock.data.reversed().forEach { rockRow ->
      val emptyBefore = ".".repeat(rockEdges.leftEdgeIndexOfRock)
      val emptyAfter = ".".repeat(MAX_CHAMBER_INDEX - rockEdges.rightEdgeIndexOfRock)
      cavern.add("$emptyBefore$rockRow$emptyAfter")
    }
  }

  private fun checkForDropCollision(cavern: MutableList<String>, nextRock: Rock, rockEdges: RockEdges): Boolean {
    // is any "solid" part of our rock directly above a "solid" part of the cave?
    // TODO: this should be reversed, going from bottom to top for rock data
    return nextRock.data.withIndex().any { (rockHeightIndexTopAligned, rockRow) ->
      val cavernIndexOfRockIndex = rockEdges.bottomEdgeOfRock + (nextRock.height - 1) - rockHeightIndexTopAligned - 1
      if (0 <= cavernIndexOfRockIndex && cavernIndexOfRockIndex < cavern.size) {
        val rockRowToCollideInto = cavern.elementAt(cavernIndexOfRockIndex)

        rockRow.withIndex().any { (rowIndex, rockSpace) ->
          val rockRowToCollideIntoIndex = rockEdges.leftEdgeIndexOfRock + rowIndex
          rockSpace != '.' && rockRowToCollideInto[rockRowToCollideIntoIndex] != '.'
        }
      } else {
        false
      }
    } || rockEdges.bottomEdgeOfRock == 0
  }

  private fun tryMoveRockRight(cavern: MutableList<String>, nextRock: Rock, rockEdges: RockEdges) {
    // check for an edge of the cavern stoppage
    if (rockEdges.rightEdgeIndexOfRock < MAX_CHAMBER_INDEX) {
      // check for hitting some existing solid ground
      val collision = nextRock.data.withIndex().any { (rockHeightIndexTopAligned, rockRow) ->
        val cavernIndexOfRockIndex = rockEdges.bottomEdgeOfRock + (nextRock.height - 1) - rockHeightIndexTopAligned

        if (0 < cavernIndexOfRockIndex && cavernIndexOfRockIndex < cavern.size) {
          rockRow.withIndex().any { (rockRowIndex, collisionCheckPortion) ->
            if (collisionCheckPortion != '.') {
              val rockRowToCollideInto = cavern.elementAt(cavernIndexOfRockIndex)
              rockRowToCollideInto[rockEdges.rightEdgeIndexOfRock + 1 - (nextRock.width - 1 - rockRowIndex)] != '.'
            } else {
              false
            }
          }
        } else {
          false
        }
      }
      if (!collision) {
        rockEdges.leftEdgeIndexOfRock++
        rockEdges.rightEdgeIndexOfRock++
      }
    }
  }

  private fun tryMoveRockLeft(cavern: MutableList<String>, nextRock: Rock, rockEdges: RockEdges) {
      // check for an edge of the cavern stoppage
      if (rockEdges.leftEdgeIndexOfRock > 0) {
        // check for hitting some existing solid ground
        val collision = nextRock.data.withIndex().any { (rockHeightIndexTopAligned, rockRow) ->
          val cavernIndexOfRockIndex = rockEdges.bottomEdgeOfRock + (nextRock.height - 1) - rockHeightIndexTopAligned

          if (0 < cavernIndexOfRockIndex && cavernIndexOfRockIndex < cavern.size) {
            rockRow.withIndex().any { (rockRowIndex, collisionCheckPortion) ->
              if (collisionCheckPortion != '.') {
                val rockRowToCollideInto = cavern.elementAt(cavernIndexOfRockIndex)
                rockRowToCollideInto[rockEdges.leftEdgeIndexOfRock - 1 + (rockRowIndex)] != '.'
              } else {
                false
              }
            }
          } else {
            false
          }
        }
        if (!collision) {
          rockEdges.leftEdgeIndexOfRock--
          rockEdges.rightEdgeIndexOfRock--
        }
      }
  }

  private fun asJetDirections(input: String): List<Direction> = input.map {
    if (it == '>') RIGHT else LEFT
  }

  data class Rock(val height: Int, val width: Int, val data: List<String>)
  data class RockEdges(var leftEdgeIndexOfRock: Int, var bottomEdgeOfRock: Int, var rightEdgeIndexOfRock: Int)
  enum class Direction { LEFT, RIGHT }

  companion object {
    private const val MAX_CHAMBER_INDEX = 6
  }
}
