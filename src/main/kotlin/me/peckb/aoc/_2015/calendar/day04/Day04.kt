package me.peckb.aoc._2015.calendar.day04

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.security.MessageDigest
import javax.inject.Inject

class Day04 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    private val MD = MessageDigest.getInstance("MD5")
  }

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
      MD.update("$key${counter}".toByteArray())
      val md5HexString = MD.digest().toHexString()
    } while(!md5HexString.startsWith(prefix))

    return counter
  }

  private fun ByteArray.toHexString() =
    this.joinToString("") { it.toString(radix = 16).padStart(2, '0') }
}
