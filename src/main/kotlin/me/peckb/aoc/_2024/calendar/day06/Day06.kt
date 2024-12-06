package me.peckb.aoc._2024.calendar.day06

import me.peckb.aoc._2024.calendar.day06.Direction.NORTH
import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day06 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { lines ->
    val (area, guard) = setup(lines)

    stepsWalkedByGuard(area, guard).size
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { lines ->
    val (area, guardStart) = setup(lines)

    var guard = guardStart.copy()

    val steps = stepsWalkedByGuard(area, guard)

    steps.count { (y, x) ->
      var stepCount = 0
      if (guardStart.location.y == y && guardStart.location.x == x) {
        false
      } else {
        val original = area[y][x]
        area[y][x] = Space.BLOCKED

        while (stepCount < 10000 && area.containsLocation(guard.location.y, guard.location.x)) {
          val nextStep = guard.direction.step(guard.location.y, guard.location.x)
          if (area.containsLocation(nextStep.first, nextStep.second)) {
            when (area[nextStep.first][nextStep.second]) {
              Space.EMPTY -> guard = Guard(guard.direction, Location(nextStep.first, nextStep.second))
              Space.BLOCKED -> guard = Guard(guard.direction.turnRight(), guard.location)
            }
          } else {
            guard = Guard(guard.direction, Location(nextStep.first, nextStep.second))
          }
          stepCount++
        }

        guard = guardStart.copy()
        area[y][x] = original

        stepCount == 10000
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

  private fun stepsWalkedByGuard(area: List<List<Space>>, guardStart: Guard) : Set<Location> {
    var guard = guardStart

    val spacesWalkedOn = mutableSetOf<Location>()

    while(area.containsLocation(guard.location.y, guard.location.x)) {
      spacesWalkedOn.add(guard.location)
      val nextStep = guard.direction.step(guard.location.y, guard.location.x)
      if (area.containsLocation(nextStep.first, nextStep.second)) {
        when (area[nextStep.first][nextStep.second]) {
          Space.EMPTY   -> guard = Guard(guard.direction, Location(nextStep.first, nextStep.second))
          Space.BLOCKED -> guard = Guard(guard.direction.turnRight(), guard.location)
        }
      } else {
        guard = Guard(guard.direction, Location(nextStep.first, nextStep.second))
      }
    }

    return spacesWalkedOn
  }
}


private fun List<List<*>>.containsLocation(y: Int, x: Int): Boolean {
  return y in indices && x in this[y].indices
}

data class Guard(val direction: Direction, val location: Location)

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

data class Location(val y: Int, val x: Int)

enum class Space { EMPTY, BLOCKED }
