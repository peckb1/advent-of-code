package me.peckb.aoc._2024.calendar.day06

import me.peckb.aoc._2024.calendar.day06.Direction.*
import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day06 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { lines ->
    val area = mutableListOf<MutableList<Space>>()
    var guard = Guard(Direction.NORTH, Location(-1, -1))

    val spacesWalkedOn = mutableSetOf<Location>()

    lines.forEachIndexed { yIndex, line ->
      val row = mutableListOf<Space>()
      line.forEachIndexed { xIndex, char ->
        when (char) {
          '.' -> row.add(Space.EMPTY)
          '#' -> row.add(Space.BLOCKED)
          '^' -> {
            row.add(Space.EMPTY)
            guard = Guard(Direction.NORTH, Location(yIndex, xIndex))
          }
        }
      }
      area.add(row)
    }

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

    spacesWalkedOn.size
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { lines ->
    val area = mutableListOf<MutableList<Space>>()
    var guard = Guard(Direction.NORTH, Location(-1, -1))

    val spacesWalkedOn = mutableMapOf<Location, Set<Direction>>()

    lines.forEachIndexed { yIndex, line ->
      val row = mutableListOf<Space>()
      line.forEachIndexed { xIndex, char ->
        when (char) {
          '.' -> row.add(Space.EMPTY)
          '#' -> row.add(Space.BLOCKED)
          '^' -> {
            row.add(Space.EMPTY)
            guard = Guard(Direction.NORTH, Location(yIndex, xIndex))
          }
        }
      }
      area.add(row)
    }

    val guardStart = guard.copy()

    area.indices.sumOf { y ->
      println("$y")
      area[y].indices.count { x ->
        var stepCount = 0
        if (guardStart.location.y == y && guardStart.location.x == x) { false } else {
          val original = area[y][x]
          area[y][x] = Space.BLOCKED
          while(stepCount < 10000 && area.containsLocation(guard.location.y, guard.location.x)) {
            val nextStep = guard.direction.step(guard.location.y, guard.location.x)
            if (area.containsLocation(nextStep.first, nextStep.second)) {
              when (area[nextStep.first][nextStep.second]) {
                Space.EMPTY   -> guard = Guard(guard.direction, Location(nextStep.first, nextStep.second))
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
//
//    var spotCount = 0
//
//    var backupSpot = guard.backup()
//    while(area.containsLocation(backupSpot.first, backupSpot.second) && area[backupSpot.first][backupSpot.second] == Space.EMPTY) {
//      spacesWalkedOn.merge(Location(backupSpot.first, backupSpot.second), setOf(guard.direction)) { a, b -> a.plus(b) }
//      backupSpot = Guard(guard.direction, Location(backupSpot.first, backupSpot.second)).backup()
//    }

//    while(area.containsLocation(guard.location.y, guard.location.x)) {
//      spacesWalkedOn.merge(guard.location, setOf(guard.direction)) { a, b -> a.plus(b) }
//
//      val nextStep = guard.direction.step(guard.location.y, guard.location.x)
//
//      // if I turn right here, would I be in a spot/direction I've been before?
//      val turnDirection = guard.direction.turnRight()
//      val directionInStep = turnDirection.step(guard.location.y, guard.location.x)
//      val newWallInFront = spacesWalkedOn.get(Location(directionInStep.first, directionInStep.second))?.contains(turnDirection) ?: false
//
//      if (newWallInFront) { spotCount++ }
//
//      if (area.containsLocation(nextStep.first, nextStep.second)) {
//        when (area[nextStep.first][nextStep.second]) {
//          Space.EMPTY   -> guard = Guard(guard.direction, Location(nextStep.first, nextStep.second))
//          Space.BLOCKED -> {
//            guard = Guard(guard.direction.turnRight(), guard.location)
//            backupSpot = guard.backup()
//            while(area.containsLocation(backupSpot.first, backupSpot.second) && area[backupSpot.first][backupSpot.second] == Space.EMPTY) {
//              spacesWalkedOn.merge(Location(backupSpot.first, backupSpot.second), setOf(guard.direction)) { a, b -> a.plus(b) }
//              backupSpot = Guard(guard.direction, Location(backupSpot.first, backupSpot.second)).backup()
//            }
//          }
//        }
//      } else {
//        guard = Guard(guard.direction, Location(nextStep.first, nextStep.second))
//      }
//    }

//    spotCount
  }
}

private fun List<List<*>>.containsLocation(y: Int, x: Int): Boolean {
  return y in indices && x in this[y].indices
}

data class Guard(val direction: Direction, val location: Location) {
  fun backup(): Pair<Int, Int> {
    return when (direction) {
      NORTH -> location.y+1 to location.x
      EAST  -> location.y to location.x-1
      SOUTH -> location.y-1 to location.x
      WEST  -> location.y to location.x+1
    }
  }
}

enum class Direction {
  NORTH {
    override fun turnRight(): Direction {
      return EAST
    }
  },
  EAST {
    override fun turnRight(): Direction {
      return SOUTH
    }
  },
  SOUTH {
    override fun turnRight(): Direction {
      return WEST
    }
  },
  WEST {
    override fun turnRight(): Direction {
      return NORTH
    }
  };

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

data class Location(val y: Int, val x: Int) {
  fun inside(area: MutableList<MutableList<Space>>): Boolean {
    return area.containsLocation(y, x)
  }
}

enum class Space { EMPTY, BLOCKED }
