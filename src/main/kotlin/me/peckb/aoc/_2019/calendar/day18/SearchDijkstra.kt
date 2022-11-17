package me.peckb.aoc._2019.calendar.day18

import me.peckb.aoc.pathing.Dijkstra
import me.peckb.aoc.pathing.DijkstraNodeWithCost

data class SearchArea(val sources: List<Day18.Section.Source>, val foundKeys: Set<Day18.Section.Source.Key>)

class SearchDijkstra(
  private val allKeys: Set<Day18.Section.Source.Key>,
  private val paths: Map<Day18.Section.Source, Map<Day18.Section.Source.Key, Day18.Route>>
) : Dijkstra<SearchArea, Distance, SearchAreaWithDistance> {
  override fun SearchArea.withCost(cost: Distance) = SearchAreaWithDistance(this, cost).withAllKeys(allKeys).withPaths(paths)
  override fun Distance.plus(cost: Distance) = this + cost
  override fun maxCost() = Int.MAX_VALUE
  override fun minCost() = 0
}

class SearchAreaWithDistance(private val searchArea: SearchArea, private val distance: Distance) :
  DijkstraNodeWithCost<SearchArea, Distance> {
  private lateinit var allKeys: Set<Day18.Section.Source.Key>
  private lateinit var paths: Map<Day18.Section.Source, Map<Day18.Section.Source.Key, Day18.Route>>

  fun withAllKeys(allKeys: Set<Day18.Section.Source.Key>) = apply { this.allKeys = allKeys }
  fun withPaths(paths: Map<Day18.Section.Source, Map<Day18.Section.Source.Key, Day18.Route>>) = apply { this.paths = paths }

  override fun compareTo(other: DijkstraNodeWithCost<SearchArea, Distance>): Int = distance.compareTo(other.cost())
  override fun cost(): Distance = distance
  override fun node(): SearchArea = searchArea

  override fun neighbors(): List<SearchAreaWithDistance> {
    val (sections, foundKeys) = searchArea

    if (foundKeys.size == allKeys.size) { return emptyList() }

    val neighbors = mutableListOf<SearchAreaWithDistance>()

    sections.forEachIndexed { index, source ->
      paths[source]?.forEach { (key, route) ->
        val (distance, doors) = route
        val doorKeys = doors.map { it.key }
        if (foundKeys.containsAll(doorKeys)) {
          val newSections = sections.toMutableList().also { it[index] = key }
          val searchArea = SearchArea(newSections, foundKeys.plus(key))
          val neighbor = SearchAreaWithDistance(searchArea, distance)
            .withAllKeys(allKeys)
            .withPaths(paths)

          neighbors.add(neighbor)
        }
      }
    }

    return neighbors
  }
}