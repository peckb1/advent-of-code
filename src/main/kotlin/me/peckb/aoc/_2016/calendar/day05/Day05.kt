package me.peckb.aoc._2016.calendar.day05

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.math.BigInteger
import java.security.MessageDigest

class Day05 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
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
      md5HexString = BigInteger(1, MD.digest(input.toByteArray())).toHexString()
    } while (!md5HexString.startsWith(PREFIX))

    return counter to md5HexString
  }


  private fun BigInteger.toHexString() = toString(16).padStart(32, '0')

  companion object {
    const val PREFIX = "00000"
    private val MD = MessageDigest.getInstance("MD5")
  }
}
