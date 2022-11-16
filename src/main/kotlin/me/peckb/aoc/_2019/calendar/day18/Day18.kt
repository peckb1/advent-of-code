package me.peckb.aoc._2019.calendar.day18

import me.peckb.aoc._2019.calendar.day18.Day18.Section.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.Dijkstra
import me.peckb.aoc.pathing.DijkstraNodeWithCost
import me.peckb.aoc.pathing.GenericIntDijkstra
import java.lang.IllegalArgumentException
import kotlin.math.min

class Day18 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {

  class SearchingDijkstra(

  ) : GenericIntDijkstra<SearchArea>() {

  }

  data class SearchArea(val area: Area, val foundKeys: Set<KEY>) : GenericIntDijkstra.DijkstraNode<SearchArea> {
    lateinit var keyToKeyPaths: Map<KEY, Map<KEY, Path>>
    lateinit var keysByLocation: Map<Area, KEY>
    lateinit var locationsByKey: Map<KEY, Area>
    lateinit var caves: List<List<Section>>

    fun withKeyToKeyPaths(keyToKeyPaths: Map<KEY, Map<KEY, Path>>) = apply {
      this.keyToKeyPaths = keyToKeyPaths
    }

    fun withKeysByLocation(keysByLocation: Map<Area, KEY>) = apply {
      this.keysByLocation = keysByLocation
    }

    fun withCaves(caves: List<List<Section>>) = apply {
      this.caves = caves
    }

    fun withLocationsByKey(locationsByKey: Map<KEY, Area>) = apply {
      this.locationsByKey = locationsByKey
    }

    override fun neighbors(): Map<SearchArea, Int> {
      val myKey = keysByLocation[area]!!
      val myConnections = keyToKeyPaths[myKey]!!.filterNot { (theirKey, path) ->
        if (foundKeys.contains(theirKey)) {
          // we've already seen that key
          true
        } else {
          val pathBlocked = path.steps.any { area ->
            val section = caves[area.y][area.x]
            section is DOOR && !foundKeys.contains(section.key)
          }
          pathBlocked
        }
      }

      return mutableMapOf<SearchArea, Int>().also { neighborMap ->
         myConnections.forEach { (neighborKey, neighborPath) ->
           val area = SearchArea(locationsByKey[neighborKey]!!, foundKeys.plus(neighborKey))
             .withCaves(caves)
             .withKeyToKeyPaths(keyToKeyPaths)
             .withKeysByLocation(keysByLocation)
             .withLocationsByKey(locationsByKey)
           neighborMap[area] = neighborPath.cost
         }
      }
    }
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
    val locationsByKey = keys.associateBy({ it.first }, { it.second })

    // cull the map of keys to everywhere to just a map of keys to other keys
    // convert "routes" into just the paths from key to key, knowing which doors block those keys
    val keyToKeyPaths = routes.mapValues { (key, paths) ->
      paths.filter { (area, _) -> keysByLocation.contains(area) }
        .filterNot { (area, _) -> keysByLocation.getValue(area) == key }
        .mapKeys { (area, _) -> keysByLocation.getValue(area) }
        .mapValues { (_, path) ->
          val newSteps = path.steps.filter { area ->
            caves[area.y][area.x] is DOOR
          }
          Path(newSteps, path.cost)
        }
    }

    val keysFromStart = solver.solve(startingLocation).filter { (area, path) ->
      keysByLocation.contains(area)
    }.mapValues { (_, path) ->
      val newSteps = path.steps.filter { area ->
        caves[area.y][area.x] is DOOR
      }
      Path(newSteps, path.cost)
    }.filterNot { (_, path) ->
      path.steps.any { area -> caves[area.y][area.x] is DOOR }
    }

    val searcher = SearchingDijkstra()
    val cheapest = keysFromStart.minOf { (area, path) ->
      val startNode = SearchArea(area, setOf(keysByLocation[area]!!))
        .withCaves(caves)
        .withKeyToKeyPaths(keyToKeyPaths)
        .withKeysByLocation(keysByLocation)
        .withLocationsByKey(locationsByKey)
      val paths = searcher.solve(startNode)
        .filter { it.key.foundKeys.size == keys.size }
      println("Finished ${keysByLocation[area]}")
      path.cost + paths.minOf { it.value }
    }

    cheapest

//    // find the keys reachable from our start location
//    val keysFromStart = solver.solve(startingLocation).filter { (area, path) ->
//      keysByLocation.contains(area)
//    }.filterNot { (_, path) ->
//      path.steps.any { area -> caves[area.y][area.x] is DOOR }
//    }
//
//    findCheapPath(keysFromStart, keyToKeyPaths, keysByLocation, locationsByKeys, caves)
  }

//  private fun findCheapPath(
//    keysFromStart: Map<Area, Path>,
//    keyToKeyPaths: Map<KEY, Map<KEY, Path>>,
//    keysByLocation: Map<Area, KEY>,
//    locationsByKeys: Map<KEY, Area>,
//    caves: List<List<Section>>
//  ): Int? {
//    return keysFromStart.mapNotNull { (startKeyArea, pathToStartKey) ->
//      println("Start Key ${keysByLocation[startKeyArea]}")
//
//      findRoute(
//        keyToKeyPaths = keyToKeyPaths,
//        keysByLocation = keysByLocation,
//        locationsByKeys = locationsByKeys,
//        caves = caves,
//        startArea = startKeyArea,
//        foundKeys = setOf(keysByLocation.getValue(startKeyArea)),
//        costToHere = pathToStartKey.cost,
//        bestPathSoFar = Int.MAX_VALUE
//      )
//    }.minOfOrNull { it }
//  }

//  private fun findRoute(
//    keyToKeyPaths: Map<KEY, Map<KEY, Path>>,
//    keysByLocation: Map<Area, KEY>,
//    locationsByKeys: Map<KEY, Area>,
//    caves: List<List<Section>>,
//    startArea: Area,
//    foundKeys: Set<KEY>,
//    costToHere: Int,
//    bestPathSoFar: Int
//  ): Int? {
//    val myKey = keysByLocation.getValue(startArea)
//    val reachableKeys = keyToKeyPaths.getValue(myKey)
//      .asSequence()
//      .filterNot { (key, _) -> foundKeys.contains(key) }
//      .filterNot { (_, path) ->
//        path.steps.any { area ->
//          val section = caves[area.y][area.x]
//          section is DOOR && !foundKeys.contains(KEY(section.identifier.lowercaseChar()))
//        }
//      }.filterNot { (_, path) ->
//        costToHere + path.cost > bestPathSoFar
//      }.toList()
//
//    return if (reachableKeys.isEmpty()) {
//      // we have nowhere to go!
//      if (foundKeys.size == keyToKeyPaths.size) {
//        // we found all possible keys!
//        println(globalCounter)
//        costToHere
//      } else {
//        // we have nowhere to go, but didn't find everyone, so it's a failed route
//        null
//      }
//    } else {
//      // we have more keys to try and fetch!
//      var newBestPath = bestPathSoFar
//      reachableKeys.mapNotNull { (nextKey, pathToNextKey) ->
//        val route = findRoute(
//          keyToKeyPaths = keyToKeyPaths,
//          keysByLocation = keysByLocation,
//          locationsByKeys = locationsByKeys,
//          caves = caves,
//          startArea = locationsByKeys.getValue(nextKey),
//          foundKeys = foundKeys.plus(nextKey),
//          costToHere = costToHere + pathToNextKey.cost,
//          bestPathSoFar = newBestPath
//        )
//        route?.also {
//          newBestPath = min(newBestPath, it)
//        }
//      }.minOfOrNull { it }
//    }
//  }

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
