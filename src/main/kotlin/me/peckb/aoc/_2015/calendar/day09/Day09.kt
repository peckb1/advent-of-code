package me.peckb.aoc._2015.calendar.day09

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.Int.Companion.MAX_VALUE
import kotlin.math.min

class Day09 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val data = input.toList()
    val routeCosts = generateDistances(data)
    routeCosts.minOf { it }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val data = input.toList()
    val routeCosts = generateDistances(data)
    routeCosts.maxOf { it }
  }

  private fun generateDistances(data: List<String>): List<Long> {
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
    val permutations = generatePermutations(mutatingData, 0, distances.size - 1)

    return permutations.map { permutation ->
      permutation.toList().windowed(2).sumOf { (s, d) -> distances[s][d] }
    }
  }

  private fun generatePermutations(data: Array<Int>, l: Int, r: Int): MutableList<Array<Int>> {
    val permutations = mutableListOf<Array<Int>>()

    if (l == r) {
      permutations.add(data.clone())
    } else {
      (l..r).map { i ->
        swap(data, l, i)
        permutations.addAll(generatePermutations(data, l + 1, r))
        swap(data, l, i)
      }
    }

    return permutations
  }

  private fun swap(data: Array<Int>, i: Int, j: Int) {
    val t = data[i]
    data[i] = data[j]
    data[j] = t
  }
}
