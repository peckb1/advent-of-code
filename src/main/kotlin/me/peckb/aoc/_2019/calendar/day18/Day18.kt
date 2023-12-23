package me.peckb.aoc._2019.calendar.day18

import arrow.core.mapNotNull
import me.peckb.aoc._2019.calendar.day18.Day18.Section.*
import me.peckb.aoc._2019.calendar.day18.Day18.Section.Source.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

typealias Distance = Int

class Day18 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val inputList = input.toList()

    val caves = createCaves(inputList)
    val paths = createPaths(caves)
    solve(paths)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val inputList = input.toList()
    val caves = createCaves(inputList).toMutableMap()

    val (robotX, robotY) = caves.entries.first { it.value is Robot }.key
    // put in the walls
    listOf(
      Area(robotX, robotY),
      Area(robotX + 1, robotY),
      Area(robotX - 1, robotY),
      Area(robotX, robotY + 1),
      Area(robotX, robotY - 1),
    ).forEach { caves[it] = Wall }
    // make four new robots for each section
    listOf(
      Area(robotX + 1, robotY + 1),
      Area(robotX + 1, robotY - 1),
      Area(robotX - 1, robotY + 1),
      Area(robotX - 1, robotY - 1),
    ).forEachIndexed { index, area -> caves[area] = Robot(index) }

    val paths = createPaths(caves)
    solve(paths)
  }

  private fun createCaves(input: List<String>): Map<Area, Section> {
    val caves = mutableMapOf<Area, Section>()
    var bots = 0
    input.forEachIndexed { y, line ->
      line.forEachIndexed { x, data ->
        val area = Area(x, y)
        when (data) {
          in 'A'..'Z' -> caves[area] = Door(Character.toLowerCase(data))
          in 'a'..'z' -> caves[area] = Key(data)
          '@' -> caves[area] = Robot(bots++)
          '.' -> caves[area] = Empty
          '#' -> caves[area] = Wall
        }
      }
    }
    return caves
  }

  private fun createPaths(caves: Map<Area, Section>): Map<Source, Map<Key, Route>> {
    val sourceLocations = caves.mapNotNull { (_, a) -> if (a is Source) a else null }

    val paths = mutableMapOf<Source, Map<Key, Route>>()

    sourceLocations.forEach { (area, source) ->
      val result = mutableMapOf<Key, Route>()
      val doorsByArea = mutableMapOf(area to emptySet<Door>())

      val caveDijkstra = CaveDijkstra(caves, result, doorsByArea, area)
      caveDijkstra.solve(area)

      paths[source] = result
    }

    return paths
  }

  private fun solve(paths: Map<Source, Map<Key, Route>>): Int {
    val allKeys: Set<Key> = paths.keys.mapNotNullTo(mutableSetOf()) { (it as? Key) }

    val searchDijkstra = SearchDijkstra(allKeys, paths)
    val sources = paths.keys.filterIsInstance<Robot>()
    val startArea = SearchArea(sources, emptySet())
    val solutions = searchDijkstra.solve(startArea)

    return solutions
      .filter { (searchArea, _) -> searchArea.foundKeys.containsAll(allKeys) }
      .minOf { it.value }
  }

  data class Area(val x: Int, val y: Int)

  data class Route(val distance: Distance, val doorsBlocking: Set<Door>)

  sealed class Section {
    sealed class Source : Section() {
      data class Robot(val id: Int) : Source()
      data class Key(val id: Char) : Source()
    }
    data class Door(val id: Char) : Section() { val key: Key = Key(id.lowercaseChar()) }
    data object Empty : Section()
    data object Wall : Section()
  }
 }
