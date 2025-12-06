package me.peckb.aoc._2025.calendar.day06

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.text.trim

class Day06 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  companion object {
    const val PRODUCT = '*'
    const val SUM = '+'
  }

  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val numbersInEquations = mutableListOf<MutableList<Long>>()
    val operationsInEquations = mutableListOf<Char>()

    val newList = { i : Int -> mutableListOf<Long>().also {
      numbersInEquations.add(i, it)
    }}

    input.forEach { line ->
      line.trim().split("\\s+".toRegex()).forEachIndexed { index, dataElement ->
        val number = dataElement.toLongOrNull()
          if (number != null) {
            numbersInEquations.getOrElse(index) { i -> newList(i) }.add(number)
          } else {
            operationsInEquations.add(index, dataElement.first())
          }
      }
    }

    numbersInEquations.zip(operationsInEquations).sumOf { (numbers, operation) ->
      when (operation) {
        SUM     -> numbers.reduce(Long::plus)
        PRODUCT -> numbers.reduce(Long::times)
        else    -> throw IllegalArgumentException("Illegal operation $operation")
      }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val numberLines = mutableListOf<String>()
    val operationData = mutableListOf<Pair<Char, Int>>()

    var combinedTotal = 0L

    // first we need to find out column spacing and operand data
    input.forEach { line ->
      val firstChar = line.first()
      if (firstChar == PRODUCT || firstChar == SUM) {
        var index = 0
        while(index < line.length) {
          val operation = line[index]
          if (index == line.length - 1) {
            // end row so our "length" should use the longest length line
            val length = (numberLines.maxOf { it.length } - line.length + 1)
            // and once we have our length, track the operation
            // since we're at the end of our input string we don't have a buffer space
            operationData.add(operation to length)
            index++
          } else {
            // not the end row - go char by char until we find the next operand
            var length = 0
            do { val next = line[index + ++length] } while (next == ' ')
            // and once we have our length, track the operation
            // there is always a buffer space between equations so offset by that
            operationData.add(operation to length - 1)
            index += length
          }
        }
      } else {
        numberLines.add(line)
      }

      // now that we have our operand and column length we can do math!
      var bufferIndex = 0
      operationData.forEach { (operation, dataSize) ->
        var total = if (operation == PRODUCT) 1L else 0L
        var digitIndex = 0

        while (digitIndex < dataSize) {
          val digit = numberLines.map { line ->
            // grab the number to use, the blank space in the string, ...
            // or the string wasn't that long and pretend we have trailing spaces
            line.getOrElse(bufferIndex + dataSize - digitIndex - 1) { ' ' }
          }.joinToString(separator = "").trim().toLong()

          if (operation == PRODUCT) total *= digit else total += digit

          digitIndex++
        }

        // don't forget the buffer space when incrementing
        bufferIndex += dataSize + 1
        // and track the total math sum
        combinedTotal += total
      }
    }

    combinedTotal
  }
}
