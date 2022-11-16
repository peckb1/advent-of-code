package me.peckb.aoc._2019.calendar.day18

import me.peckb.aoc._2019.calendar.day18.Day18.Section.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.Dijkstra
import me.peckb.aoc.pathing.DijkstraNodeWithCost
import java.lang.IllegalArgumentException

class Day18 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  class CaveDijkstra(private val caves: List<List<Section>>) : Dijkstra<Area, Path, AreaWithPath> {
    override fun Path.plus(cost: Path): Path = cost

    override fun Area.withCost(cost: Path): AreaWithPath = AreaWithPath(this, cost).withCaves(caves)

    override fun minCost(): Path = Path(emptyList(), Int.MIN_VALUE)

    override fun maxCost(): Path = Path(emptyList(), Int.MAX_VALUE)
  }

  class AreaWithPath(private val area: Area, private val path: Path) : DijkstraNodeWithCost<Area, Path> {
    private lateinit var caves: List<List<Section>>

    override fun compareTo(other: DijkstraNodeWithCost<Area, Path>): Int {
      val pathCompare by lazy { path.compareTo(other.cost()) }
      val yCompare by lazy { path.steps.last().y.compareTo(other.cost().steps.last().y) }
      val xCompare by lazy { path.steps.last().x.compareTo(other.cost().steps.last().x) }

      return when (pathCompare) {
        0 -> {
          when (yCompare) {
            0 -> xCompare
            else -> yCompare
          }
        }
        else -> pathCompare
      }
    }

    override fun neighbors(): List<AreaWithPath> {
      val (x, y) = area

      val n = caves[y - 1][x] to Area(x, y - 1)
      val e = caves[y][x + 1] to Area(x + 1, y)
      val s = caves[y + 1][x] to Area(x, y + 1)
      val w = caves[y][x - 1] to Area(x - 1, y)

      return listOf(n, e, s, w)
        .filter { (section, _) -> section !is WALL }
        .map { (_, area) -> AreaWithPath(area, Path(path.steps.plus(area))) }
    }

    override fun node(): Area = area

    override fun cost(): Path = path

    fun withCaves(caves: List<List<Section>>) = apply {
      this.caves = caves
    }
  }

  data class Area(val x: Int, val y: Int)

  data class Path(val steps: List<Area>, val cost: Int = steps.size): Comparable<Path> {
    override fun compareTo(other: Path) = cost.compareTo(other.cost)
  }

  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    // parse our input and create our caves
    val (caves, startingLocation) = createCaves(input).also { (c, loc) ->
      c.print()
      c[loc.y][loc.x] = EMPTY
    }

    // find our keys!
    val keys = caves.flatMapIndexed { y, row ->
      row.mapIndexedNotNull { x, section ->
        if (section is KEY) { section to Area(x, y) } else { null }
      }
    }

    // setup a map of every key to the path to everywhere else on the map
    val solver = CaveDijkstra(caves)
    val routes = mutableMapOf<KEY, MutableMap<Area, Path>>().apply {
      keys.associateByTo(
        destination = this,
        keySelector = { (key, _) -> key},
        valueTransform = { (_, area) -> solver.solve(area) }
      )
    }

    // create some helper/reference maps
    val keysByLocation = keys.associateBy({ it.second }, { it.first })
    val locationsByKeys = keys.associateBy({ it.first }, { it.second })

    // cull the map of keys to everywhere to just a map of keys to other keys
    // convert "routes" into just the paths from key to key, knowing which doors block those keys
    val keyToKeyPaths = routes.mapValues { (key, paths) ->
      paths.filter { (area, _) -> keysByLocation.contains(area) }
        .filterNot { (area, _) -> keysByLocation.getValue(area) == key }
        .mapKeys { (area, _) -> keysByLocation.getValue(area) }
    }

    // find the keys reachable from our start location
    val keysFromStart = solver.solve(startingLocation).filter { (area, path) ->
      keysByLocation.contains(area)
    }.filterNot { (_, path) ->
      path.steps.any { area -> caves[area.y][area.x] is DOOR }
    }

    findCheapPath(keysFromStart, keyToKeyPaths, keysByLocation, locationsByKeys, caves)
  }

  private fun findCheapPath(
    keysFromStart: Map<Area, Path>,
    keyToKeyPaths: Map<KEY, Map<KEY, Path>>,
    keysByLocation: Map<Area, KEY>,
    locationsByKeys: Map<KEY, Area>,
    caves: List<List<Section>>
  ): Int? {
    return keysFromStart.mapNotNull { (startKeyArea, pathToStartKey) ->
      println("Start Key ${keysByLocation[startKeyArea]}")

      findRoute(
        keyToKeyPaths,
        keysByLocation,
        locationsByKeys,
        caves,
        startKeyArea,
        setOf(keysByLocation.getValue(startKeyArea)),
        pathToStartKey.cost
      )
    }.minOfOrNull { it }
  }

  private fun findRoute(
    keyToKeyPaths: Map<KEY, Map<KEY, Path>>,
    keysByLocation: Map<Area, KEY>,
    locationsByKeys: Map<KEY, Area>,
    caves: List<List<Section>>,
    startArea: Area,
    foundKeys: Set<KEY>,
    costToHere: Int
  ): Int? {
    val myKey = keysByLocation.getValue(startArea)
    val reachableKeys = keyToKeyPaths.getValue(myKey)
      .filterNot { (key, _) -> foundKeys.contains(key) }
      .filterNot { (_, path) ->
        path.steps.any { area ->
          val section = caves[area.y][area.x]
          section is DOOR && !foundKeys.contains(KEY(section.identifier.lowercaseChar()))
        }
      }

    return if (reachableKeys.isEmpty()) {
      // we have nowhere to go!
      if (foundKeys.size == keyToKeyPaths.size) {
        // we found all possible keys!
        println("$costToHere $foundKeys")
        costToHere
      } else {
        // we have nowhere to go, but didn't find everyone, so it's a failed route
        null
      }
    } else {
      // we have more keys to try and fetch!
      reachableKeys.mapNotNull { (nextKey, pathToNextKey) ->
        if (foundKeys.size < 5) {
          println("\t".repeat(foundKeys.size) +"NextKey Key $nextKey")
        }
        findRoute(
          keyToKeyPaths,
          keysByLocation,
          locationsByKeys,
          caves,
          locationsByKeys.getValue(nextKey),
          foundKeys.plus(nextKey),
          costToHere + pathToNextKey.cost
        )
      }.minOfOrNull { it }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    -1
  }


  private fun createCaves(input: Sequence<String>): Pair<MutableList<MutableList<Section>>, Area> {
    val caves = mutableListOf<MutableList<Section>>()
    var location = Area(-1, -1)

    input.forEachIndexed { y, line ->
      val row = mutableListOf<Section>()
      line.forEachIndexed { x, sectionChar ->
        val section = Section.fromChar(sectionChar)
        row.add(section).also {
          if (section is PERSON) location = Area(x, y)
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
