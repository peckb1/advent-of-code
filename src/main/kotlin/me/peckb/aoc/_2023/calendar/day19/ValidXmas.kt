package me.peckb.aoc._2023.calendar.day19

import kotlin.math.max
import kotlin.math.min

data class ValidXmas(
  val xMin: Int = 1, val xMax: Int = 4000,
  val mMin: Int = 1, val mMax: Int = 4000,
  val aMin: Int = 1, val aMax: Int = 4000,
  val sMin: Int = 1, val sMax: Int = 4000,
) {
  fun adjustGreaterThan(variableName: Char, value: Int): ValidXmas {
    return when (variableName) {
      'x' -> copy(xMin = max(xMin, value))
      'm' -> copy(mMin = max(mMin, value))
      'a' -> copy(aMin = max(aMin, value))
      's' -> copy(sMin = max(sMin, value))
      else -> throw IllegalArgumentException("Unknown variable: $variableName")
    }
  }

  fun adjustLessThan(variableName: Char, value: Int): ValidXmas {
    return when (variableName) {
      'x' -> copy(xMax = min(xMax, value))
      'm' -> copy(mMax = min(mMax, value))
      'a' -> copy(aMax = min(aMax, value))
      's' -> copy(sMax = min(sMax, value))
      else -> throw IllegalArgumentException("Unknown variable: $variableName")
    }
  }

  fun allowed(): Long {
    return (xMin..xMax).count().toLong() *
      (mMin..mMax).count().toLong() *
      (aMin..aMax).count().toLong() *
      (sMin..sMax).count().toLong()
  }
}
