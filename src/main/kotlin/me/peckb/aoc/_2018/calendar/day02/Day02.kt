package me.peckb.aoc._2018.calendar.day02

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day02 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    var twos = 0
    var threes = 0
    input.forEach { word ->
      val characters = word.groupBy { it }
      if (characters.any { it.value.size == 2 }) twos++
      if (characters.any { it.value.size == 3 }) threes++
    }
    twos * threes
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val words = input.toList()

    var wordOneIndex = -1
    var wordTwoIndex = -1

    words.indices.find { i1 ->
      wordOneIndex = i1
      val wordOne = words[i1]
      (i1 + 1 until words.size).firstOrNull { i2 ->
        wordTwoIndex = i2
        wordOne.indices.count { index -> wordOne[index] != words[i2][index] } == 1
      } != null
    }

    val wordOne = words[wordOneIndex]
    val wordTwo = words[wordTwoIndex]

    wordOne.filterIndexed { index, c ->
      wordTwo[index] == c
    }
  }
}
