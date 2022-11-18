package me.peckb.aoc._2019.calendar.day20

import me.peckb.aoc._2019.calendar.day20.Day20.Area.*
import me.peckb.aoc._2019.calendar.day20.Day20.PortalDirection.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.GenericIntDijkstra
import me.peckb.aoc.pathing.GenericIntDijkstra.DijkstraNode

class Day20 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val torus = Torus.setup(input)
    val (portalToSpaces, spaceToPortal) = findPortals(torus)

    val dijkstra = TorusDijkstra()
    val startNode = TorusPath(portalToSpaces["AA"]!!.first())
      .withTorus(torus)
      .withSpaceToPortal(spaceToPortal)
      .withPortalToSpaces(portalToSpaces)
    val endNode = TorusPath(portalToSpaces["ZZ"]!!.first())
    val paths = dijkstra.solve(startNode, endNode)

    paths[endNode]
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    -1
  }

  class TorusDijkstra : GenericIntDijkstra<TorusPath>()

  data class TorusPath(val path: Path) : DijkstraNode<TorusPath> {
    private lateinit var torus: Torus
    private lateinit var portalToSpaces: Map<String, Set<Path>>
    private lateinit var spaceToPortal: Map<Path, String>

    fun withTorus(torus: Torus) = apply { this.torus = torus }
    fun withPortalToSpaces(portalToSpaces: Map<String, Set<Path>>) = apply { this.portalToSpaces = portalToSpaces }
    fun withSpaceToPortal(spaceToPortal: Map<Path, String>) = apply { this.spaceToPortal = spaceToPortal }

    override fun neighbors(): Map<TorusPath, Int> {
      val (x, y) = path
      val n = torus.map[y - 1][x]
      val e = torus.map[y][x + 1]
      val s = torus.map[y + 1][x]
      val w = torus.map[y][x - 1]

      val nonPortalNeighbors = listOf(n, e, s, w)
        .filterIsInstance<Path>()
        .map { path ->
          TorusPath(path).withTorus(torus).withPortalToSpaces(portalToSpaces).withSpaceToPortal(spaceToPortal)
        }.associateWith { 1 }
        .toMutableMap()

      spaceToPortal[path]
        ?.let {  portal -> portalToSpaces[portal]!!.minus(path).firstOrNull() }
        ?.let { TorusPath(it).withTorus(torus).withPortalToSpaces(portalToSpaces).withSpaceToPortal(spaceToPortal) }
        ?.also { nonPortalNeighbors[it] = 1 }

      return nonPortalNeighbors
    }
  }

  private fun findPortals(torus: Torus): Pair<Map<String, Set<Path>>, Map<Path, String>> {
    val portalToSpaces = mutableMapOf<String, Set<Path>>()
    val spaceToPortal = mutableMapOf<Path, String>()

    fun addToMaps(portals: Map<Path, String>) {
      portals.forEach { (area, identifier) ->
        spaceToPortal[area] = identifier
        portalToSpaces.merge(identifier, setOf(area)) { a, b -> a.plus(b) }
      }
    }

    addToMaps(findHorizontalLinePortals(torus.map, torus.outerNorthIndex, UP))
    addToMaps(findVerticalLinePortals(torus.map, torus.outerEastIndex, RIGHT))
    addToMaps(findHorizontalLinePortals(torus.map, torus.outerSouthIndex, DOWN))
    addToMaps(findVerticalLinePortals(torus.map, torus.outerWestIndex, LEFT))

    addToMaps(findHorizontalLinePortals(torus.map, torus.innerNorthIndex, DOWN))
    addToMaps(findVerticalLinePortals(torus.map, torus.innerEastIndex, LEFT))
    addToMaps(findHorizontalLinePortals(torus.map, torus.innerSouthIndex, UP))
    addToMaps(findVerticalLinePortals(torus.map, torus.innerWestIndex, RIGHT))

    // it's not inner -> outer
    // it is up -> down and left -> right
    return portalToSpaces to spaceToPortal
  }

  private fun findHorizontalLinePortals(map: List<List<Area>>, yIndex: Int, portalDirection: PortalDirection): Map<Path, String> {
    val row = map[yIndex]
    val oneOffRow = map[portalDirection.directionChanger(yIndex, 1)]
    val twoOffRow = map[portalDirection.directionChanger(yIndex, 2)]

    val spaceToPortal = mutableMapOf<Path, String>()

    row.forEachIndexed { xIndex, area ->
      if (xIndex >= oneOffRow.size) return@forEachIndexed

      val oneOffArea = oneOffRow[xIndex]
      val twoOffArea = twoOffRow[xIndex]
      if (area is Path && oneOffArea is Portal && twoOffArea is Portal) {
        spaceToPortal[area] = portalDirection.idGenerator(oneOffArea.data, twoOffArea.data)
      }
    }

    return spaceToPortal
  }

  private fun findVerticalLinePortals(map: List<List<Area>>, xIndex: Int, portalDirection: PortalDirection): Map<Path, String> {
    val oneOffXIndex = portalDirection.directionChanger(xIndex, 1)
    val twoOffXIndex = portalDirection.directionChanger(xIndex, 2)

    val spaceToPortal = mutableMapOf<Path, String>()

    map.forEach { row ->
      if (oneOffXIndex >= row.size || xIndex >= row.size) return@forEach

      val area = row[xIndex]
      val oneOffArea = row[oneOffXIndex]
      val twoOffArea = row[twoOffXIndex]

      if (area is Path && oneOffArea is Portal && twoOffArea is Portal) {
        spaceToPortal[area] = portalDirection.idGenerator(oneOffArea.data, twoOffArea.data)
      }
    }

    return spaceToPortal
  }

  sealed class Area {
    data class Path(val x : Int, val y: Int) : Area()
    object Wall : Area()
    object Nothingness : Area()
    data class Portal(val data: Char) : Area()
  }

  enum class PortalDirection(
    val directionChanger: (Int, Int) -> Int,
    val idGenerator: (Char, Char) -> String
  ) {
    UP(Int::minus, { inner, outer -> "$outer$inner" }),
    DOWN(Int::plus, { inner, outer -> "$inner$outer" }),
    LEFT(Int::minus, { inner, outer -> "$outer$inner" }),
    RIGHT(Int::plus, { inner, outer -> "$inner$outer" })
  }
}
