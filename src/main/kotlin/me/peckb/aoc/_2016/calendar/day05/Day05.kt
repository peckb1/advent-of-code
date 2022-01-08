package me.peckb.aoc._2016.calendar.day05

import me.peckb.aoc.MD5
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day05 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
  private val md5: MD5
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    var counter = 0L
    var password = ""
    while (password.length < 8) {
      val (c, key) = findWithPrefix(input, counter)
      counter = c
      password += key[5]
    }
    password
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    var counter = 0L
    val password = "--------".toCharArray()
    while (password.contains('-')) {
      val (c, key) = findWithPrefix(input, counter)
      if (key[5].isDigit()) {
        val index = Character.getNumericValue(key[5])
        if (index in 0..7 && password[index] == '-') {
          password[index] = key[6]
        }
      }
      counter = c
    }
    password.joinToString("")
  }

  private fun findWithPrefix(key: String, counterStart: Long): Pair<Long, String> {
    var counter = counterStart
    var md5HexString: String

    do {
      counter++
      val input = "$key${counter}"
      md5HexString = md5.hash(input)
    } while (!md5HexString.startsWith(PREFIX))

    return counter to md5HexString
  }

  companion object {
    const val PREFIX = "00000"
  }
}
