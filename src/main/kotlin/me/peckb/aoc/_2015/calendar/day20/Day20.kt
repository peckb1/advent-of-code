package me.peckb.aoc._2015.calendar.day20

import arrow.core.foldLeft
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.pow
import kotlin.math.sqrt

class Day20 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val tooFar = input.toInt() / 10
    (2 until tooFar step 2).first { sumOfFactors(it) >= tooFar }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val cap = input.toInt()
    // chosen "randomly" to limit our bounds
    val array = Array(1_000_000) { 0 }

    (1..cap).first { elf ->
      (elf..(elf*50) step elf).forEach { house ->
        if (house < array.size) {
          array[house] += elf * 11
        }
      }
      array[elf] >= cap
    }
  }

  private fun sumOfFactors(num: Int): Int {
    return primeFactors(num).groupBy { it }.foldLeft(1.0) { acc, next ->
      acc * ((next.key.toDouble().pow(next.value.size.toDouble() + 1.0) - 1) / (next.key - 1))
    }.toInt()
  }

  @Suppress("unused")
  /**
   * Leaving in for historical learnings. Originally I just went piece meal finding the actual
   * factors, and summing them up. And then after getting the stars, found some "more memory,
   * less time" solutions
   */
  private fun factorsOfNumber(num: Int): MutableList<Int> {
    val factors = mutableListOf<Int>()
    if (num < 1) return factors
    (1..(num / 2))
      .filter { num % it == 0 && num / it <= 50 }
      .forEach { factors.add(it) }
    factors.add(num)
    return factors
  }

  private fun primeFactors(number: Int): ArrayList<Int> {
    val arr: ArrayList<Int> = arrayListOf()
    var n = number
    while (n % 2 == 0) {
      arr.add(2)
      n /= 2
    }
    val squareRoot = sqrt(n.toDouble()).toInt()

    for (i in 3..squareRoot step 2) {
      while (n % i == 0) {
        arr.add(i)
        n /= i
      }
    }

    if (n > 2) arr.add(n)

    return arr
  }
}
