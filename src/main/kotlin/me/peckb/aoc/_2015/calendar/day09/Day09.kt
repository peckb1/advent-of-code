package me.peckb.aoc._2015.calendar.day09

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.generators.PermutationGenerator
import javax.inject.Inject
import kotlin.Int.Companion.MAX_VALUE
import kotlin.math.max
import kotlin.math.min

class Day09 @Inject constructor(
  private val permutationGenerator: PermutationGenerator,
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    generateDistances(input.toList(), ::min)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    generateDistances(input.toList(), ::max)
  }

  private fun generateDistances(data: List<String>, comparator: (Long, Long) -> Long): Long {
    val allCities = mutableSetOf<String>()

    data.forEach {
      val cities = it.substringBefore(" = ")
      val (source, destination) = cities.split(" to ")
      allCities.add(source)
      allCities.add(destination)
    }
    val citiesWithIndices = allCities.toList()
    val indexMapping = mutableMapOf<String, Int>()

    citiesWithIndices.forEachIndexed { i, city -> indexMapping[city] = i }

    val distances = Array(allCities.size) { Array(allCities.size) { MAX_VALUE.toLong() } }.apply {
      indices.forEach { this[it][it] = 0 }
    }

    data.forEach {
      val distance = it.substringAfter(" = ").toLong()
      val cities = it.substringBefore(" = ")
      val (source, destination) = cities.split(" to ")
      distances[indexMapping[source]!!][indexMapping[destination]!!] = distance
      distances[indexMapping[destination]!!][indexMapping[source]!!] = distance
    }

    val mutatingData = Array(distances.size) { MAX_VALUE }.apply {
      (0 until size).forEach { this[it] = it }
    }
    val permutations = permutationGenerator.generatePermutations(mutatingData)

    var bestCost = permutations.first().toList().windowed(2).sumOf { (s, d) -> distances[s][d] }
    permutations.drop(1).map { permutation ->
      val cost = permutation.toList().windowed(2).sumOf { (s, d) -> distances[s][d] }
      bestCost = comparator(bestCost, cost)
    }
    return bestCost
  }
}
