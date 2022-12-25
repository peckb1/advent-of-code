package me.peckb.aoc._2022.calendar.day25

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.*
import kotlin.text.StringBuilder

class Day25 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partZero(filename: String) = generatorFactory.forFile(filename).read { input ->
    input.map { Snafu.fromString(it) }
      .fold(SNAFU_ZERO) { acc, nextSnafu -> acc + nextSnafu }
      .toString()
  }

  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::snafu) { input ->
    input.sum().toSnafu()
  }

  private fun snafu(line: String): Double {
    var sum = 0.0
    line.reversed().forEachIndexed { d, c ->
      val digit = 5.0.pow(d.toDouble())
      when (c) {
        '2' -> { sum += 2 * digit }
        '1' -> { sum += digit }
        '0' -> { /* */ }
        '-' -> { sum -= digit }
        '=' -> { sum -= 2 * digit }
      }
    }
    return sum
  }

  private fun Double.toSnafu(): String {
    val snafu = StringBuilder()

    var sum = 0.0
    var maxDigit = 0
    val maxSums = mutableListOf<Double>()

    while (sum < this) {
      maxDigit++
      val digitMax = (2 * 5.0.pow(maxDigit - 1))
      sum += digitMax
      maxSums.add(digitMax)
    }

    var remaining = this
    (maxDigit downTo 1).forEach { digit ->
      if (remaining == 0.0) { snafu.append("0"); return@forEach }

      val index = digit - 1
      val maxAdjustment = maxSums.take(index).sum()

      fun canTwo(n: Double) = abs(n -  maxSums[index]     ) <= maxAdjustment
      fun canOne(n: Double) = abs(n - (maxSums[index] / 2)) <= maxAdjustment

      if (remaining > 0) {
        if      (canTwo(remaining))    { snafu.append("2"); remaining -= maxSums[index] }
        else if (canOne(remaining))    { snafu.append("1"); remaining -= maxSums[index] / 2 }
        else                           { snafu.append("0") }
      } else if (remaining < 0) {
        val absRemaining = abs(remaining)
        if      (canTwo(absRemaining)) { snafu.append("="); remaining += maxSums[index] }
        else if (canOne(absRemaining)) { snafu.append("-"); remaining += maxSums[index] / 2 }
        else                           { snafu.append("0") }
      }
    }

    return snafu.toString()
  }

  companion object {
    private val SNAFU_ZERO = Snafu.fromString("0")
  }
}
