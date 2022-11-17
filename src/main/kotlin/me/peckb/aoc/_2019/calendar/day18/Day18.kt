package me.peckb.aoc._2019.calendar.day18

import me.peckb.aoc._2019.calendar.day18.Day18.Section.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalArgumentException

class Day18 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    // parse our input and create our caves
    val (caves, startingLocation) = createCaves(input).also { (c, loc) ->
      c[loc.y][loc.x] = EMPTY
    }

    val (locationsByKey, keysByLocation, keyToKeyPaths) = setupData(caves)

    val solver = CaveDijkstra(caves)
    val keysFromStart = solver.solve(startingLocation)
      .filter { (area, _) -> keysByLocation.contains(area) }
      .filterNot { (_, path) -> path.steps.any { area -> caves[area.y][area.x] is DOOR } }
      .mapValues { (_, path) ->
        val newSteps = path.steps.filter { area -> caves[area.y][area.x] is DOOR }
        Path(newSteps, path.cost)
      }

    val searcher = SearchingDijkstra()
    val cheapest = keysFromStart.minOf { (area, path) ->
      val startNode = SearchArea(area, setOf(keysByLocation[area]!!))
        .withCaves(caves)
        .withKeyToKeyPaths(keyToKeyPaths)
        .withKeysByLocation(keysByLocation)
        .withLocationsByKey(locationsByKey)
        .usingInitialNeighbors { emptyMap() }
      val paths = searcher.solve(startNode)
        .filter { it.key.foundKeys.size == locationsByKey.size }
      path.cost + paths.minOf { it.value }
    }

    cheapest
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    // parse our input and create our caves
    val (caves, startingLocation) = createCaves(input).also { (c, loc) ->
      val (x, y) = loc
      c[y][x] = WALL
      c[y - 1][x] = WALL
      c[y][x + 1] = WALL
      c[y + 1][x] = WALL
      c[y][x - 1] = WALL
      c.print()
    }
    val (locationsByKey, keysByLocation, keyToKeyPaths) = setupData(caves)

    val solver = CaveDijkstra(caves)
    val startingLocations = startingLocation.let { (x, y) ->
      listOf(
        Area(x - 1, y - 1),
        Area(x - 1, y + 1),
        Area(x + 1, y - 1),
        Area(x + 1, y + 1),
      )
    }
    fun initialNeighbors(foundKeys: Set<KEY>) : Map<SearchArea, Int> {
      return startingLocations
        .map { solver.solve(it).toSafeKeys(keysByLocation, caves, foundKeys) }
        .flatMap { it.asSequence() }
        .groupBy({ it.key }, { it.value })
        .mapValues { (_, v) -> v.first().cost }
        .mapKeys { (area, _) ->
          SearchArea(area, foundKeys)
            .withCaves(caves)
            .withKeyToKeyPaths(keyToKeyPaths)
            .withKeysByLocation(keysByLocation)
            .withLocationsByKey(locationsByKey)
          .usingInitialNeighbors(::initialNeighbors)
        }
    }

    val asd = initialNeighbors(emptySet())

    val searcher = SearchingDijkstra()
    val startNode = SearchArea(Area(startingLocation.x, startingLocation.y), emptySet())
      .withCaves(caves)
      .withKeyToKeyPaths(keyToKeyPaths)
      .withKeysByLocation(keysByLocation)
      .withLocationsByKey(locationsByKey)
      .usingInitialNeighbors(::initialNeighbors)

    val base = searcher.solve(startNode)

    -1

    base.filter { it.key.foundKeys.size == (locationsByKey.size - 1) }
      .minOfOrNull { it.value }
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

  private fun setupData(caves: MutableList<MutableList<Section>>): Data {
    // find our keys and their routes!
    val locationsByKey = mutableMapOf<KEY, Area>()
    val keysByLocation = mutableMapOf<Area, KEY>()
    caves.forEachIndexed { y, row ->
      row.forEachIndexed { x, section ->
        if (section is KEY) {
          val area = Area(x, y)
          locationsByKey[section] = area
          keysByLocation[area] = section
        }
      }
    }

    // set up a map of every key to the path to everywhere else on the map
    val solver = CaveDijkstra(caves)
    val routes = locationsByKey.mapValues { (_, area) ->
      solver.solve(area)
    }

    // cull the map of keys to everywhere to just a map of keys to other keys
    // convert "routes" into just the paths from key to key, knowing which doors block those keys
    val keyToKeyPaths: Map<KEY, Map<KEY, Path>> = routes.mapValues { (_, paths) ->
      mutableMapOf<KEY, Path>().apply {
        paths.filter { (area, path) -> keysByLocation.contains(area) && path.cost > 0 }
          .forEach { (area, path) ->
            val newSteps = path.steps.filter { caves[it.y][it.x] is DOOR }
            this[keysByLocation[area]!!] = Path(newSteps, path.cost)
          }
      }
    }

    return Data(locationsByKey, keysByLocation, keyToKeyPaths)
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
    data class DOOR(val identifier: Char, val key: KEY = KEY(identifier.lowercaseChar())) : Section(identifier) {
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

private fun MutableMap<Area, Path>.toSafeKeys(
  keysByLocation: MutableMap<Area, KEY>,
  caves: MutableList<MutableList<Day18.Section>>,
  keysFound: Set<KEY>
) = this.filter { (area, _) -> keysByLocation.contains(area) }
  .filterNot { (_, path) ->
    path.steps.any { area ->
      val section = caves[area.y][area.x]
      section is DOOR && !keysFound.contains(section.key)
    }
  }.filterNot { (area, _) ->
    keysFound.contains(keysByLocation[area])
  }
  .mapValues { (_, path) ->
    val newSteps = path.steps.filter { area -> caves[area.y][area.x] is DOOR }
    Path(newSteps, path.cost)
  }

data class Data(
  val locationsByKey: MutableMap<KEY, Area>,
  val keysByLocation: MutableMap<Area, KEY>,
  val keyToKeyPaths: Map<KEY, Map<KEY, Path>>
)
