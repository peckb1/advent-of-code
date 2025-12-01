package me.peckb.aoc._2025.calendar.day01

import me.peckb.aoc._2025.calendar.day01.Day01.Direction.LEFT
import me.peckb.aoc._2025.calendar.day01.Day01.Direction.RIGHT
import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.sequences.forEach

class Day01 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    const val DIAL_NUMBERS = 100
    const val START_LOCATION = 50
  }

  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::rotation) { rotations ->
    var dialLocation = START_LOCATION
    var countAtZeroAfterMovement = 0

    rotations.forEach { rotation ->
      when (rotation.direction) {
        LEFT  -> dialLocation -= rotation.count
        RIGHT -> dialLocation += rotation.count
      }

      while (dialLocation !in 0 until DIAL_NUMBERS) {
        if (dialLocation < 0) {
          dialLocation += DIAL_NUMBERS
        } else { // if (dialLocation >= DIAL_NUMBERS)
          dialLocation -= DIAL_NUMBERS
        }
      }

      if (dialLocation == 0) { countAtZeroAfterMovement++ }
    }

    countAtZeroAfterMovement
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::rotation) { rotations ->
    var dialLocation = START_LOCATION
    var countAtZeroAfterMovement = 0

    rotations.forEach { rotation ->
      val fullLoops = rotation.count / DIAL_NUMBERS
      val remainingTurns = rotation.count % DIAL_NUMBERS

      // always increment a full loop
      countAtZeroAfterMovement += fullLoops

      val dialLocationBeforeChanging = dialLocation
      if (remainingTurns != 0) {
        when (rotation.direction) {
          RIGHT -> {
            dialLocation += remainingTurns
            // if we pass over 99, increment again
            if (dialLocation >= DIAL_NUMBERS) {
              dialLocation -= DIAL_NUMBERS
              countAtZeroAfterMovement++
            }
          }
          LEFT -> {
            dialLocation -= remainingTurns
            // if we go under 0 ... maybe increment
            if (dialLocation <= 0) {
              // going under zero but starting at zero shouldn't double count
              if (dialLocationBeforeChanging != 0) { countAtZeroAfterMovement++ }
              // but either way we're under zero and need to fix our dial
              if (dialLocation < 0)                { dialLocation += DIAL_NUMBERS }
            }
          }
        }
      }
    }

    countAtZeroAfterMovement
  }

  private fun rotation(line: String) : Rotation {
    val direction = when(val dc = line[0]) {
      'L' -> LEFT
      'R' -> RIGHT
      else -> throw IllegalStateException("Unknown direction $dc")
    }
    val count = line.drop(1).toInt()

    return Rotation(direction, count)
  }

  enum class Direction(val d: String) {
    LEFT("L"), RIGHT("R");

    override fun toString(): String = d
  }

  data class Rotation(val direction: Direction, val count: Int) {
    override fun toString(): String = "$direction$count"
  }
}
