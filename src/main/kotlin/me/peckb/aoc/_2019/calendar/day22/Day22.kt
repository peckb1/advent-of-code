package me.peckb.aoc._2019.calendar.day22

import me.peckb.aoc._2019.calendar.day22.Day22.Shuffle.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.math.BigInteger
import java.math.BigInteger.*
import kotlin.IllegalStateException
import kotlin.math.absoluteValue

class Day22 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::shuffle) { input ->
    var cards = List(10007) { it }

    input.forEach { shuffle ->
      cards = when (shuffle) {
        is Cut -> cards.cut(shuffle.depth)
        DealIntoNewStack -> cards.asReversed()
        is DealWithIncrement -> cards.deal(shuffle.increment)
      }
    }

    cards.indexOf(2019)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::shuffle) { input ->
    val zero = ZERO
    val one = ONE
    val two = TWO
    val numCards = 119315717514047.toBigInteger()
    val shuffles = 101741582076661.toBigInteger()

    val memory = arrayOf(one, zero)

    input.toList().asReversed().forEach { shuffle ->
      when (shuffle) {
        is Cut -> {
          memory[1] += shuffle.bigDepth
        }
        DealIntoNewStack -> {
          memory[0] = memory[0].negate()
          memory[1] = (memory[1].inc()).negate()
        }
        is DealWithIncrement -> {
          shuffle.bigIncrement.modPow(numCards - two, numCards).also {
            memory[0] *= it
            memory[1] *= it
          }
        }
      }
      memory[0] %= numCards
      memory[1] %= numCards
    }
    val power = memory[0].modPow(shuffles, numCards)

    val findIndex = (power * 2020.toBigInteger())
    val memOneResult = (memory[1] * (power + numCards.dec()))
    val memTwoResult = (memory[0].dec()).modPow(numCards - two, numCards)

    (findIndex + (memOneResult * memTwoResult)).mod(numCards).toLong()
  }

  private fun List<Int>.cut(depth: Int): List<Int> = when {
    depth > 0 -> this.drop(depth) + this.take(depth)
    depth < 0 -> depth.absoluteValue.let { this.takeLast(it) + this.dropLast(it) }
    else -> this
  }

  private fun List<Int>.deal(increment: Int): List<Int> {
    val newCards = Array(size) { -1 }

    forEachIndexed { cardIndex, card ->
      val newIndex = (cardIndex * increment) % size
      newCards[newIndex] = card
    }

    return newCards.toList()
  }

  private fun shuffle(line: String): Shuffle {
    val parts = line.split(" ")
    return when (parts.first()) {
      "cut" -> Cut(parts[1].toInt())
      "deal" -> when (parts[1]) {
        "into" -> DealIntoNewStack
        "with" -> DealWithIncrement(parts[3].toInt())
        else -> throw IllegalStateException("Unknown Deal [$line]")
      }
      else -> throw IllegalStateException("Unknown Shuffle [$line]")
    }
  }

  sealed class Shuffle {
    object DealIntoNewStack : Shuffle()
    data class Cut(val depth: Int, val bigDepth: BigInteger = depth.toBigInteger()) : Shuffle()
    data class DealWithIncrement(val increment: Int, val bigIncrement: BigInteger = increment.toBigInteger()) : Shuffle()
  }
}
