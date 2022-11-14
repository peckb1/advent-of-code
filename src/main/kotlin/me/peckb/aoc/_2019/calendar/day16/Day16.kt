package me.peckb.aoc._2019.calendar.day16

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.abs
import kotlin.math.absoluteValue

class Day16 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val inputList = input.map { it.digitToInt() }
    val maxSize = inputList.size
    val basePattern = listOf(0, 1, 0, -1)
    val actualPatterns = (1 .. inputList.size).map { rotateCount ->
      var basePatternIndex = 0
      List(maxSize) { index ->
        val trueIndex = index + 1
        if (trueIndex % rotateCount == 0) {
          basePatternIndex = (basePatternIndex + 1) % basePattern.size
        }
        basePattern[basePatternIndex]
      }
    }

    fun iterate(inputData: List<Int>): List<Int> {
      return (0 until maxSize).map { patternIndex ->
        val onesDigit = (0 until maxSize).sumOf { inputIndex ->
          actualPatterns[patternIndex][inputIndex] * inputData[inputIndex]
        } % 10
        abs(onesDigit)
      }
    }

    var currentInput = inputList
    repeat(100) {
      currentInput = iterate(currentInput)
    }

    currentInput.take(8).joinToString("").toInt()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val inputList = input.map { it.digitToInt() }

    // grab the offset
    val offset = input.take(7).toInt()
    // since our input number starts with a 5, we know that our data will be past the halfway point
    // DEV NOTE: this requires some "cleverness"
    // also since the list is so large, we should mutate it, instead of creating new versions
    val giantInput = (offset until 10_000 * input.length)
      .map { inputList[it % input.length] }
      .toMutableList()
    repeat(100) {
      val indicesStartingAtTheEnd = giantInput.indices.reversed()
      // by going backwards we can keep track of our values, since the previous one is
      // always the sum of the following digits
      // DEV NOTE: more "cleverness" here
      indicesStartingAtTheEnd.fold(0) { acc, index ->
        val mySum = giantInput[index] + acc
        val nextData = abs(mySum % 10)
        nextData.also { giantInput[index] = it }
      }
    }
    giantInput.take(8).joinToString("").toInt()
  }
}
