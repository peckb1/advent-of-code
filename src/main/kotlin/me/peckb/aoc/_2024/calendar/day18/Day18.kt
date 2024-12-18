package me.peckb.aoc._2024.calendar.day18

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.GenericIntDijkstra

class Day18 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::location) { locations ->
    val area = Array(HEIGHT) { Array(WIDTH) { Memory.EMPTY } }

    locations.take(1024).forEach { (y, x) ->
      area[y][x] = Memory.CORRUPTED
    }

    val solver = object : GenericIntDijkstra<Location>() { }

    val solutions = solver.solve(
      Location(0, 0).withArea(area).withPreviousPath(emptyList()),
      Location(HEIGHT - 1, WIDTH- 1)
    )

    solutions[Location(HEIGHT - 1, WIDTH- 1)]
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::location) { locations ->
    val area = Array(HEIGHT) { Array(WIDTH) { Memory.EMPTY } }
    val start = Location(0, 0).withArea(area).withPreviousPath(emptyList())
    val end = Location(HEIGHT - 1, WIDTH - 1)

    val locationList = locations.toList()

    var counter = 1024
    locationList.take(1024).forEach { (y, x) -> area[y][x] = Memory.CORRUPTED }

    var solutions: Map<Location, Int> = emptyMap()
    val solver = object : GenericIntDijkstra<Location>() {}
    search@ do {
      val next = locationList[counter++]
      area[next.y][next.x] = Memory.CORRUPTED

      if (solutions.entries.firstOrNull { it.key == end }?.key?.path?.contains(next) == false) {
        continue@search
      }

      solutions = solver.solve(start, end)
    } while(solutions[end] != null)

    locationList[counter - 1].let { "${it.x},${it.y}" }
  }

  private fun location(line: String) = line.split(",")
    .map { it.toInt() }
    .let { (x, y) -> Location(y, x) }

  companion object {
    const val WIDTH  = 71
    const val HEIGHT = 71
  }
}

data class Location(val y: Int, val x: Int) : GenericIntDijkstra.DijkstraNode<Location> {
  lateinit var path: List<Location>
  lateinit var area: Array<Array<Memory>>

  fun withArea(area: Array<Array<Memory>>)   = apply { this.area = area }
  fun withPreviousPath(path: List<Location>) = apply { this.path = path.plus(this) }

  override fun neighbors(): Map<Location, Int> {
    return Direction.entries.mapNotNull { d ->
      val (newY, newX) = d.yDelta + y to d.xDelta + x
      if (newY in area.indices && newX in area[newY].indices && area[newY][newX] == Memory.EMPTY) {
        Location(newY, newX).withArea(area).withPreviousPath(path)
      } else { null }
    }.associateWith { 1 }
  }
}

enum class Direction(val yDelta: Int, val xDelta: Int) {
  N(-1, 0),
  E(0, 1),
  S(1, 0),
  W(0, -1);
}

enum class Memory(val s: String) {
  EMPTY("."), CORRUPTED("#");

  override fun toString(): String = s
}
