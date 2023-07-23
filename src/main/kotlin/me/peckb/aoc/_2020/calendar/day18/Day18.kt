package me.peckb.aoc._2020.calendar.day18

import arrow.core.Either
import me.peckb.aoc._2020.calendar.day18.Day18.Operator.ADDITION
import me.peckb.aoc._2020.calendar.day18.Day18.Operator.MULTIPLICATION
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day18 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  enum class Operator(val code: Char) {
    ADDITION('+'), MULTIPLICATION('*');

    companion object {
      fun fromCode(code: Char) = Operator.values().first { it.code == code }
    }
  }

  interface PriorityOperator {
    val code: Char
    val priority: Int
    val apply: (Long, Long) -> Long
  }

  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    input.map {
      performMaths(it).first
    }.sum()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val operators = listOf(
      object : PriorityOperator {
        override val code: Char = '*'
        override val priority: Int = 0
        override val apply: (Long, Long) -> Long = Long::times
        override fun toString(): String = code.toString()
      },
      object : PriorityOperator {
        override val code: Char = '+'
        override val priority: Int = 1
        override val apply: (Long, Long) -> Long = Long::plus
        override fun toString(): String = code.toString()
      }
    )
    input.sumOf { line ->
      performMaths(line, operators).first
    }
  }

  private fun performMaths(line: String, operators: List<PriorityOperator>, startIndex: Int = 0): Pair<Long, Int> {
    var index = startIndex
    val validOperatorSet = operators.map { it.code }.toSet()

    var waitingValue: Long? = null
    var waitingOperator: PriorityOperator? = null
    var currentValue: Long? = null
    var currentOperator: PriorityOperator? = null

    while(index < line.length) {
      when (val item = line[index]) {
        in '0'..'9' -> {
          if (waitingValue == null) {
            waitingValue = item.digitToInt().toLong()
          } else if (currentValue == null) {
            currentValue = item.digitToInt().toLong()
          } else {
            // we have both a waitingValue and a currentValue
            if (waitingOperator == null || currentOperator == null) {
              throw IllegalStateException("We have a third value, but are missing operators")
            }
            if (currentOperator.priority > waitingOperator.priority) {
              // the currentOperator is a higher priority
              val temp = currentOperator.apply(currentValue, item.digitToInt().toLong())
              currentValue = temp
              currentOperator = null
            } else {
              // the waiting operator can be applied
              val temp = waitingOperator.apply(waitingValue, currentValue)
              waitingValue = temp
              waitingOperator = currentOperator
              currentValue = item.digitToInt().toLong()
              currentOperator = null
            }
          }
        }
        in validOperatorSet -> {
          if (waitingOperator == null) {
            waitingOperator = operators.find { it.code == item }
          } else if (currentOperator == null) {
            currentOperator = operators.find { it.code == item }
          } else {
            throw IllegalStateException("Third operator needing to be stored")
          }
        }
        '(' -> {
          val (innerResult, lastIndex) = performMaths(line, operators, index + 1)
          if (waitingValue == null) {
            waitingValue = innerResult
          } else if (currentValue == null) {
            currentValue = innerResult
          } else {
            // we have both a waitingValue and a currentValue
            if (waitingOperator == null || currentOperator == null) {
              throw IllegalStateException("We have a third value, but are missing operators")
            }
            if (currentOperator.priority > waitingOperator.priority) {
              // the currentOperator is a higher priority
              val temp = currentOperator.apply(currentValue, innerResult)
              currentValue = temp
              currentOperator = null
            } else {
              // the waiting operator can be applied
              val temp = waitingOperator.apply(waitingValue, currentValue)
              waitingValue = temp
              waitingOperator = currentOperator
              currentValue = innerResult
              currentOperator = null
            }
          }
          index = lastIndex
        }
        ')' -> {
          if (currentValue != null && waitingOperator != null && waitingValue != null) {
            return waitingOperator.apply(waitingValue, currentValue) to index
          } else {
            throw IllegalStateException("End of a group with no values found")
          }
        }
      }
      index++
    }

    if (waitingValue != null && waitingOperator != null && currentValue != null) {
      return waitingOperator.apply(waitingValue, currentValue) to index
    }

    throw IllegalStateException("End of a block but no operation was waiting")
  }

  private fun performMaths(line: String, startIndex: Int = 0): Pair<Long, Int> {
    var index = startIndex
    var result = 0L
    var lastOperator: Operator? = null
    var lastValue: Long? = null

    while(index < line.length) {
      when(val item = line[index]) {
        in '0'..'9' -> {
          if (lastOperator != null && lastValue != null) {
            result = when (lastOperator) {
              ADDITION -> lastValue + item.digitToInt()
              MULTIPLICATION -> lastValue * item.digitToInt()
            }
            lastValue = result
            lastOperator = null
          } else {
            lastValue = item.digitToInt().toLong()
          }
        }
        in setOf('+', '*') -> {
          lastOperator = Operator.fromCode(item)
        }
        '(' -> {
          val (lastResult, newIndex) = performMaths(line, index + 1)
          result = when (lastOperator) {
            ADDITION -> (lastValue ?: 0) + lastResult
            MULTIPLICATION -> (lastValue ?: 1) * lastResult
            null -> lastResult
          }
          lastValue = result
          lastOperator = null
          index = newIndex
        }
        ')' -> {
          return result to index
        }
        ' ' -> { /* skip */ }
      }
      index++
    }

    return result to index
  }
}
