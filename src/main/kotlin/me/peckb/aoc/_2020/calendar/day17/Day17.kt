package me.peckb.aoc._2020.calendar.day17

import me.peckb.aoc._2020.calendar.day17.State.ACTIVE
import me.peckb.aoc._2020.calendar.day17.State.INACTIVE
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day17 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    runCycles(input) { y, x -> Point3D(x, y, 0) }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    runCycles(input) { y, x -> Point4D(x, y, 0, 0) }
  }

  private fun runCycles(input: Sequence<String>, pointGenerator: (Int, Int) -> Point): Int {
    val knownSpaces = mutableMapOf<Point, State>()

    input.forEachIndexed { y, row ->
      row.forEachIndexed { x, c ->
        val state = State.fromChar(c)
        knownSpaces[pointGenerator(y, x)] = state
      }
    }

    val initialUniverse = mutableMapOf<Point, State>()
      .apply { putAll(knownSpaces) }

    knownSpaces.forEach { (point, _) ->
      point.neighbors.forEach { neighbor ->
        initialUniverse.putIfAbsent(neighbor, INACTIVE)
      }
    }

    val finalUniverse = (1..6).fold(initialUniverse) { previousUniverse, _ ->
      val nextUniverse = mutableMapOf<Point, State>()

      previousUniverse.forEach { (point, state) ->
        val activeNeighbors = point.neighbors
          .onEach { neighbor -> nextUniverse.putIfAbsent(neighbor, INACTIVE) }
          .count { previousUniverse[it] == ACTIVE }

        when (state) {
          ACTIVE -> {
            nextUniverse[point] = if (activeNeighbors != 2 && activeNeighbors != 3) {
              INACTIVE
            } else {
              ACTIVE
            }
          }

          INACTIVE -> {
            nextUniverse[point] = if (activeNeighbors == 3) {
              ACTIVE
            } else {
              INACTIVE
            }
          }
        }
      }

      nextUniverse
    }

    return finalUniverse.count { it.value == ACTIVE }
  }
}
