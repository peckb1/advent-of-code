package me.peckb.aoc._2015.calendar.day13

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.generators.PermutationGenerator
import kotlin.math.max

class Day13 @Inject constructor(
  private val permutationGenerator: PermutationGenerator,
  private val generatorFactory: InputGeneratorFactory
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::happinessMeasurement) { input ->
    val seatingOptions = generateSeatingOptions(input)

    val people = seatingOptions.keys.toTypedArray()
    val peoplePermutations = permutationGenerator.generatePermutations(people)

    findBestArrangement(peoplePermutations, seatingOptions)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::happinessMeasurement) { input ->
    val seatingOptions = generateSeatingOptions(input)

    var people = seatingOptions.keys.toTypedArray()
    people.forEach { neighbor ->
      seatingOptions.merge("me", mutableMapOf(neighbor to 0)) { cur, me ->
        (cur + me).toMutableMap()
      }
      seatingOptions[neighbor]!!["me"] = 0
    }
    people = seatingOptions.keys.toTypedArray()
    val peoplePermutations = permutationGenerator.generatePermutations(people)

    findBestArrangement(peoplePermutations, seatingOptions)
  }

  private fun happinessMeasurement(line: String): HappinessMeasurement {
    // Alice would gain 54 happiness units by sitting next to Bob.
    val (occupantAndHappiness, neighbor) = line.split(" happiness units by sitting next to ")
    val (occupant, happiness) = occupantAndHappiness.split(" would ")

    val (gainOrLose, amount) = happiness.split(" ")
    val happinessChange = if (gainOrLose == "gain") amount.toInt() else -amount.toInt()

    return HappinessMeasurement(occupant, neighbor.dropLast(1), happinessChange)
  }

  private data class HappinessMeasurement(val occupant: String, val neighbor: String, val happinessChange: Int)

  private fun generateSeatingOptions(input: Sequence<HappinessMeasurement>): MutableMap<String, MutableMap<String, Int>> {
    val seatingOptions: MutableMap<String, MutableMap<String, Int>> = mutableMapOf()
    input.forEach { (occupant, neighbor, happinessChange) ->
      seatingOptions.merge(occupant, mutableMapOf(neighbor to happinessChange)) { cur, me ->
        (cur + me).toMutableMap()
      }
    }
    return seatingOptions
  }

  // "borrowed" from 2015 day 09, extract out if we use it a third time
  private fun findBestArrangement(peoplePermutations: List<Array<String>>, seatingOptions: MutableMap<String, MutableMap<String, Int>>): Int {
    var bestCost = peoplePermutations.first().toList().windowed(2).sumOf { (s, d) ->
      seatingOptions[s]!![d]!! + seatingOptions[d]!![s]!!
    }
    peoplePermutations.drop(1).map { permutation ->
      var cost = permutation.toList().windowed(2).sumOf { (s, d) ->
        seatingOptions[s]!![d]!! + seatingOptions[d]!![s]!!
      }
      val s = permutation[permutation.size - 1]
      val d = permutation[0]
      cost += seatingOptions[s]!![d]!! + seatingOptions[d]!![s]!!

      bestCost = max(bestCost, cost)
    }
    return bestCost
  }
}

