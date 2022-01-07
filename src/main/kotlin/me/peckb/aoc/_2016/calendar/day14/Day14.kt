package me.peckb.aoc._2016.calendar.day14

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.math.BigInteger
import java.security.MessageDigest

class Day14 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { salt ->
    val superHashes = generatePads(salt, PART_ONE_REPEATS)
    superHashes.last()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { salt ->
    val superHashes = generatePads(salt, PART_TWO_REPEATS)
    superHashes.last()
  }

  private fun generatePads(salt: String, repeats: Int): List<Int> {
    val hashes = mutableMapOf<Int, String>()
    val superHashes = mutableListOf<Int>()
    var index = 0
    while(superHashes.size < 64) {
      val key = "$salt$index"
      val hash = hashes[index] ?: md5(key, repeats).also { hashes[index] = it }
      val matchResults = "(.)\\1{2}".toRegex().findAll(hash)
      matchResults.firstOrNull()?.let{ match ->
        val character = match.value[0]
        (index + 1..index + 1000).firstOrNull { followUpIndex ->
          val followUpKey = "$salt$followUpIndex"
          val followUpHash = hashes[followUpIndex] ?: md5(followUpKey, repeats).also { hashes[followUpIndex] = it }
          "(.)\\1{4}".toRegex().findAll(followUpHash).any { it.value[0] == character }
        }?.let {
          superHashes.add(index)
        }
      }
      index++
    }
    return superHashes
  }

  private fun md5(key: String, repeats: Int): String {
    var hash = BigInteger(1, MD.digest(key.toByteArray())).toHexString()
    repeat(repeats) {
      hash = BigInteger(1, MD.digest(hash.toByteArray())).toHexString()
    }
    return hash
  }

  private fun BigInteger.toHexString() = toString(16).padStart(32, '0')

  companion object {
    const val PART_ONE_REPEATS = 0
    const val PART_TWO_REPEATS = 2016
    private val MD = MessageDigest.getInstance("MD5")
  }
}
