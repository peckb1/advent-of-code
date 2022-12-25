package me.peckb.aoc._2022.calendar.day25

enum class SnafuDigit(val c: Char) {
  TWO('2') {
    override fun plus(other: SnafuDigit) =
      when (other) {
        TWO          -> MINUS        to ONE
        ONE          -> DOUBLE_MINUS to ONE
        ZERO         -> TWO          to ZERO
        MINUS        -> ONE          to ZERO
        DOUBLE_MINUS -> ZERO         to ZERO
      }.let { AddResult(it.first, it.second) }
  },
  ONE('1') {
    override fun plus(other: SnafuDigit) =
      when (other) {
        TWO          -> DOUBLE_MINUS to ONE
        ONE          -> TWO          to ZERO
        ZERO         -> ONE          to ZERO
        MINUS        -> ZERO         to ZERO
        DOUBLE_MINUS -> MINUS        to ZERO
      }.let { AddResult(it.first, it.second) }
  },
  ZERO('0') {
    override fun plus(other: SnafuDigit) = AddResult(other, ZERO)
  },
  MINUS('-') {
    override fun plus(other: SnafuDigit) =
      when (other) {
        TWO          -> ONE          to ZERO
        ONE          -> ZERO         to ZERO
        ZERO         -> MINUS        to ZERO
        MINUS        -> DOUBLE_MINUS to ZERO
        DOUBLE_MINUS -> TWO          to MINUS
      }.let { AddResult(it.first, it.second) }
  },
  DOUBLE_MINUS('=') {
    override fun plus(other: SnafuDigit) =
      when (other) {
        TWO          -> ZERO         to ZERO
        ONE          -> MINUS        to ZERO
        ZERO         -> DOUBLE_MINUS to ZERO
        MINUS        -> TWO          to MINUS
        DOUBLE_MINUS -> ONE          to MINUS
      }.let { AddResult(it.first, it.second) }
  };

  abstract infix operator fun plus(other: SnafuDigit) : AddResult

  companion object {
    fun fromChar(c: Char): SnafuDigit = values().first { c == it.c }
  }
}

data class AddResult(val result: SnafuDigit, val carry: SnafuDigit)
