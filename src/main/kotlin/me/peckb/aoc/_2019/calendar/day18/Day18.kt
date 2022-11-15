package me.peckb.aoc._2019.calendar.day18

import me.peckb.aoc._2019.calendar.day18.Day18.Section.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.GenericIntDijkstra
import me.peckb.aoc.pathing.GenericIntDijkstra.DijkstraNode
import java.lang.IllegalArgumentException
import kotlin.math.min

typealias Cost = Int

class Day18 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (caves, startingLocation) = createCaves(input)
    caves.print()
    caves[startingLocation.y][startingLocation.x] = EMPTY

    val maxKeysToFind = caves.howManyKeys()

    val randomInitialCost = findRandomPathCost(caves, startingLocation)
    findReachableKeysCost(
      caves = caves,
      myLocation = startingLocation,
      foundKeys = emptySet(),
      costNotToExceed = randomInitialCost,
      costSoFar = 0,
      maxKeysToFind = maxKeysToFind
    )
  }

  private fun findRandomPathCost(
    caves: List<List<Section>>,
    startLocation: Location
  ): Int {
    var runningCost = 0
    val foundKeys = mutableSetOf<KEY>()
    var firstReachableKeys = searchForKeys(caves, startLocation, foundKeys)

    while(firstReachableKeys.isNotEmpty()) {
      val (key, location, cost) = firstReachableKeys.random()
      runningCost += cost
      foundKeys.add(key)
      firstReachableKeys = searchForKeys(caves, location, foundKeys)
    }

    return runningCost
  }

  private fun findReachableKeysCost(
    caves: List<List<Section>>,
    myLocation: Location,
    foundKeys: Set<KEY>,
    costNotToExceed: Int,
    costSoFar: Int,
    maxKeysToFind: Int
  ): Int {
    val firstReachableKeys = searchForKeys(caves, myLocation, foundKeys)

    if (firstReachableKeys.isEmpty()) {
      return 0
    }

    var myCheapestChild = 9000 - costSoFar
    var newHighCost = costNotToExceed
    val myCheapestChildCost = firstReachableKeys.minOf search@ { (key, location, cost) ->
      if (costSoFar + cost > newHighCost) {
        return@search 9000
      }

      val newKeySet = foundKeys.plus(key)
      println("\t".repeat(foundKeys.size) + "${newKeySet.size} $key")
      if (newKeySet.size == maxKeysToFind) {
        return 0
      }


      val cheapestCostAfterFindingKey = findReachableKeysCost(
        caves = caves,
        myLocation = location,
        foundKeys = newKeySet,
        costNotToExceed = newHighCost,
        costSoFar = costSoFar + cost,
        maxKeysToFind = maxKeysToFind
      )

      myCheapestChild = min(cheapestCostAfterFindingKey, myCheapestChild)
      newHighCost = min(costNotToExceed, costSoFar + myCheapestChild)

      cost + cheapestCostAfterFindingKey
    }

    return myCheapestChildCost
  }

  private fun searchForKeys(
    caves: List<List<Section>>,
    currentLocation: Location,
    keysFound: Set<KEY>
  ): List<AttainableKeys> {
    val keyLocations = mutableSetOf<Pair<KEY, Location>>()

    val searcher = object : GenericIntDijkstra<Location>() {}

    val start = Location(currentLocation.x, currentLocation.y)
      .withCaves(caves)
      .withKeysFound(keysFound)
      .withKeyLocations(keyLocations)
    val paths = searcher.solve(start)

    return keyLocations.mapNotNull { (key, loc) ->
      paths[Location(loc.x, loc.y)]?.let { cost ->
        AttainableKeys(key, loc, cost)
      }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    -1
  }

  data class Location(val x: Int, val y: Int) : DijkstraNode<Location> {
    private lateinit var caves: List<List<Section>>
    private lateinit var keysFound: Set<KEY>
    private lateinit var keyLocations: MutableSet<Pair<KEY, Location>>

    fun withCaves(caves: List<List<Section>> = mutableListOf()) = apply {
      this.caves = caves
    }

    fun withKeysFound(keysFound: Set<KEY>) = apply {
      this.keysFound = keysFound
    }

    fun withKeyLocations(keyLocations: MutableSet<Pair<KEY, Location>>) = apply {
      this.keyLocations = keyLocations
    }

    override fun neighbors(): Map<Location, Int> {
      val mySpot = caves[y][x]
      if (mySpot is KEY) {
        // if we have not found this key before
        if (!keysFound.contains(mySpot)) {
          // store it as a new key location
          keyLocations.add(mySpot to Location(x, y).withCaves(caves).withKeysFound(keysFound).withKeyLocations(keyLocations))
          // and don't explore further from this spot
          return emptyMap()
        }
      }

      // if we did not return early from finding a key, check our neighbors
      val n = caves[y - 1][x] to Location(x, y - 1).withCaves(caves).withKeysFound(keysFound).withKeyLocations(keyLocations)
      val e = caves[y][x + 1] to Location(x + 1, y).withCaves(caves).withKeysFound(keysFound).withKeyLocations(keyLocations)
      val s = caves[y + 1][x] to Location(x, y + 1).withCaves(caves).withKeysFound(keysFound).withKeyLocations(keyLocations)
      val w = caves[y][x - 1] to Location(x - 1, y).withCaves(caves).withKeysFound(keysFound).withKeyLocations(keyLocations)

      val neighborsToExplore = listOf(n, e, s, w).filter { (section, _) ->
        when (section) {
          is DOOR -> {
            keysFound.contains(KEY(section.identifier.lowercaseChar()))
          }
          is KEY -> true
          EMPTY -> true
          PERSON -> true
          WALL -> false
        }
      }

      return neighborsToExplore.map { it.second }.associateWith { 1 }
    }
  }

  data class AttainableKeys(val key: KEY, val location: Location, val cost: Cost)

  private fun createCaves(input: Sequence<String>): Pair<MutableList<MutableList<Section>>, Location> {
    val caves = mutableListOf<MutableList<Section>>()
    var location = Location(-1, -1)

    input.forEachIndexed { y, line ->
      val row = mutableListOf<Section>()
      line.forEachIndexed { x, sectionChar ->
        val section = Section.fromChar(sectionChar)
        row.add(section).also {
          if (section is PERSON) location = Location(x, y)
        }
      }
      caves.add(row)
    }
    return caves to location
  }

  private fun List<List<Section>>.print() {
    forEach { row ->
      row.forEach { print(it) }
      println()
    }
  }

  private fun List<List<Section>>.howManyKeys(): Int {
    return this.sumOf { row ->
      row.sumOf { section ->
        val result: Int = if (section is KEY) 1 else 0
        result
      }
    }
  }

  sealed class Section(private val representation: Char) {
    object WALL : Section('#')
    data class KEY(val identifier: Char) : Section(identifier) {
      override fun toString() = super.toString()
    }
    data class DOOR(val identifier: Char) : Section(identifier) {
      override fun toString() = super.toString()
    }
    object EMPTY : Section('.')
    object PERSON : Section('@')

    override fun toString(): String {
      return representation.toString()
    }

    companion object {
      fun fromChar(c: Char): Section {
        return when {
          ('a' .. 'z').contains(c) -> KEY(c)
          ('A' .. 'Z').contains(c) -> DOOR(c)
          c == '#' -> WALL
          c == '.' -> EMPTY
          c == '@' -> PERSON
          else -> throw IllegalArgumentException("Unknown representation: $c")
        }
      }
    }
  }
}
