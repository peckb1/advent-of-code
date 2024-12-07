package me.peckb.aoc._2024.calendar.day07

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day07 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::calibration) { input ->
    input.filter { check(it, listOf(ADD, MULTIPLY)) }.sumOf { it.value }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::calibration) { input ->
    input.filter { check(it, listOf(ADD, MULTIPLY, CONCAT)) }.sumOf { it.value }
  }

  fun check(calibration: Calibration, operands: List<Operand>): Boolean {
    val (n, m) = calibration.numbers.take(2)
    val otherNumbers = calibration.numbers.drop(2)
    return operands.any { op -> check(calibration.value, listOf(op.invoke(n, m)).plus(otherNumbers), operands) }
  }

  fun check(targetVal: Long, remainingNumbers: List<Long>, operands: List<Operand>) : Boolean {
    if (remainingNumbers.isEmpty())      return false
    if (remainingNumbers.size == 1)      return targetVal == remainingNumbers[0]
    if (remainingNumbers[0] > targetVal) return false

    val (n, m) = remainingNumbers.take(2)
    val otherNumbers = remainingNumbers.drop(2)
    return operands.any { op -> check(targetVal, listOf(op.invoke(n, m)).plus(otherNumbers), operands) }
  }

  private fun calibration(line: String): Calibration {
    return line.split(": ").let { (valueStr, numbersStr) ->
      val numbers = numbersStr.split(" ").map(String::toLong)
      Calibration(valueStr.toLong(), numbers)
    }
  }

  companion object {
    val ADD      = { n: Long, m: Long -> n + m }
    val MULTIPLY = { n: Long, m: Long -> n * m }
    val CONCAT   = { n: Long, m: Long -> "$n$m".toLong() }
  }
}

typealias Operand = (Long, Long) -> Long

data class Calibration(val value: Long, val numbers: List<Long>)
