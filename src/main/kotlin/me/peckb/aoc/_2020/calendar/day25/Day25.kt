package me.peckb.aoc._2020.calendar.day25

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day25 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read {
    val (cardPublicKey, doorPublicKey) = it.toList().map { it.toLong() }
    val cardLoopSize = findLoopSize(cardPublicKey)

    findEncryptionKey(doorPublicKey, cardLoopSize)
  }

  private fun findLoopSize(publicKey: Long): Int {
    var value = 1L
    var loopSize = 0

    while(value != publicKey) {
      loopSize++
      value = (value * 7L) % 2020_12_27L
    }

    return loopSize
  }

  private fun findEncryptionKey(publicKey: Long, loopSize: Int): Long {
    var value = 1L
    repeat(loopSize) { value = (value * publicKey) % 2020_12_27L }
    return value
  }
}
