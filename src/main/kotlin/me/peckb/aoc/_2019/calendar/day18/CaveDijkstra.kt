package me.peckb.aoc._2019.calendar.day18

import me.peckb.aoc.pathing.Dijkstra
import me.peckb.aoc.pathing.DijkstraNodeWithCost
import me.peckb.aoc.pathing.GenericIntDijkstra

class CaveDijkstra(private val caves: List<List<Day18.Section>>) : Dijkstra<Area, Path, AreaWithPath> {
  override fun Path.plus(cost: Path): Path = cost

  override fun Area.withCost(cost: Path): AreaWithPath = AreaWithPath(this, cost).withCaves(caves)

  override fun minCost(): Path = Path(emptyList(), Int.MIN_VALUE)

  override fun maxCost(): Path = Path(emptyList(), Int.MAX_VALUE)
}

data class AreaWithPath(private val area: Area, private val path: Path) : DijkstraNodeWithCost<Area, Path> {
  private lateinit var caves: List<List<Day18.Section>>

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
      .filter { (section, _) -> section !is Day18.Section.WALL }
      .map { (_, area) -> AreaWithPath(area, Path(path.steps.plus(area))) }
  }

  override fun node(): Area = area

  override fun cost(): Path = path

  fun withCaves(caves: List<List<Day18.Section>>) = apply {
    this.caves = caves
  }
}

data class Area(val x: Int, val y: Int)

data class Path(val steps: List<Area>, val cost: Int = steps.size): Comparable<Path> {
  override fun compareTo(other: Path) = cost.compareTo(other.cost)
}

class SearchingDijkstra : GenericIntDijkstra<SearchArea>()

data class SearchArea(val area: Area, val foundKeys: Set<Day18.Section.KEY>) : GenericIntDijkstra.DijkstraNode<SearchArea> {
  private lateinit var keyToKeyPaths: Map<Day18.Section.KEY, Map<Day18.Section.KEY, Path>>
  private lateinit var keysByLocation: Map<Area, Day18.Section.KEY>
  private lateinit var locationsByKey: Map<Day18.Section.KEY, Area>
  private lateinit var caves: List<List<Day18.Section>>

  private lateinit var initialNeighbors: (Set<Day18.Section.KEY>) -> Map<SearchArea, Int>

  fun usingInitialNeighbors(initialNeighbors: (Set<Day18.Section.KEY>) -> Map<SearchArea, Int>) = apply {
    this.initialNeighbors = initialNeighbors
  }

  fun withKeyToKeyPaths(keyToKeyPaths: Map<Day18.Section.KEY, Map<Day18.Section.KEY, Path>>) = apply {
    this.keyToKeyPaths = keyToKeyPaths
  }

  fun withKeysByLocation(keysByLocation: Map<Area, Day18.Section.KEY>) = apply {
    this.keysByLocation = keysByLocation
  }

  fun withCaves(caves: List<List<Day18.Section>>) = apply {
    this.caves = caves
  }

  fun withLocationsByKey(locationsByKey: Map<Day18.Section.KEY, Area>) = apply {
    this.locationsByKey = locationsByKey
  }

  override fun neighbors(): Map<SearchArea, Int> {
    val myKey = keysByLocation[area]

//    println("Looking for neighbors for $myKey")

    val extraConnections = if (this::initialNeighbors.isInitialized && foundKeys.size != keyToKeyPaths.size) {
      val keySet = myKey?.let(foundKeys::plus) ?: foundKeys
//      println("Assuming we have seen $keySet")
      initialNeighbors.invoke(keySet).also {
//        println("Found these extra connections $it")
      }.toMutableMap()
    } else {
//      println("no extra connections")
      mutableMapOf()
    }

    val myConnections = myKey?.let {
      keyToKeyPaths[it]!!.filterNot { (theirKey, path) ->
        if (foundKeys.contains(theirKey)) {
          // we've already seen that key
          true
        } else {
          val pathBlocked = path.steps.any { area ->
            val section = caves[area.y][area.x]
            section is Day18.Section.DOOR && !foundKeys.contains(section.key)
          }
          pathBlocked
        }
      }
    } ?: emptyMap()

    println("My normal connections are $myConnections")

    return extraConnections.also { neighborMap ->
      myConnections.forEach { (neighborKey, neighborPath) ->
        val area = SearchArea(locationsByKey[neighborKey]!!, foundKeys.plus(neighborKey))
          .withCaves(caves)
          .withKeyToKeyPaths(keyToKeyPaths)
          .withKeysByLocation(keysByLocation)
          .withLocationsByKey(locationsByKey)
          .usingInitialNeighbors(initialNeighbors)
        neighborMap[area] = neighborPath.cost
      }
    }
  }
}
