package me.peckb.aoc._2015.calendar.day04

import me.peckb.aoc.MD5
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day04 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
  private val mD5: MD5
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    findWithPrefix(input, "00000")
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    findWithPrefix(input, "000000")
  }

  private fun findWithPrefix(key: String, prefix: String): Long {
    var counter = 0L

    do {
      counter++
      val md5HexString = mD5.hash("$key${counter}")
    } while(!md5HexString.startsWith(prefix))

    return counter
  }
}
