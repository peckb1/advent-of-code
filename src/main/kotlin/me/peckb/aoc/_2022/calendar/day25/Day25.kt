package me.peckb.aoc._2022.calendar.day25

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.StringBuilder
import kotlin.math.*

class Day25 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::snafu) { input ->
    input.sum().toSnafu()
  }

  private fun snafu(line: String): Double {
    var sum = 0.0
    line.reversed().forEachIndexed { d, c ->
      val digit = 5.0.pow(d.toDouble())
      when (c) {
        '2' -> { sum += 2 * digit }
        '1' -> { sum += digit}
        '0' -> { /* */ }
        '-' -> { sum -= digit }
        '=' -> { sum -= 2 * digit }
      }
    }
    return sum
  }

  private fun Double.toSnafu() : String {
    val snafu = StringBuilder()

    var sum = 0.0
    var maxDigit = 0
    val maxSums = mutableListOf<Double>()

    while(sum < this) {
      maxDigit++
      val digitMax = (2 * 5.0.pow(maxDigit - 1))
      sum += digitMax
      maxSums.add(digitMax)
    }

    var res = this
    (maxDigit downTo 1).forEach { digit ->
      val index = digit - 1
      if (res == 0.0) {
        snafu.append("0")
        return@forEach
      }
      val maxAdjustment = maxSums.take(index).sum()

      if (res > 0) {
        if (abs(res - maxSums[index]) <= maxAdjustment) {
          snafu.append("2")
          res -= maxSums[index]
        } else if (abs(res - (maxSums[index] / 2)) <= maxAdjustment) {
          snafu.append("1")
          res -= maxSums[index] / 2
        } else {
          snafu.append("0")
        }
      } else if (res < 0) {
        if (abs(abs(res) - maxSums[index]) <= maxAdjustment) {
          snafu.append("=")
          res += maxSums[index]
        } else if (abs(abs(res) - (maxSums[index] / 2)) <= maxAdjustment) {
          snafu.append("-")
          res += maxSums[index] / 2
        } else {
          snafu.append("0")
        }
      }
    }

    return snafu.toString()
  }
}
