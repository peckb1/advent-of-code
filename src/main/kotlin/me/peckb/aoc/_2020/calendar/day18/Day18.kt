package me.peckb.aoc._2020.calendar.day18

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day18 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  interface PriorityOperator {
    val code: Char
    val priority: Int
    val apply: (Long, Long) -> Long
  }

  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val operators = listOf(createMultiplication(0), createAddition(0))
    input.sumOf { line -> performMaths(line, operators).first }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val operators = listOf(createMultiplication(0), createAddition(1))
    input.sumOf { line -> performMaths(line, operators).first }
  }

  private fun createAddition(priority: Int): PriorityOperator = object : PriorityOperator {
    override val code: Char = '+'
    override val priority: Int = priority
    override val apply: (Long, Long) -> Long = Long::plus
    override fun toString(): String = code.toString()
  }

  private fun createMultiplication(priority: Int): PriorityOperator = object : PriorityOperator {
    override val code: Char = '*'
    override val priority: Int = priority
    override val apply: (Long, Long) -> Long = Long::times
    override fun toString(): String = code.toString()
  }

  private fun performMaths(line: String, operators: List<PriorityOperator>, startIndex: Int = 0): Pair<Long, Int> {
    var index = startIndex
    val validOperatorSet = operators.map { it.code }.toSet()

    var waitingValue: Long? = null
    var waitingOperator: PriorityOperator? = null
    var currentValue: Long? = null
    var currentOperator: PriorityOperator? = null

    fun handleNumber(number: Long) {
      if (waitingValue == null) {
        waitingValue = number
      } else if (currentValue == null) {
        currentValue = number
      } else {
        // we have both a waitingValue and a currentValue
        // DEV NOTE: even though  we have the `!!` below and would also get an exception
        //           the compiler seems smart enough to optimize here since there are mutliple
        //           `!!` for each of these operators
        if (waitingOperator == null || currentOperator == null) {
          throw IllegalStateException("We have a third value, but are missing operators")
        }
        if (currentOperator!!.priority > waitingOperator!!.priority) {
          // the currentOperator is a higher priority
          val temp = currentOperator!!.apply(currentValue!!, number)
          currentValue = temp
          currentOperator = null
        } else {
          // the waiting operator can be applied
          val temp = waitingOperator!!.apply(waitingValue!!, currentValue!!)
          waitingValue = temp
          waitingOperator = currentOperator
          currentValue = number
          currentOperator = null
        }
      }
    }

    while (index < line.length) {
      when (val item = line[index]) {
        in '0'..'9' -> {
          handleNumber(item.digitToInt().toLong())
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
          handleNumber(innerResult)
          index = lastIndex
        }

        ')' -> {
          return waitingOperator!!.apply(waitingValue!!, currentValue!!) to index
        }
      }
      index++
    }

    return waitingOperator!!.apply(waitingValue!!, currentValue!!) to index
  }
}
