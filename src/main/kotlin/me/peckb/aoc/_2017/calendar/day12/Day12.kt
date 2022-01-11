package me.peckb.aoc._2017.calendar.day12

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.util.ArrayDeque

class Day12 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::house) { input ->
    val neighborhood = input.toList()
    val zeroGroup = mutableSetOf<Int>()
    val toExplore = ArrayDeque<Int>()

    val origin = neighborhood[0]
    zeroGroup.add(origin.id)
    toExplore.addAll(origin.connections)

    while(toExplore.isNotEmpty()) {
      val next = toExplore.pop()
      zeroGroup.add(next)
      neighborhood[next].connections.forEach {
        if (!zeroGroup.contains(it)) {
          toExplore.push(it)
        }
      }
    }

    zeroGroup.size
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::house) { input ->
    val neighborhood = input.toList()
    // val zeroGroup = mutableSetOf<Int>()
    val groups = mutableListOf<MutableSet<Int>>()


    var index = 0
    while(index < neighborhood.size) {
      val house = neighborhood[index]
      if (groups.none { it.contains(house.id) }) {
        val newGroup = mutableSetOf<Int>()
        val toExplore = ArrayDeque<Int>()

        newGroup.add(house.id)
        toExplore.addAll(house.connections)

        while(toExplore.isNotEmpty()) {
          val next = toExplore.pop()
          newGroup.add(next)
          neighborhood[next].connections.forEach {
            if (!newGroup.contains(it)) toExplore.push(it)
          }
        }
        groups.add(newGroup)
      }
      index ++
    }

    groups.size
  }

  private fun house(line: String): House {
    val (id, pipes) = line.split(" <-> ")
    val connections = pipes.split(", ").map { it.toInt() }

    return House(id.toInt(), connections)
  }

  data class House(val id: Int, val connections: List<Int>)
}
