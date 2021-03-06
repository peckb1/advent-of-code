package me.peckb.aoc._2016.calendar.day14

import me.peckb.aoc.MD5
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day14 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
  private val mD5: MD5
) {
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
    var hash = mD5.hash(key)
    repeat(repeats) {
      hash = mD5.hash(hash)
    }
    return hash
  }

  companion object {
    const val PART_ONE_REPEATS = 0
    const val PART_TWO_REPEATS = 2016
  }
}
