package me.peckb.aoc._2022.calendar.day25

import arrow.core.padZip
import me.peckb.aoc._2022.calendar.day25.SnafuDigit.Companion.fromChar
import me.peckb.aoc._2022.calendar.day25.SnafuDigit.ZERO
import java.lang.IllegalStateException

class Snafu private constructor(val number: List<SnafuDigit>) {
  infix operator fun plus(other: Snafu): Snafu {
    var carry = ZERO
    val result = StringBuilder()

    number.reversed().padZip(other.number.reversed()) { d1, d2 ->
      val addResult = (d1 ?: ZERO) + (d2 ?: ZERO)
      val addResultWithCarry = addResult.result + carry
      val carrySum = addResultWithCarry.carry + addResult.carry

      if (carrySum.carry != ZERO) throw IllegalStateException("There should never be a double carry")

      result.append(addResultWithCarry.result.c)
      carry = carrySum.result
    }

    if (carry != ZERO) result.append(carry.c)

    return fromString(result.toString().reversed())
  }

  val length = number.size

  override fun toString(): String = number.map { it.c }.joinToString("")

  companion object {
    fun fromString(snafuString: String) = Snafu(snafuString.map { fromChar(it) })
  }
}
