package me.peckb.aoc._2016.calendar.day10

import arrow.core.Either

typealias Output = Int
typealias RobotId = Int

data class Robot(
  val id: RobotId,
  var lowValue: Int? = null,
  var highValue: Int? = null,
  var high: Either<Output, RobotId>? = null,
  var low: Either<Output, RobotId>? = null
) {
  fun giveValue(value: Int, robots: MutableMap<Int, Robot>) {
    val h = highValue
    val l = lowValue

    if (h == null && l == null) {
      lowValue = value
    } else if (h == null && l != null) {
      if (l < value) {
        highValue = value
      } else {
        highValue = lowValue
        lowValue = value
      }
    } else if (h != null && l == null) {
      if (value < h) {
        lowValue = value
      } else {
        lowValue = highValue
        highValue = value
      }
    } else if (h != null && l != null) {
      throw IllegalStateException("Why are you giving me a value, I already have two!")
    }

    checkAndContinue(robots)
  }

  fun giveHigh(out: Either<Output, RobotId>, robots: MutableMap<Int, Robot>) {
    if (high == null) {
      high = out
    } else {
      throw IllegalStateException("Why are you giving me another high, I already have one!")
    }

    checkAndContinue(robots)
  }

  fun giveLow(out: Either<Output, RobotId>, robots: MutableMap<Int, Robot>) {
    if (low == null) {
      low = out
    } else {
      throw IllegalStateException("Why are you giving me another low, I already have one!")
    }

    checkAndContinue(robots)
  }

  private fun checkAndContinue(robots: MutableMap<Int, Robot>) {
    val l = lowValue
    val h = highValue
    val lowOut = low
    val highOut = high

    if (l == null || h == null || lowOut == null || highOut == null) return

    lowOut.map { robotId ->
      robots[robotId] = (robots[robotId] ?: Robot(robotId)).also { it.giveValue(l, robots) }
    }
    highOut.map { robotId ->
      robots[robotId] = (robots[robotId] ?: Robot(robotId)).also { it.giveValue(h, robots) }
    }
  }
}