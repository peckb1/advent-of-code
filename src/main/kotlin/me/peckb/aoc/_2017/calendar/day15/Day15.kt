package me.peckb.aoc._2017.calendar.day15

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day15 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::generator) { input ->
    val (generatorA, generatorB) = input.toList()

    var counter = 0
    repeat(40_000_000) {
      val nextA = generatorA.next() and LOWER_16_BIT_GRABBER
      val nextB = generatorB.next() and LOWER_16_BIT_GRABBER
      if (nextA == nextB) counter++
    }

    counter
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::generator) { input ->
    val (generatorA, generatorB) = input.toList()

    var counter = 0
    repeat(5_000_000) {
      val nextA = generatorA.magicNext() and LOWER_16_BIT_GRABBER
      val nextB = generatorB.magicNext() and LOWER_16_BIT_GRABBER

      if (nextA == nextB) counter++
    }

    counter
  }

  private fun generator(line: String): Generator {
    val parts = line.split(" ")
    val id = parts[1]
    val start = parts[4].toLong()

    val (factor, magicMultiple) = when (id) {
      "A" -> A_FACTOR to 4L
      "B" -> B_FACTOR to 8L
      else -> throw IllegalArgumentException("Unknown Generator $line")
    }

    return Generator(id, start, factor, magicMultiple)
  }

  data class Generator(
    private val id: String,
    private val start: Long,
    private val factor: Long,
    private val magicMultiple: Long
  ) {
    var previous = start

    fun next() = ((previous * factor) % DIVISOR).also { previous = it }

    fun magicNext(): Long {
      var next = next()
      while (next % magicMultiple != 0L) {
        next = next()
      }
      return next
    }
  }

  companion object {
    const val LOWER_16_BIT_GRABBER = 65535L
    const val A_FACTOR = 16807L
    const val B_FACTOR = 48271L
    const val DIVISOR = 2147483647L
  }
}
