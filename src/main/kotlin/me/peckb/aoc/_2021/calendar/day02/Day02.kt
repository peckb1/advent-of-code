package me.peckb.aoc._2021.calendar.day02

import me.peckb.aoc._2021.calendar.day02.Day02.Path.Direction.FORWARD
import me.peckb.aoc._2021.calendar.day02.Day02.Path.Direction.DOWN
import me.peckb.aoc._2021.calendar.day02.Day02.Path.Direction.UP
import me.peckb.aoc._2021.generators.InputGenerator
import javax.inject.Inject
import kotlin.math.max

class Day02 @Inject constructor(private val generatorFactory: InputGenerator.InputGeneratorFactory) {
  fun travel(filename: String) = generatorFactory.forFile(filename).readAs(::path) { input ->
    val restingPlace = Location().apply { input.forEach(this::move) }

    restingPlace.depth * restingPlace.distance
  }

  fun travelAndAim(filename: String) = generatorFactory.forFile(filename).readAs(::path) { input ->
    val restingPlace = LocationAndAim().apply { input.forEach(this::move) }

    restingPlace.depth * restingPlace.distance
  }

  private fun path(line: String) = Path.fromLine(line)

  private data class Path(val direction: Direction, val distance: Int) {
    companion object {
      fun fromLine(dataLine: String): Path {
        val dataParts = dataLine.split(" ")
        return Path(Direction.valueOf(dataParts.first().uppercase()), dataParts.last().toInt())
      }
    }

    enum class Direction {
      FORWARD, DOWN, UP
    }
  }

  private class Location {
    var distance: Int = 0; private set
    var depth: Int = 0; private set

    fun move(path: Path) {
      when (path.direction) {
        FORWARD -> distance += path.distance
        DOWN -> depth += path.distance
        UP -> depth = max(0, depth - path.distance)
      }
    }
  }

  private class LocationAndAim {
    var distance: Int = 0; private set
    var depth: Int = 0; private set
    private var aim: Int = 0

    fun move(path: Path) {
      when (path.direction) {
        FORWARD -> {
          distance += path.distance
          depth += max(0, path.distance * aim)
        }
        DOWN -> aim += path.distance
        UP -> aim -= path.distance
      }
    }
  }
}
