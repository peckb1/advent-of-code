package me.peckb.aoc._2022.calendar.day25

import arrow.core.padZip
import me.peckb.aoc._2022.calendar.day25.SnafuDigit.Companion.add
import me.peckb.aoc._2022.calendar.day25.SnafuDigit.Companion.fromChar
import me.peckb.aoc._2022.calendar.day25.SnafuDigit.ZERO

class Snafu private constructor(val number: List<SnafuDigit>) {
  infix operator fun plus(other: Snafu): Snafu {
    var nextCarry = ZERO
    val snafuString = StringBuilder()

    number.reversed().padZip(other.number.reversed()).forEach { (d1, d2) ->
      add(d1 ?: ZERO, d2 ?: ZERO, nextCarry).also { (result, carry) ->
        snafuString.append(result.c)
        nextCarry = carry
      }
    }

    if (nextCarry != ZERO) snafuString.append(nextCarry.c)

    return fromString(snafuString.toString().reversed())
  }

  override fun toString(): String = number.map { it.c }.joinToString("")

  companion object {
    fun fromString(snafuString: String) = Snafu(snafuString.map { fromChar(it) })
  }
}
