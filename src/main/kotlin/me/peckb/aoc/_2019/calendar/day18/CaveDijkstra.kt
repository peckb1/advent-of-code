package me.peckb.aoc._2019.calendar.day18

import me.peckb.aoc._2019.calendar.day18.Day18.*
import me.peckb.aoc.pathing.Dijkstra
import me.peckb.aoc.pathing.DijkstraNodeWithCost

class CaveDijkstra(
  private val caves: Map<Area, Section>,
  private val result: MutableMap<Section.Source.Key, Route>,
  private val doorsByArea: MutableMap<Area, Set<Section.Door>>,
  private val sourceArea: Area
) : Dijkstra<Area, Distance, CaveWithDistance> {
  override fun Area.withCost(cost: Distance) = CaveWithDistance(this, cost).withResult(result).withDoorsByArea(doorsByArea).withCaves(caves).withSourceArea(sourceArea)
  override fun Distance.plus(cost: Distance) = this + cost
  override fun maxCost() = Int.MAX_VALUE
  override fun minCost() = 0
}

class CaveWithDistance(private val area: Area, private val distance: Distance) :
  DijkstraNodeWithCost<Area, Distance> {
  private lateinit var caves: Map<Area, Section>
  private lateinit var doorsByArea: MutableMap<Area, Set<Section.Door>>
  private lateinit var result: MutableMap<Section.Source.Key, Route>
  private lateinit var sourceArea: Area

  fun withResult(result: MutableMap<Section.Source.Key, Route>) = apply { this.result = result }
  fun withDoorsByArea(doorsByArea: MutableMap<Area, Set<Section.Door>>) = apply { this.doorsByArea = doorsByArea }
  fun withCaves(caves: Map<Area, Section>) = apply { this.caves = caves }
  fun withSourceArea(sourceArea: Area) = apply { this.sourceArea = sourceArea }

  override fun compareTo(other: DijkstraNodeWithCost<Area, Distance>): Int = distance.compareTo(other.cost())
  override fun cost(): Distance = distance
  override fun node(): Area = area

  override fun neighbors(): List<CaveWithDistance> {
    var doors = doorsByArea[area]!!
    when (val section = caves[area]!!) {
      is Section.Door -> {
        doors = doors.plus(section)
      }
      is Section.Source.Key -> {
        result[section] = Route(distance, doors)
        if (area != sourceArea) { return emptyList() }
      }
      Section.Empty, Section.Wall, is Section.Source.Robot -> { /* ignore */ }
    }

    val (x, y) = area
    val neighborAreas = listOf(
      Area(x - 1, y),
      Area(x, y - 1),
      Area(x, y + 1),
      Area(x + 1, y)
    )

    val neighborsToSearch = neighborAreas.mapNotNull { neighbor ->
      if (neighbor !in caves || neighbor in doorsByArea || caves[neighbor] is Section.Wall) {
        null
      } else {
        doorsByArea[neighbor] = doors
        neighbor
      }
    }

    return neighborsToSearch.map { CaveWithDistance(it, 1) }
  }
}