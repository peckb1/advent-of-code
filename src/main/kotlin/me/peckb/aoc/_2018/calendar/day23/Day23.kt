package me.peckb.aoc._2018.calendar.day23

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day23 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::nanobot) { input ->
    val botsByPower = input.sortedByDescending { it.radius }.toList()
    val strongest = botsByPower.first()

    botsByPower.count { them -> strongest.point.distanceTo(them.point) <= strongest.radius }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::nanobot) { input ->
    val nanobots = input.toList()

    // we only have one starting robot, and a range of HUGE
    var currentBots = setOf(ORIGIN)
    var currentRadius = Int.MAX_VALUE.toLong()

    while (currentRadius > 0) {
      // we start by cutting our range in half each time
      currentRadius /= 2

      val newGeneration = currentBots.flatMap { centerNanobot ->
        // for each nanobot we originally knew about we create 27 new points. One
        // at each single step away, and one at our point
        // i.e. single square -> rubik's cube centered at square with a distance of currentRadius
        val neighborsWithinRange = centerNanobot.point.neighbors(currentRadius)

        neighborsWithinRange.map { point ->
          // for each of those 27 new cubes, we create a new nanobot at that point
          val cubeNanobot = Nanobot(point, currentRadius)
          // find out how many "real" nanobots this new nanobot have intersecting spheres
          val intersections = nanobots.count { cubeNanobot.intersects(it) }
          // keep track of it and the count
          cubeNanobot to intersections
        }
      }
      // find out which of the cube areas has the most intersecting cubes
      val maxIntersections = newGeneration.maxOfOrNull { it.second } ?: 0

      // any nanobot which matches that max intersection number gets to be in the next set of people
      // essentially making smaller and smaller radius nanobots until we've hit 0, finding nanobots
      // which, when their radius is 0, they are still maximally touching other nanobots
      currentBots = newGeneration.asSequence()
        .filter { it.second == maxIntersections }
        .map { it.first }
        .toSet()
    }

    // then for any point which has the maximum number of intersections
    // grab one, and then return the distance from us to it
    currentBots.firstOrNull()
      ?.point
      ?.distanceTo(ORIGIN.point)
  }

  private fun nanobot(line: String): Nanobot {
    // pos=<0,0,0>, r=4
    val (x, y, z) = line.substringAfter("pos=<").substringBefore(">").split(",").map(String::toLong)
    val radius = line.substringAfter("r=").toLong()

    return Nanobot(Point(x, y, z), radius)
  }

  companion object {
    private val ORIGIN = Nanobot(Point(0, 0, 0), 0)
  }
}
