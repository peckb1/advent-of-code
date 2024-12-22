package me.peckb.aoc._2024.calendar.day22

import arrow.core.Tuple4
import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day22 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::secretNumber) { input ->
    input.sumOf { number ->
      (0 until 2000).fold(number) { n, _ -> nextNumber(n) }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::secretNumber) { input ->
    val data = input.toList()

    val solutions = mutableMapOf<Tuple4<Long, Long, Long, Long>, Long>()

    data.forEach { number->
      val buyerNumbers = sequence {
        var n = number
        while (true) {
          yield(n)
          n = nextNumber(n)
        }
      }

      val mySequences = mutableSetOf<Tuple4<Long, Long, Long, Long>>()

      buyerNumbers
        .map { it % 10 }
        .windowed(5)
        .take(2000)
        .forEach { (a, b, c, d, e) ->
          val k = Tuple4(b - a, c - b, d - c, e - d)
          if (k !in mySequences) {
            solutions.merge(k, e, Long::plus)
            mySequences.add(k)
          }
        }
    }

    solutions.values.max()
  }

  private fun nextNumber(number: Long): Long {
    var n = number

    // step one
    val a = n * 64   // Calculate the result of multiplying the secret number by 64.
    n = n xor a      // Then, mix this result into the secret number.
    n %= 16_777_216  // Finally, prune the secret number.

    // step two
    val b = n / 32   // Calculate the result of dividing the secret number by 32.
    n = n xor b      // Then, mix this result into the secret number.
    n %= 16_777_216  // Finally, prune the secret number.

    // step three
    val c = n * 2048 // Calculate the result of multiplying the secret number by 2048.
    n = n xor c      // Then, mix this result into the secret number.
    n %= 16_777_216  // Finally, prune the secret number.

    return n
  }

  private fun secretNumber(line: String) = line.toLong()
}
