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
    doThing(input, 2022).size
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val rocks = 10000
    val cavesWithProbableCycle = doThing(input, rocks)
    var cycle: List<String> = emptyList()
    (rocks / 2 downTo 1).any { possibleCycleSize ->
      (0 until (rocks/possibleCycleSize)).any { cycleStartPosition ->
        val cavesToExplore = cavesWithProbableCycle.drop(cycleStartPosition)
        cavesToExplore.chunked(possibleCycleSize).windowed(2).any { (first, last) ->
          (first == last).also { if (it) { cycle = first } }
        }
      }
    }

    val minRocksForCycle = (3000 until 10000).first { rocksToDrop ->
      val caves = doThing(input, rocksToDrop)
      caves.takeLast(cycle.size) == cycle
    }
    val nextRocksForCycle = ((minRocksForCycle + 1) until 10000).first { rocksToDrop ->
      val caves = doThing(input, rocksToDrop)
      caves.takeLast(cycle.size) == cycle
    }

    val rocksCycleLength = nextRocksForCycle - minRocksForCycle
    val sizeAtFirstCycle = doThing(input, minRocksForCycle).size
    val sizeAtSecondCycle = doThing(input, nextRocksForCycle).size

    val heightGrownEachCycle = sizeAtSecondCycle - sizeAtFirstCycle

    val numberOfCycles = (1000000000000 - minRocksForCycle) / rocksCycleLength
    val rockCountBeforeACyclePushesUsOver = minRocksForCycle + (numberOfCycles * rocksCycleLength)
    val rocksRemainingToPlace = 1000000000000 - rockCountBeforeACyclePushesUsOver

    val cavesAfterAddingRemainingRocks = doThing(input, minRocksForCycle + rocksRemainingToPlace.toInt())
    val heightAdded = cavesAfterAddingRemainingRocks.size - sizeAtFirstCycle

    sizeAtFirstCycle.toLong() + (heightGrownEachCycle * numberOfCycles) + heightAdded
  }

  private fun doThing(input: String, rocksToDrop: Int): List<String> {
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
      var leftEdgeIndexOfRock = 2
      var rightEdgeIndexOfRock = leftEdgeIndexOfRock + (nextRock.width - 1)
      var bottomEdgeOfRock = cavern.size + 3 // three blank spaces above the top of the current cavern

      var rockHadSettled = false
      while(!rockHadSettled) {
        // try and push the rock
        when (jetPushes[jetPushesIndex]) {
          LEFT -> {
            // check for an edge of the cavern stoppage
            if (leftEdgeIndexOfRock > 0) {
              // check for hitting some existing solid ground
              val collision = nextRock.data.withIndex().any { (rockHeightIndexTopAligned, rockRow) ->
                val cavernIndexOfRockIndex = bottomEdgeOfRock + (nextRock.height - 1) - rockHeightIndexTopAligned

                if (0 < cavernIndexOfRockIndex && cavernIndexOfRockIndex < cavern.size) {
                  rockRow.withIndex().any { (rockRowIndex, collisionCheckPortion) ->
                    if (collisionCheckPortion != '.') {
                      val rockRowToCollideInto = cavern.elementAt(cavernIndexOfRockIndex)
                      rockRowToCollideInto[leftEdgeIndexOfRock - 1 + (rockRowIndex)] != '.'
                    } else {
                      false
                    }
                  }
                } else {
                  false
                }
              }
              if (!collision) {
                leftEdgeIndexOfRock--
                rightEdgeIndexOfRock--
              }
            }
          }
          RIGHT -> {
            // check for an edge of the cavern stoppage
            if (rightEdgeIndexOfRock < MAX_CHAMBER_INDEX) {
              // check for hitting some existing solid ground
              val collision = nextRock.data.withIndex().any { (rockHeightIndexTopAligned, rockRow) ->
                val cavernIndexOfRockIndex = bottomEdgeOfRock + (nextRock.height - 1) - rockHeightIndexTopAligned

                if (0 < cavernIndexOfRockIndex && cavernIndexOfRockIndex < cavern.size) {
                  rockRow.withIndex().any { (rockRowIndex, collisionCheckPortion) ->
                    if (collisionCheckPortion != '.') {
                      val rockRowToCollideInto = cavern.elementAt(cavernIndexOfRockIndex)
                      rockRowToCollideInto[rightEdgeIndexOfRock + 1 - (nextRock.width - 1 - rockRowIndex)] != '.'
                    } else {
                      false
                    }
                  }
                } else {
                  false
                }
              }
              if (!collision) {
                leftEdgeIndexOfRock++
                rightEdgeIndexOfRock++
              }
            }
          }
        }


        // is any "solid" part of our rock directly above a "solid" part of the cave?
        // TODO: this should be reversed, going from bottom to top for rock data
        val collision = nextRock.data.withIndex().any { (rockHeightIndexTopAligned, rockRow) ->
          val cavernIndexOfRockIndex = bottomEdgeOfRock + (nextRock.height - 1) - rockHeightIndexTopAligned - 1
          if (0 <= cavernIndexOfRockIndex && cavernIndexOfRockIndex < cavern.size) {
            val rockRowToCollideInto = cavern.elementAt(cavernIndexOfRockIndex)

            rockRow.withIndex().any { (rowIndex, rockSpace) ->
              val rockRowToCollideIntoIndex = leftEdgeIndexOfRock + rowIndex
              rockSpace != '.' && rockRowToCollideInto[rockRowToCollideIntoIndex] != '.'
            }
          } else {
            false
          }
        } || bottomEdgeOfRock == 0

        // if so, we have a collision and merge it into our cave system
        if (collision) {
          rockHadSettled = true

          if (bottomEdgeOfRock == cavern.size) { // we landed just above the bottom do just add ourselves to the cavern
            nextRock.data.reversed().forEach { rockRow ->
              val emptyBefore = ".".repeat(leftEdgeIndexOfRock)
              val emptyAfter = ".".repeat(MAX_CHAMBER_INDEX - rightEdgeIndexOfRock)
              cavern.add("$emptyBefore$rockRow$emptyAfter")
            }
          } else { // we landed "inside" the cavern and need to merge our data
            val numRowsToMerge = min(cavern.size - bottomEdgeOfRock, nextRock.height)

            val rockRowsToMerge = nextRock.data.takeLast(numRowsToMerge)
            rockRowsToMerge.forEachIndexed { rowOffset, rockRow ->
              val emptyBefore = ".".repeat(leftEdgeIndexOfRock)
              val emptyAfter = ".".repeat(MAX_CHAMBER_INDEX - rightEdgeIndexOfRock)
              val myRow = "$emptyBefore$rockRow$emptyAfter"
              val cavernIndex = (bottomEdgeOfRock + (numRowsToMerge - 1) - rowOffset)
              val cavernRow = cavern[cavernIndex]
              val newRow = myRow.zip(cavernRow).map { (me, them) ->
                if (me == '.') them else me
              }.joinToString("")
              cavern[cavernIndex] = newRow
            }
            nextRock.data.dropLast(numRowsToMerge).reversed().forEach { rockRow ->
              val emptyBefore = ".".repeat(leftEdgeIndexOfRock)
              val emptyAfter = ".".repeat(MAX_CHAMBER_INDEX - rightEdgeIndexOfRock)
              cavern.add("$emptyBefore$rockRow$emptyAfter")
            }
          }
        } else { // if not, we should drop the rock by one index
          bottomEdgeOfRock--
        }

        jetPushesIndex = (jetPushesIndex + 1) % jetPushes.size
      }

      rockIndex = (rockIndex + 1) % rocks.size
    }

    return cavern
  }

  private fun asJetDirections(input: String): List<Direction> = input.map {
    if (it == '>') RIGHT else LEFT
  }

  data class Rock(val height: Int, val width: Int, val data: List<String>)

  enum class Direction {
    LEFT, RIGHT
  }

  companion object {
    private const val MAX_CHAMBER_INDEX = 6
  }
}
