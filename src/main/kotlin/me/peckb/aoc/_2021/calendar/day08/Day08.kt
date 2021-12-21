package me.peckb.aoc._2021.calendar.day08

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day08 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    private const val ALL_LETTERS = "abcdefg"
    private val NUMBER_TO_SIGNAL_CODE = mapOf(
      '0' to "abcefg",
      '1' to "cf",
      '2' to "acdeg",
      '3' to "acdfg",
      '4' to "bcdf",
      '5' to "abdfg",
      '6' to "abdefg",
      '7' to "acf",
      '8' to "abcdefg",
      '9' to "abcdfg"
    )
    private val SIGNAL_CODE_TO_NUMBER = NUMBER_TO_SIGNAL_CODE.entries.associate { (k, v) -> v to k }
  }

  fun findUniqueNumbers(fileName: String) = generatorFactory.forFile(fileName).readAs(::measurement) { input ->
    val wantedValues = listOf(2, 3, 4, 7)
    input.sumOf { (_, outputValue) ->
      outputValue.count { wantedValues.contains(it.length) }
    }
  }

  fun sumAllOutputs(fileName: String) = generatorFactory.forFile(fileName).readAs(::measurement) { input ->
    val outputResult = input.map { (patterns, outputValues) ->
      val wirePossibilities = ALL_LETTERS.associateWith { ALL_LETTERS.toMutableList() }

      fun String.hasEveryPossibilityFor(possibility: Char) =
        wirePossibilities[possibility]?.all { this.contains(it) } == true

      patterns.sortedBy { it.length }.forEach { encoding ->
        when(encoding.length) {
          2 -> wirePossibilities.removeValuesFor('1', encoding)
          3 -> wirePossibilities.removeValuesFor('7', encoding)
          4 -> wirePossibilities.removeValuesFor('4', encoding)
          5 -> if (encoding.hasEveryPossibilityFor('c') && encoding.hasEveryPossibilityFor('f')) {
            // The "c" and "f" encodings fully matching can only happen in a #3
            // as in a #2 you would have no 'f' and in a #5 you would have no 'c'
            wirePossibilities.removeValuesFor('3', encoding)
          }
          6 -> if (!(encoding.hasEveryPossibilityFor('c') && encoding.hasEveryPossibilityFor('f'))) {
            // The 'c' and 'f' encodings NOT fully matching can only happen in a #6
            wirePossibilities.removeValuesFor('6', encoding)
          }
        }
      }

      val mappings = wirePossibilities.entries.associate { (k, v) -> v.first() to k }

      outputValues
        .map { mappings.decode(it) }
        .map { SIGNAL_CODE_TO_NUMBER[it]!! }
        .joinToString("").toInt()
    }

    outputResult.sum()
  }

  private fun Map<Char, MutableList<Char>>.removeValuesFor(number: Char, encoding: String) {
    // we start by ensuring only values in our encoding show up for a given number
    // for example if we were looking for the number "2" we would have two signal characters
    // and for 'c' and 'f' we remove any value as a possibility that isn't inside our encoding
    NUMBER_TO_SIGNAL_CODE[number]?.forEach { signalCharacter ->
      this[signalCharacter]?.removeAll { !encoding.contains(it) }
    }

    // we also do the inverse, by clearing our encodings from anything that our number doesn't fill
    // so if we have the number "2" again, we remove these two encoded values from a possibility
    // to have encoded 'a', 'b', 'd', 'e', 'g'
    NUMBER_TO_SIGNAL_CODE[number]
      ?.let { signal -> signal.fold(ALL_LETTERS) { letters, letterToRemove -> letters.filterNot { it == letterToRemove } } }
      ?.forEach { signalCharacter -> this[signalCharacter]?.removeAll { encoding.contains(it) } }
  }

  private fun Map<Char, Char>.decode(outputValue: String) = outputValue
    .toCharArray()
    .map { this[it]!! }
    .sorted()
    .joinToString("")

  private fun measurement(line: String): Measurement {
    val (patterns, output) = line.split("|").map { it.trim().split(" ") }
    return Measurement(patterns, output)
  }

  private data class Measurement(val patterns: List<String>, val outputValues: List<String>)
}
