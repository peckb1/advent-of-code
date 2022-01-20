package me.peckb.aoc._2018.calendar.day12

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day12 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val data = input.toList()
    var state = data.first().substringAfter("initial state: ")
    val instructions = data.drop(2).associate { it.toInstruction() }

    val iterations = 20
    repeat(iterations) {
      state = "...$state...".windowed(5).joinToString("") { key -> instructions[key] ?: "." }
    }

    count(state, iterations)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val data = input.toList()
    var state = data.first().substringAfter("initial state: ")
    val instructions = data.drop(2).associate { it.toInstruction() }

    var iterations = 0
    var lastCount = 0L
    var delta = 0L
    var foundSteadyState = false

    while(!foundSteadyState) {
      iterations++
      state = "...$state...".windowed(5).joinToString("") { key -> instructions[key] ?: "." }
      val count = count(state, iterations)
      if (count - lastCount == delta) {
        foundSteadyState = true
      }
      delta = count - lastCount
      lastCount = count
    }

    // once we have our steady state, each iteration just adds `delta` points
    // so find out how many iterations are between us and the target
    // and add a `delta for each one
    (50_000_000_000 - iterations) * delta + lastCount
  }

  private fun count(state: String, iterations: Int): Long {
    return state.withIndex().sumOf { (i, c) ->
      if (c == '#') {
        (i - iterations).toLong()
      } else {
        0L
      }
    }
  }

  private fun String.toInstruction(): Pair<String, String> {
    val (key, value) = split(" => ")
    return key to value
  }
}
