package me.peckb.aoc._2017.calendar.day24

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day24 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::day24) { input ->
    val components = input.mapIndexed { index, component -> component.apply { id = index } }.toList()

    buildBridges(components).maxOf { it.second }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::day24) { input ->
    val components = input.mapIndexed { index, component -> component.apply { id = index } }.toList()

    buildBridges(components).groupBy { it.first }.maxByOrNull { it.key }?.value?.maxOf { it.second }
  }

  private fun buildBridges(components: List<Component>): List<Pair<Int, Int>> {
    return buildBridges(0, mutableSetOf(), components)
  }

  private fun buildBridges(endValue: Int, currentBridgeComponents: MutableSet<Component>, allComponents: List<Component>): List<Pair<Int, Int>> {
    val nextPieces = allComponents.filter { (it.a == endValue || it.b == endValue) && !currentBridgeComponents.contains(it) }

    if (nextPieces.isEmpty()) return listOf(currentBridgeComponents.size to currentBridgeComponents.strength())

    return nextPieces.flatMap { component ->
      currentBridgeComponents.add(component)
      (if (component.b == endValue) {
        buildBridges(component.a, currentBridgeComponents, allComponents)
      } else {
        buildBridges(component.b, currentBridgeComponents, allComponents)
      }).also {
        currentBridgeComponents.remove(component)
      }
    }
  }

  private fun day24(line: String): Component {
    val (a, b) = line.split("/").map { it.toInt() }
    return Component(null, a, b)
  }

  data class Component(var id: Int?, val a: Int, val b: Int)

  private fun Set<Component>.strength(): Int {
    return fold(0) { acc, component -> acc + component.a + component.b }
  }
}
