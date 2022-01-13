package me.peckb.aoc._2017.calendar.day18

import arrow.core.Either

sealed class Instruction {
  data class Send(val register: Char) : Instruction()
  data class Set(val register: Char, val value: Either<Int, Char>) : Instruction()
  data class Add(val register: Char, val value: Either<Int, Char>) : Instruction()
  data class Multiply(val register: Char, val value: Either<Int, Char>) : Instruction()
  data class Modulo(val register: Char, val value: Either<Int, Char>) : Instruction()
  data class Receive(val register: Char) : Instruction()
  data class JumpGreaterThanZero(val value: Either<Int, Char>, val steps: Either<Int, Char>) : Instruction()
}
