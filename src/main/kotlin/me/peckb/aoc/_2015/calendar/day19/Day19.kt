package me.peckb.aoc._2015.calendar.day19

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.Int.Companion.MAX_VALUE

class Day19 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (replacements, molecule) = loadPartOneInput(input)
    molecule.stepForward(replacements).size
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val data = loadPartTwoInput(input)
    val longestReplacement = data.replacements.keys.maxByOrNull { it.length }?.length ?: MAX_VALUE

    var molecule = data.molecule
    var end = molecule.length
    var start = end - 1
    var replacements = 0

    while(molecule != "e") {
      val sequence = molecule.substring(start, end)
      data.replacements[sequence]?.let { replacement ->
        // we found a replacement, so swap it out and reset our search
        molecule = molecule.replace(start, end, replacement)
        end = molecule.length
        start = end - 1
        replacements++
      } ?: run {
        // either way, we can't add more data to our range and find a replacement
        // so remove the last character and try again
        if (end - start == longestReplacement || start == 0) {
          end--
          start = end -1
        } else {
          start--
        }
      }
    }

    replacements
  }

  private fun loadPartTwoInput(input: Sequence<String>): Data {
    val replacements = mutableMapOf<String, String>()
    var mainString: String? = null
    var loadingReplacements = true

    input.forEach { line ->
      if (line.isBlank()) {
        loadingReplacements = false
      } else {
        if (loadingReplacements) {
          val (origin, destination) = line.split(" => ").let { it.first() to it.last() }
            replacements[destination] = origin
        } else {
          mainString = line
        }
      }
    }

    return Data(replacements, mainString!!)
  }

  private fun loadPartOneInput(input: Sequence<String>): Pair<Map<String, List<String>>, String> {
    val replacements = mutableMapOf<String, List<String>>()
    var mainString: String? = null
    var loadingReplacements = true

    input.forEach { line ->
      if (line.isBlank()) {
        loadingReplacements = false
      } else {
        if (loadingReplacements) {
          val (origin, destination) = line.split(" => ").let { it.first() to it.last() }
          replacements.merge(origin, mutableListOf(destination), List<String>::plus)
        } else {
          mainString = line
        }
      }
    }

    return replacements to mainString!!
  }

  data class Data(val replacements: MutableMap<String, String>, val molecule: String)
}

private fun String.replace(start: Int, end: Int, replacement: String): String {
  val newStart = take(start)
  val newEnd = takeLast(length - end)

  return "$newStart$replacement$newEnd"
}

private fun String.stepForward(replacements: Map<String, List<String>>): Set<String> {
  val molecules = mutableSetOf<String>()

  replacements.forEach { (source, destinations) ->
    destinations.forEach { destination ->
      val regex = source.toRegex()
      val matchResults = regex.findAll(this)
      matchResults.forEach { matchResult ->
        molecules.add(replace(matchResult.range.first, matchResult.range.last + 1, destination))
      }
    }
  }

  return molecules
}
