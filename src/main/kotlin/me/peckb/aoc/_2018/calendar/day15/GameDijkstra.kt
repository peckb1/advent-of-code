package me.peckb.aoc._2018.calendar.day15

import me.peckb.aoc._2018.calendar.day15.GameDijkstra.SpaceWithPath
import me.peckb.aoc.pathing.Dijkstra
import me.peckb.aoc.pathing.DijkstraNodeWithCost

class GameDijkstra(val gameMap: List<List<Space>>) : Dijkstra<Space, Path, SpaceWithPath> {
  override fun Space.withCost(cost: Path) = SpaceWithPath(this, cost)
  override fun minCost() = Path(emptyList(), Int.MIN_VALUE)
  override fun maxCost() = Path(emptyList(), Int.MAX_VALUE)
  override fun Path.plus(cost: Path) = cost

  inner class SpaceWithPath(private val space: Space, private val path: Path) :
    DijkstraNodeWithCost<Space, Path> {
    override fun neighbors(): List<DijkstraNodeWithCost<Space, Path>> {
      val u = gameMap[space.y - 1][space.x]
      val l = gameMap[space.y][space.x - 1]
      val r = gameMap[space.y][space.x + 1]
      val d = gameMap[space.y + 1][space.x]

      val emptySpaces = listOf(u, l, r, d).filterIsInstance<Space.Empty>()
      return emptySpaces.map { emptySpace ->
        val (x, y) = emptySpace.x to emptySpace.y
        SpaceWithPath(emptySpace, Path(path.steps.plus(Point(x, y))))
      }
    }

    override fun node() = space

    override fun cost() = path

    override fun compareTo(other: DijkstraNodeWithCost<Space, Path>): Int {
      // DEV NOTE: referencing the parent classes path comparator doubles our runtime,
      // compared to creating it ourself
      return when (val pathComp = path.compareTo(other.cost())) {
        0 -> {
          when (val yComp = path.steps.last().y.compareTo(other.cost().steps.last().y)) {
            0 -> path.steps.last().x.compareTo(other.cost().steps.last().x)
            else -> yComp
          }
        }
        else -> pathComp
      }
    }
  }
}

data class Path(val steps: List<Point>, val cost: Int = steps.size) : Comparable<Path> {
  override fun compareTo(other: Path) = cost.compareTo(other.cost)
}
