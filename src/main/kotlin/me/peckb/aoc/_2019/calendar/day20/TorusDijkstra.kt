package me.peckb.aoc._2019.calendar.day20

import me.peckb.aoc.pathing.GenericIntDijkstra

class TorusDijkstra : GenericIntDijkstra<TorusPath>()

data class TorusPath(val path: Day20.Area.Path, val depth: Int) : GenericIntDijkstra.DijkstraNode<TorusPath> {
  private lateinit var searchData: Day20.SearchData

  fun withSearchData(searchData: Day20.SearchData) = apply { this.searchData = searchData }

  override fun neighbors(): Map<TorusPath, Int> {
    val (x, y) = path

    val n = searchData.torus.map[y - 1][x]
    val e = searchData.torus.map[y][x + 1]
    val s = searchData.torus.map[y + 1][x]
    val w = searchData.torus.map[y][x - 1]

    val nonPortalNeighbors = listOf(n, e, s, w)
      .filterIsInstance<Day20.Area.Path>()
      .map { path -> TorusPath(path, depth).withSearchData(searchData) }
      .associateWith { 1 }
      .toMutableMap()

    searchData.outerPortals[path]
      ?.let {  portal -> searchData.portalToSpaces[portal]!!.minus(path).firstOrNull() }
      ?.let {
        val newDepth = if(searchData.shouldRecurse) depth - 1 else depth
        if (newDepth >= 0) TorusPath(it, newDepth).withSearchData(searchData) else null
      }?.also { nonPortalNeighbors[it] = 1 }

    searchData.innerPortals[path]
      ?.let {  portal -> searchData.portalToSpaces[portal]!!.minus(path).firstOrNull() }
      ?.let {
        val newDepth = if(searchData.shouldRecurse) depth + 1 else depth
        TorusPath(it, newDepth).withSearchData(searchData)
      }?.also { nonPortalNeighbors[it] = 1 }

    return nonPortalNeighbors
  }
}
