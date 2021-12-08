package me.peckb.aoc._2021.calendar.day08

import me.peckb.aoc._2021.generators.InputGenerator
import javax.inject.Inject

class Day08 @Inject constructor(private val generatorFactory: InputGenerator.InputGeneratorFactory) {
  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::day08) { input ->
    input.sumOf { (_, outputValue) ->
      outputValue.count {
        when (it.length) {
          2, 3, 4, 7 -> true
          else -> false
        }
      }
    }
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day08) { input ->
    val realSignals: Map<String, String> = mapOf(
      "abcefg" to "0",
      "cf" to "1",
      "acdeg" to "2",
      "acdfg" to "3",
      "bcdf" to "4",
      "abdfg" to "5",
      "abdefg" to "6",
      "acf" to "7",
      "abcdefg" to "8",
      "abcdfg" to "9",
    )

    val ouputResult = input.map { (patterns, outputValues) ->
      val wireMatches: Map<Char, MutableList<Char>> = mapOf(
        'a' to mutableListOf('a', 'b', 'c', 'd', 'e', 'f', 'g'),
        'b' to mutableListOf('a', 'b', 'c', 'd', 'e', 'f', 'g'),
        'c' to mutableListOf('a', 'b', 'c', 'd', 'e', 'f', 'g'),
        'd' to mutableListOf('a', 'b', 'c', 'd', 'e', 'f', 'g'),
        'e' to mutableListOf('a', 'b', 'c', 'd', 'e', 'f', 'g'),
        'f' to mutableListOf('a', 'b', 'c', 'd', 'e', 'f', 'g'),
        'g' to mutableListOf('a', 'b', 'c', 'd', 'e', 'f', 'g'),
      )

      val twoThreeFives = mutableListOf<String>()


      (patterns + outputValues).sortedBy { it.length }.forEach { encoding ->
        when(encoding.length) {
          // #1
          2 -> {
            wireMatches['c']?.removeAll { !encoding.contains(it) }
            wireMatches['f']?.removeAll { !encoding.contains(it) }

            wireMatches['a']?.removeAll { encoding.contains(it) }
            wireMatches['b']?.removeAll { encoding.contains(it) }
            wireMatches['d']?.removeAll { encoding.contains(it) }
            wireMatches['e']?.removeAll { encoding.contains(it) }
            wireMatches['g']?.removeAll { encoding.contains(it) }
          }
          // # 7
          3 -> {
            wireMatches['a']?.removeAll { !encoding.contains(it) }
            wireMatches['c']?.removeAll { !encoding.contains(it) }
            wireMatches['f']?.removeAll { !encoding.contains(it) }

            wireMatches['b']?.removeAll { encoding.contains(it) }
            wireMatches['d']?.removeAll { encoding.contains(it) }
            wireMatches['e']?.removeAll { encoding.contains(it) }
            wireMatches['g']?.removeAll { encoding.contains(it) }
          }
          // #4
          4 -> {
            wireMatches['b']?.removeAll { !encoding.contains(it) }
            wireMatches['c']?.removeAll { !encoding.contains(it) }
            wireMatches['d']?.removeAll { !encoding.contains(it) }
            wireMatches['f']?.removeAll { !encoding.contains(it) }

            wireMatches['a']?.removeAll { encoding.contains(it) }
            wireMatches['e']?.removeAll { encoding.contains(it) }
            wireMatches['g']?.removeAll { encoding.contains(it) }
          }
          // #2, #3, or #5
          5 -> {
            if ((wireMatches['c']?.all { encoding.contains(it) }) == true && (wireMatches['f']?.all { encoding.contains(it) }) == true) {
              // we have a #3
              wireMatches['a']?.removeAll { !encoding.contains(it) }
              wireMatches['c']?.removeAll { !encoding.contains(it) }
              wireMatches['d']?.removeAll { !encoding.contains(it) }
              wireMatches['f']?.removeAll { !encoding.contains(it) }
              wireMatches['g']?.removeAll { !encoding.contains(it) }

              wireMatches['b']?.removeAll { encoding.contains(it) }
              wireMatches['e']?.removeAll { encoding.contains(it) }
            }
          }
          // #0, #6, or #9
          6 -> {
            if (!((wireMatches['c']?.all { encoding.contains(it) }) == true && (wireMatches['f']?.all { encoding.contains(it) }) == true)) {
              // we have a #6
              wireMatches['a']?.removeAll { !encoding.contains(it) }
              wireMatches['b']?.removeAll { !encoding.contains(it) }
              wireMatches['d']?.removeAll { !encoding.contains(it) }
              wireMatches['e']?.removeAll { !encoding.contains(it) }
              wireMatches['f']?.removeAll { !encoding.contains(it) }
              wireMatches['g']?.removeAll { !encoding.contains(it) }

              wireMatches['c']?.removeAll { encoding.contains(it) }
            }
          }
          // #8
          7 -> {
            // every light is on, not really much help ...
          }
        }
      }

      val mappings = wireMatches
        .mapValues { (_, v) -> v.first() }
        .entries.associate{(k,v)-> v to k}

      val numbers = outputValues.map { outputValue ->
        val sortedSignal = outputValue.toCharArray().map { mappings[it]!! }.sorted().joinToString("")
        val x = realSignals[sortedSignal]
        if (x == null) {
          print("a")
        }
        x!!
      }

      numbers.joinToString("").toInt()
    }

    ouputResult.sum()
  }

  private fun day08(line: String): Measurement {
    val (patterns, output) = line.split("|").map { it.trim() }.map { it.split(" ") }
    return Measurement(patterns, output)
  }

  // private data class SignalPattern(val pattern: String)

  private data class Measurement(val patterns: List<String>, val outputValue: List<String>)
}
