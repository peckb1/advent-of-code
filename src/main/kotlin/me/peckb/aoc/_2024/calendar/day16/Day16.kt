package me.peckb.aoc._2024.calendar.day16

import me.peckb.aoc._2024.calendar.day16.Direction.E
import me.peckb.aoc._2024.calendar.day16.Direction.N
import me.peckb.aoc._2024.calendar.day16.Direction.S
import me.peckb.aoc._2024.calendar.day16.Direction.W
import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.Dijkstra
import me.peckb.aoc.pathing.DijkstraNodeWithCost
import java.util.PriorityQueue

class Day16 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    lateinit var start: Room
    lateinit var end: Room

    val maze = mutableListOf<MutableList<Room>>()
    input.forEachIndexed { y, line ->
      val row = mutableListOf<Room>()
      line.forEachIndexed { x, c ->
        when (c) {
          '#' -> row.add(Room(y, x, space = Space.FULL))
          '.' -> row.add(Room(y, x))
          'S' -> row.add(Room(y, x).also { start = it; it.direction = E })
          'E' -> row.add(Room(y, x).also { end = it })
        }
      }
      maze.add(row)
    }

    val solver = MazeDijkstra(maze)
    val solutions = solver.solve(start)

    solutions[end]
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    lateinit var start: Pair<Int, Int>
    lateinit var end: Pair<Int, Int>

    val maze = mutableListOf<MutableList<Space>>()
    input.forEachIndexed { y, line ->
      val row = mutableListOf<Space>()
      line.forEachIndexed { x, c ->
        when (c) {
          '#' -> row.add(Space.FULL)
          '.' -> row.add(Space.EMPTY)
          'S' -> row.add(Space.EMPTY.also { start = y to x } )
          'E' -> row.add(Space.EMPTY.also { end = y to x } )
        }
      }
      maze.add(row)
    }

    val visitedWithCheapestCost = mutableMapOf<Triple<Int, Int, Direction>, Long>()
    val visited = mutableSetOf<SpaceWithPath>()
    val toVisit = PriorityQueue<SpaceWithPath>()

    toVisit.add(SpaceWithPath(start, end, listOf(start)))
    loop@ while(toVisit.isNotEmpty()) {
      val current = toVisit.poll()

      if (current.loc == end) {
        visited.add(current)
        continue@loop
      }
      val myDirection = SpaceWithPath.findDirection(current.path)
      val key = Triple(current.loc.first, current.loc.second, myDirection)
      if (visitedWithCheapestCost.getOrDefault(key, Long.MAX_VALUE) < current.cost) {
        continue@loop
      } else {
        visitedWithCheapestCost[key] = current.cost
      }

      val n = (-1 to 0)
      val e = (0 to 1)
      val s = (1 to 0)
      val w = (0 to -1)

      listOf(n, s, e, w)
        .asSequence()
        .map { (yDelta, xDelta) ->
          current.loc.first + yDelta to current.loc.second + xDelta
        }
        .filter { (y, x) ->
          y in maze.indices && x in maze[y].indices && maze[y][x] == Space.EMPTY && !current.path.contains(y to x)
        }
        .forEach { (y, x) ->
            toVisit.add(SpaceWithPath(y to x, end, current.path.plus(y to x)))
        }
    }

    val minCostRoutes = visited.filter { it.loc == end }.groupBy { (_, _, path, cost) ->
      cost
    }.minBy { it.key }

    minCostRoutes.value.fold(mutableSetOf<Pair<Int, Int>>()) { acc, next ->
      acc.also { it.addAll(next.path) }
    }.size
  }

  data class SpaceWithCost(
    val loc: Pair<Int, Int>,
    val cost: Long
  )

  data class SpaceWithPath(
    val loc: Pair<Int, Int>,
    val end: Pair<Int, Int>,
    val path: List<Pair<Int, Int>>,
    val cost: Long = findCost(path)
  ) : Comparable<SpaceWithPath> {
    override fun compareTo(other: SpaceWithPath): Int = cost.compareTo(other.cost)

    companion object {
      fun findDirection(path: List<Pair<Int, Int>>): Direction {
        var direction = E
        path.windowed(2).lastOrNull()?.let { (s, d) ->
          val deltas = (d.first - s.first) to (d.second - s.second)

          direction = when (deltas) {
            (-1 to 0) -> N
            (0 to 1) -> E
            (1 to 0) -> S
            (0 to -1) -> W
            else -> throw IllegalStateException("Unknown Deltas: $deltas")
          }
        }
        return direction
      }

      fun findCost(path: List<Pair<Int, Int>>): Long {
        var direction = E
        return path.windowed(2).sumOf { (s, d) ->
          val deltas = (d.first - s.first) to (d.second - s.second)

          val newDirection = when (deltas) {
            (-1 to 0) -> N
            (0 to 1) -> E
            (1 to 0) -> S
            (0 to -1) -> W
            else -> throw IllegalStateException("Unknown Deltas: $deltas")
          }

          (direction.turns(newDirection) + 1).also { direction = newDirection }
        }
      }
    }
  }
}

class MazeDijkstra(private val maze: MutableList<MutableList<Room>>) : Dijkstra<Room, Long, RoomWithCost> {
  override fun Room.withCost(cost: Long) = RoomWithCost(this, cost).withMaze(maze)
  override fun Long.plus(cost: Long) = this + cost
  override fun maxCost() = Long.MAX_VALUE
  override fun minCost() = 0L
}

data class RoomWithCost(val room: Room, val cost: Long) : DijkstraNodeWithCost<Room, Long> {
  private lateinit var maze: MutableList<MutableList<Room>>

  fun withMaze(maze: MutableList<MutableList<Room>>) = apply { this.maze = maze }

  override fun compareTo(other: DijkstraNodeWithCost<Room, Long>): Int = cost.compareTo(other.cost())

  override fun neighbors(): List<DijkstraNodeWithCost<Room, Long>> {
    val myDirection = room.direction
    val n = (-1 to 0) to Direction.N
    val e = (0 to 1)  to E
    val s = (1 to 0)  to Direction.S
    val w = (0 to -1) to Direction.W

    return listOf(n, s, e, w).mapNotNull { (loc, dir) ->
      val (y, x) = room.y + loc.first to room.x + loc.second
      if (y !in maze.indices || x !in maze[y].indices) { null }
      else if (maze[y][x].space == Space.FULL) { null }
      else {
        val cost = myDirection.turns(dir) + 1
        RoomWithCost(Room(y, x).also { it.direction = dir }, cost).withMaze(maze)
      }
    }
  }

  override fun node(): Room = room

  override fun cost(): Long = cost

  companion object {
    private const val TURN_COST = 1000
  }
}

data class Room(
  val y: Int,
  val x: Int,
  val space: Space = Space.EMPTY
) {
  var direction: Direction? = null
}

enum class Space { FULL, EMPTY }

enum class Direction { N, E, S, W }

private fun Direction?.turns(direction: Direction): Long {
  if (this == null) return 0
  if (this == direction) return 0

  if (this == Direction.N && direction == Direction.S ||
    this == Direction.S && direction == Direction.N ||
    this == E && direction == Direction.W ||
    this == Direction.W && direction == E
  ) return 2000

  return 1000
}
