package me.peckb.aoc._2023.calendar.day01

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day01 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    input.map { lineFromFile ->
      val digits = lineFromFile.mapNotNull { character ->
        if (character.isDigit()) {
          character.digitToInt()
        } else {
          null
        }
      }

      val first = digits.first()
      val last = digits.last()

      "$first$last".toInt()
    }.sum()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    input.map { lineFromFile ->
      val first = listOf(
        lineFromFile.indexOf("one") to 1,
        lineFromFile.indexOf("two") to 2,
        lineFromFile.indexOf("three") to 3,
        lineFromFile.indexOf("four") to 4,
        lineFromFile.indexOf("five") to 5,
        lineFromFile.indexOf("six") to 6,
        lineFromFile.indexOf("seven") to 7,
        lineFromFile.indexOf("eight") to 8,
        lineFromFile.indexOf("nine") to 9,

        lineFromFile.indexOf('1') to 1,
        lineFromFile.indexOf('2') to 2,
        lineFromFile.indexOf('3') to 3,
        lineFromFile.indexOf('4') to 4,
        lineFromFile.indexOf('5') to 5,
        lineFromFile.indexOf('6') to 6,
        lineFromFile.indexOf('7') to 7,
        lineFromFile.indexOf('8') to 8,
        lineFromFile.indexOf('9') to 9,
      ).sortedBy { (index, _) -> index }
        .first { (index, _) -> index >= 0 }
        .second

      val last = listOf(
        lineFromFile.lastIndexOf("one") to 1,
        lineFromFile.lastIndexOf("two") to 2,
        lineFromFile.lastIndexOf("three") to 3,
        lineFromFile.lastIndexOf("four") to 4,
        lineFromFile.lastIndexOf("five") to 5,
        lineFromFile.lastIndexOf("six") to 6,
        lineFromFile.lastIndexOf("seven") to 7,
        lineFromFile.lastIndexOf("eight") to 8,
        lineFromFile.lastIndexOf("nine") to 9,

        lineFromFile.lastIndexOf('1') to 1,
        lineFromFile.lastIndexOf('2') to 2,
        lineFromFile.lastIndexOf('3') to 3,
        lineFromFile.lastIndexOf('4') to 4,
        lineFromFile.lastIndexOf('5') to 5,
        lineFromFile.lastIndexOf('6') to 6,
        lineFromFile.lastIndexOf('7') to 7,
        lineFromFile.lastIndexOf('8') to 8,
        lineFromFile.lastIndexOf('9') to 9,
      ).sortedBy { (index, _) -> index }
        .last { (index, _) -> index >= 0 }
        .second

      "$first$last".toInt()
    }.sum()
  }
}
