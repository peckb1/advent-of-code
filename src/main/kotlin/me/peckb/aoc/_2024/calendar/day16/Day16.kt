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
import kotlin.math.abs
import kotlin.math.min

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
    lateinit var start: Location
    lateinit var end: Location

    val maze = mutableListOf<MutableList<Space>>()
    input.forEachIndexed { y, line ->
      val row = mutableListOf<Space>()
      line.forEachIndexed { x, c ->
        when (c) {
          '#' -> row.add(Space.FULL)
          '.' -> row.add(Space.EMPTY)
          'S' -> row.add(Space.EMPTY.also { start = Location(y, x) } )
          'E' -> row.add(Space.EMPTY.also { end = Location(y, x) } )
        }
      }
      maze.add(row)
    }

    val visitedInSolutions = mutableSetOf<Location>()
    val cheapest = mutableMapOf<Route, Long>()
    val toVisit = PriorityQueue<Route> { r1, r2 ->
      val d1 = abs(end.y - r1.loc.y) + abs(end.x - r1.loc.x)
      val d2 = abs(end.y - r2.loc.y) + abs(end.x - r2.loc.x)
      when (val n = d1.compareTo(d2)) {
        0    -> r1.cost.compareTo(r2.cost)
        else -> n
      }
    }.apply { add(Route(start, E).withPath(listOf(start))) }
    var cheapestEndSolution = Long.MAX_VALUE

    search@ while(toVisit.isNotEmpty()) {
      val cur = toVisit.poll()

      if (cur.cost > cheapestEndSolution) {
        continue@search
      }

      if (cur.loc == end) {
        if (cur.cost < cheapestEndSolution) {
          visitedInSolutions.clear()
          cheapestEndSolution = min(cheapestEndSolution, cur.cost)
        }
        visitedInSolutions.addAll(cur.path)
        continue@search
      }

      val cheapestToHere = cheapest[cur] ?: Long.MAX_VALUE
      if (cheapestToHere < cur.cost) { continue@search }
      cheapest[cur] = cur.cost

      Direction.entries.forEach { d ->
        val nextSpot = Location(cur.loc.y + d.yDelta, cur.loc.x + d.xDelta)
        if (nextSpot.y in maze.indices &&
            nextSpot.x in maze[nextSpot.y].indices &&
            maze[nextSpot.y][nextSpot.x] == Space.EMPTY &&
            !cur.path.contains(nextSpot)
        ) {
          val extraCost = cur.direction.turnCost(d) + 1
          val newCost = cur.cost + extraCost
          if (newCost <= cheapestEndSolution) {
            val newRoute = Route(nextSpot, d).withCost(newCost).withPath(cur.path.plus(nextSpot))
            toVisit.add(newRoute)
          }
        }
      }
    }

    visitedInSolutions.size
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
    val n = (-1 to 0) to N
    val e = (0 to 1)  to E
    val s = (1 to 0)  to S
    val w = (0 to -1) to W

    return listOf(n, s, e, w).mapNotNull { (loc, dir) ->
      val (y, x) = room.y + loc.first to room.x + loc.second
      if (y !in maze.indices || x !in maze[y].indices) { null }
      else if (maze[y][x].space == Space.FULL) { null }
      else {
        val cost = myDirection.turnCost(dir) + 1
        RoomWithCost(Room(y, x).also { it.direction = dir }, cost).withMaze(maze)
      }
    }
  }

  override fun node(): Room = room

  override fun cost(): Long = cost
}

data class Room(
  val y: Int,
  val x: Int,
  val space: Space = Space.EMPTY
) {
  var direction: Direction? = null
}

enum class Space { FULL, EMPTY }

enum class Direction(val yDelta: Int, val xDelta: Int) {
  N(-1, 0),
  E(0, 1),
  S(1, 0),
  W(0, -1)
}

private fun Direction?.turnCost(direction: Direction): Long {
  if (this == null) return 0
  if (this == direction) return 0

  if (
    this == N && direction == S ||
    this == S && direction == N ||
    this == E && direction == W ||
    this == W && direction == E
  ) return 2000

  return 1000
}

data class Location(val y: Int, val x: Int)

data class Route(val loc: Location, val direction: Direction) {
  var path: List<Location> = emptyList()
  var cost: Long = 0

  fun withPath(p: List<Location>) = apply { this.path = p }
  fun withCost(c: Long) = apply { this.cost = c }
}