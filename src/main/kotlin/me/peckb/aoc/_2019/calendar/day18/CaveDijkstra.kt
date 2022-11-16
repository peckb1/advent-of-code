package me.peckb.aoc._2019.calendar.day18

import me.peckb.aoc.pathing.Dijkstra
import me.peckb.aoc.pathing.DijkstraNodeWithCost

class CaveDijkstra(private val caves: List<List<Day18.Section>>) : Dijkstra<Area, Path, AreaWithPath> {
  override fun Path.plus(cost: Path): Path = cost

  override fun Area.withCost(cost: Path): AreaWithPath = AreaWithPath(this, cost).withCaves(caves)

  override fun minCost(): Path = Path(emptyList(), Int.MIN_VALUE)

  override fun maxCost(): Path = Path(emptyList(), Int.MAX_VALUE)
}

class AreaWithPath(private val area: Area, private val path: Path) : DijkstraNodeWithCost<Area, Path> {
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