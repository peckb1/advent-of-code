package me.peckb.aoc._2023.calendar.day19

data class XMAS(val x: Int, val m: Int, val a: Int, val s: Int) {
  val value = (x + m + a + s).toLong()

  fun valueOf(variableName: Char): Int {
    return when (variableName) {
      'x' -> x
      'm' -> m
      'a' -> a
      's' -> s
      else -> throw IllegalArgumentException("Unknown Variable Name: $variableName")
    }
  }
}
