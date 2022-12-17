package me.peckb.aoc._2022.calendar.day17

import me.peckb.aoc._2022.calendar.day17.Day17.Direction.LEFT
import me.peckb.aoc._2022.calendar.day17.Day17.Direction.RIGHT
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.StringBuilder

class Day17 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val jetPushes = asJetDirections(input)
    val rocks = listOf(
      Rock(1, 4, listOf("####")),
      Rock(3, 3, listOf(".#.", "###", ".#.")),
      Rock(3, 3, listOf("..#", "..#", "###")),
      Rock(4, 1, listOf("#", "#", "#", "#")),
      Rock(2, 2, listOf("##", "##")),
    )

    var rockIndex = 0
    var jetPushesIndex = 0

    val cavern = mutableListOf<String>()

    val numRocksToDrop = 11
    repeat(numRocksToDrop) { rockCounter ->
      println("-------")
      (cavern.size - 1 downTo 0).forEach { println(cavern[it]) }
      println("-------")
      val nextRock = rocks[rockIndex]
      println("${rockCounter + 1} $nextRock is being dropped")
      var leftEdgeIndexOfRock = 2
      var rightEdgeIndexOfRock = leftEdgeIndexOfRock + (nextRock.width - 1)
      var bottomEdgeOfRock = cavern.size + 3 // three blank spaces above the top of the current cavern

      var rockHadSettled = false
      while(!rockHadSettled) {
        // try and push the rock
        when (jetPushes[jetPushesIndex]) {
          LEFT -> {
            // check for an edge of the cavern stoppage
            log(rockCounter, "trying to get pushed LEFT")
            if (leftEdgeIndexOfRock > 0) {
              log(rockCounter, "cavern does not stop us")
              // check for hitting some existing solid ground
              val collision = nextRock.data.withIndex().any { (rockHeightIndexTopAligned, rockRow) ->
                val cavernIndexOfRockIndex = bottomEdgeOfRock + (nextRock.height - 1) - rockHeightIndexTopAligned

                if (0 < cavernIndexOfRockIndex && cavernIndexOfRockIndex < cavern.size) {
                  val leftMostPortion = rockRow[0]
                  if (leftMostPortion == '#') {
                    val rockRowToCollideInto = cavern.elementAt(cavernIndexOfRockIndex)
                    rockRowToCollideInto[leftEdgeIndexOfRock - 1] == '#'
                  } else {
                    false
                  }
                } else {
                  false
                }
              }
              if (!collision) {
                log(rockCounter, "existing rock did not stop us")
                leftEdgeIndexOfRock--
                rightEdgeIndexOfRock--
              } else {
                log(rockCounter, "existing rock stopped us from moving")
              }
            } else {
              log(rockCounter, "cavern edge stopped us")
            }
          }
          RIGHT -> {
            // check for an edge of the cavern stoppage
            log(rockCounter, "trying to get pushed RIGHT")
            if (rightEdgeIndexOfRock < MAX_CHAMBER_INDEX) {
              log(rockCounter, "cavern does not stop us")
              // check for hitting some existing solid ground
              val collision = nextRock.data.withIndex().any { (rockHeightIndexTopAligned, rockRow) ->
                val cavernIndexOfRockIndex = bottomEdgeOfRock + (nextRock.height - 1) - rockHeightIndexTopAligned

                if (0 < cavernIndexOfRockIndex && cavernIndexOfRockIndex < cavern.size) {
                  val rightMostPortion = rockRow[nextRock.width - 1]
                  if (rightMostPortion == '#') {
                    val rockRowToCollideInto = cavern.elementAt(cavernIndexOfRockIndex)
                    rockRowToCollideInto[rightEdgeIndexOfRock + 1] == '#'
                  } else {
                    false
                  }
                } else {
                  false
                }
              }
              if (!collision) {
                log(rockCounter, "existing rock did not stop us")
                leftEdgeIndexOfRock++
                rightEdgeIndexOfRock++
              } else {
                log(rockCounter, "existing rock stopped us from moving")
              }
            } else {
              log(rockCounter, "cavern edge stopped us")
            }
          }
        }

        // is any "solid" part of our rock directly above a "solid" part of the cave?
        // TODO: this should be reversed, going from bottom to top for rock data
        val collision = nextRock.data.withIndex().any { (rockHeightIndexTopAligned, rockRow) ->
          val cavernIndexOfRockIndex = bottomEdgeOfRock + (nextRock.height - 1) - rockHeightIndexTopAligned - 1
          if (0 <= cavernIndexOfRockIndex && cavernIndexOfRockIndex < cavern.size) {
            if (rockCounter == 9) {
              -1
            }
            val rockRowToCollideInto = cavern.elementAt(cavernIndexOfRockIndex)

            rockRow.withIndex().any { (rowIndex, rockSpace) ->
              val rockRowToCollideIntoIndex = leftEdgeIndexOfRock + rowIndex
              rockSpace == '#' && rockRowToCollideInto[rockRowToCollideIntoIndex] == '#'
            }
          } else {
            false
          }
        } || bottomEdgeOfRock == 0

        // if so, we have a collision and merge it into our cave system
        if (collision) {
          log(rockCounter, "We ran into something at the bottom")
          rockHadSettled = true

          if (bottomEdgeOfRock == cavern.size) { // we landed just above the bottom do just add ourselves to the cavern
            log(rockCounter, "we landed normally")
            nextRock.data.reversed().forEach { rockRow ->
              val emptyBefore = ".".repeat(leftEdgeIndexOfRock)
              val emptyAfter = ".".repeat(MAX_CHAMBER_INDEX - rightEdgeIndexOfRock)
              cavern.add("$emptyBefore$rockRow$emptyAfter")
            }
          } else { // we landed "inside" the cavern and need to merge our data
            log(rockCounter, "we need to merge data")
            val numRowsToMerge = cavern.size - bottomEdgeOfRock

            val rockRowsToMerge = nextRock.data.takeLast(numRowsToMerge)
            rockRowsToMerge.forEachIndexed { rowOffset, row ->
              val cavernIndex = (cavern.size - 1) - rowOffset
              val cavernRow = cavern[cavernIndex]
              val newRow = StringBuilder()
              (0 until leftEdgeIndexOfRock).forEach { newRow.append(cavernRow[it]) }
              newRow.append(row)
              ((rightEdgeIndexOfRock + 1) .. MAX_CHAMBER_INDEX).forEach { newRow.append(cavernRow[it]) }
              cavern[cavernIndex] = newRow.toString()
            }
            nextRock.data.dropLast(numRowsToMerge).reversed().forEach { rockRow ->
              val emptyBefore = ".".repeat(leftEdgeIndexOfRock)
              val emptyAfter = ".".repeat(MAX_CHAMBER_INDEX - rightEdgeIndexOfRock)
              cavern.add("$emptyBefore$rockRow$emptyAfter")
            }
          }
        } else { // if not, we should drop the rock by one index
          log(rockCounter, "we would not collide running into something lowering bottom edge from $bottomEdgeOfRock to ${bottomEdgeOfRock-1}")
          bottomEdgeOfRock--
        }

        jetPushesIndex = (jetPushesIndex + 1) % jetPushes.size
      }

      rockIndex = (rockIndex + 1) % rocks.size
    }

    println("-------")
    (cavern.size - 1 downTo 0).forEach { println(cavern[it]) }
    println("-------")
  }

  private fun log(rockCounter: Int, text: String) {
    if (rockCounter == 9) {
      println(text)
    }
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
