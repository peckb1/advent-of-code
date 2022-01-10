package me.peckb.aoc._2017.calendar.day10

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.min

class Day10 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val lengths = input.split(",").map { it.trim().toInt() }
    val data = runInput(lengths, 1)

    data[0] * data[1]
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val lengths = input.map { it.code }.plus(listOf(17, 31, 73, 47, 23))
    val data = runInput(lengths, 64)

    data.chunked(16)
      .map { it.reduce { acc, next -> acc.xor(next) } }
      .joinToString("") {
        val hex = it.toString(16)
        if (hex.length < 2) "0$hex" else hex
      }
  }

  private fun runInput(lengths: List<Int>, times: Int): List<Int> {
    val data = (0..255).map { it }.toMutableList()

    var currentPosition = 0
    var skipSize = 0

    repeat(times) {
      lengths.forEach loop@{ length ->
        if (length > data.size) return@loop

        val extra = if (currentPosition + length > data.size) (currentPosition + length - data.size) else 0
        val endIndex = min(currentPosition + length - 1, data.size - 1)
        val toReverse = data.slice(currentPosition..endIndex).plus(data.slice(0 until extra))

        var changeCounter = 0
        toReverse.reversed().forEach {
          val indexToUpdate = (currentPosition + changeCounter) % data.size
          data[indexToUpdate] = it
          changeCounter++
        }

        currentPosition += length + skipSize
        currentPosition %= data.size
        skipSize++
      }
    }

    return data
  }
}
