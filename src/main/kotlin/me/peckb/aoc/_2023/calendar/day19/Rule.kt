package me.peckb.aoc._2023.calendar.day19

sealed interface Rule {
  fun apply(xmas: XMAS): String?

  class GreaterThan(val variableName: Char, val value: Int, val ifTrue: String) : Rule {
    override fun apply(xmas: XMAS) = ifTrue.takeIf { xmas.valueOf(variableName) > value }
  }

  class LessThan(val variableName: Char, val value: Int, val ifTrue: String) : Rule {
    override fun apply(xmas: XMAS) = ifTrue.takeIf { xmas.valueOf(variableName) < value }
  }

  class Static(val result: String) : Rule {
    override fun apply(xmas: XMAS) = result
  }
}
