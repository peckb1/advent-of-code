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
