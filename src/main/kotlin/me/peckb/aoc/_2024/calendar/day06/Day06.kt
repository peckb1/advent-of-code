package me.peckb.aoc._2024.calendar.day06

import me.peckb.aoc._2024.calendar.day06.Direction.NORTH
import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day06 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  companion object {
    const val MAX_STEPS = 8000 // minor optimization - 10k worked well, but this saved time
  }

  fun partOne(filename: String) = generatorFactory.forFile(filename).read { lines ->
    val (area, guard) = setup(lines)

    val steps = mutableSetOf<Location>()
    walk(area, guard, step = steps::add)

    steps.size
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { lines ->
    val (area, guardStart) = setup(lines)

    var guard = Guard(guardStart.direction, guardStart.location.copy())
    val steps = mutableSetOf<Location>()

    walk(area, guard, step = steps::add)

    steps.count { (y, x) ->
      if (guardStart.location.y == y && guardStart.location.x == x) {
        false
      } else {
        var stepCount = 0
        area[y][x] = Space.BLOCKED

        walk(area, guard, totalStepHandler = { stepCount = it})

        guard = Guard(guardStart.direction, guardStart.location.copy())
        area[y][x] = Space.EMPTY

        stepCount == MAX_STEPS
      }
    }
  }

  private fun setup(lines: Sequence<String>): Pair<MutableList<MutableList<Space>>, Guard> {
    val area = mutableListOf<MutableList<Space>>()
    var guard = Guard(NORTH, Location(-1, -1))

    lines.forEachIndexed { yIndex, line ->
      val row = mutableListOf<Space>()
      line.forEachIndexed { xIndex, char ->
        when (char) {
          '.' -> row.add(Space.EMPTY)
          '#' -> row.add(Space.BLOCKED)
          '^' -> {
            row.add(Space.EMPTY)
            guard = Guard(NORTH, Location(yIndex, xIndex))
          }
        }
      }
      area.add(row)
    }

    return area to guard
  }

  private fun walk(
    area: List<List<Space>>,
    guardStart: Guard,
    step: (Location) -> Unit = { },
    totalStepHandler: (Int) -> Unit = { }
  ) {
    val guard = Guard(guardStart.direction, guardStart.location.copy())

    var stepCount = 0

    while (stepCount < MAX_STEPS && area.containsLocation(guard.location.y, guard.location.x)) {
      val (nextY, nextX) = guard.direction.step(guard.location.y, guard.location.x)
      step(guard.location.copy())

      if (area.containsLocation(nextY, nextX) && area[nextY][nextX] == Space.BLOCKED) {
        guard.turnRight()
      } else {
        guard.stepForward()
      }

      stepCount++
    }

    totalStepHandler(stepCount)
  }
}


private fun List<List<*>>.containsLocation(y: Int, x: Int): Boolean {
  return y in indices && x in this[y].indices
}

data class Guard(var direction: Direction, val location: Location) {
  fun stepForward() { location.step(direction) }

  fun turnRight() { direction = direction.turnRight() }
}

enum class Direction {
  NORTH { override fun turnRight(): Direction { return EAST } },
  EAST { override fun turnRight(): Direction { return SOUTH } },
  SOUTH { override fun turnRight(): Direction { return WEST } },
  WEST { override fun turnRight(): Direction { return NORTH } };

  abstract fun turnRight(): Direction

  fun step(y: Int, x: Int): Pair<Int, Int> {
    return when (this) {
      NORTH -> y - 1 to x
      EAST  -> y     to x + 1
      SOUTH -> y + 1 to x
      WEST  -> y     to x - 1
    }
  }
}

data class Location(var y: Int, var x: Int) {
  fun step(direction: Direction) {
    direction.step(y, x).also { (newY, newX) ->
      y = newY
      x = newX
    }
  }
}

enum class Space { EMPTY, BLOCKED }
