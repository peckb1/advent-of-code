package me.peckb.aoc._2020.calendar.day18

abstract class PriorityOperator(val priority: Int) {
  abstract val code: Char
  abstract val apply: (Long, Long) -> Long

  override fun toString(): String = code.toString()
}

class Addition(priority: Int) : PriorityOperator(priority) {
  override val code: Char = '+'
  override val apply: (Long, Long) -> Long = Long::plus
}

class Multiplication(priority: Int) : PriorityOperator(priority) {
  override val code: Char = '*'
  override val apply: (Long, Long) -> Long = Long::times
}
