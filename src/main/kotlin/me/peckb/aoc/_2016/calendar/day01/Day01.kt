package me.peckb.aoc._2016.calendar.day01

import me.peckb.aoc._2016.calendar.day01.Day01.FacingDirection.EAST
import me.peckb.aoc._2016.calendar.day01.Day01.FacingDirection.NORTH
import me.peckb.aoc._2016.calendar.day01.Day01.FacingDirection.SOUTH
import me.peckb.aoc._2016.calendar.day01.Day01.FacingDirection.WEST
import me.peckb.aoc._2016.calendar.day01.Day01.TurnDirection.LEFT
import me.peckb.aoc._2016.calendar.day01.Day01.TurnDirection.RIGHT
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.abs

class Day01 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val location = Location(NORTH, 0, 0)

    val paths = input.split(", ").asSequence().map {
      val turnDirection = if (it[0] == 'L') LEFT else RIGHT
      val distance = it.substring(1).toInt()
      Path(turnDirection, distance)
    }

    paths.forEach { location.walk(it) }

    abs(location.x) + abs(location.y)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val location = Location(NORTH, 0, 0)

    val paths = input.split(", ").asSequence().map {
      val turnDirection = if (it[0] == 'L') LEFT else RIGHT
      val distance = it.substring(1).toInt()
      Path(turnDirection, distance)
    }

    var doubleLocation: Pair<Int, Int> = 0 to 0
    val locations: MutableSet<Pair<Int, Int>> = mutableSetOf(doubleLocation)
    paths.firstOrNull { path ->
      location.walk(path).any {
        if (locations.contains(it)) {
          doubleLocation = it
          true
        } else {
          locations.add(it)
          false
        }
      }
    }

    abs(doubleLocation.first) + abs(doubleLocation.second)
  }

  data class Path(val turnDirection: TurnDirection, val distance: Int)

  enum class TurnDirection { LEFT, RIGHT }

  enum class FacingDirection { NORTH, EAST, SOUTH, WEST }

  data class Location(var facingDirection: FacingDirection, var y: Int, var x: Int) {
    fun walk(path: Path): List<Pair<Int, Int>> {
      val locations = mutableListOf<Pair<Int, Int>>()

      val walkDistance = (1..path.distance)
      fun walkY(walker: (Int, Int) -> Int) = walkDistance.forEach { s -> locations.add(Pair(walker(y, s), x)) }
      fun walkX(walker: (Int, Int) -> Int) = walkDistance.forEach { s -> locations.add(Pair(y, walker(x, s))) }

      when (path.turnDirection) {
        LEFT -> {
          when (facingDirection) {
            NORTH -> { facingDirection = WEST; walkX(Int::minus); x -= path.distance }
            WEST -> { facingDirection = SOUTH; walkY(Int::minus); y -= path.distance }
            SOUTH -> { facingDirection = EAST; walkX(Int::plus); x += path.distance }
            EAST -> { facingDirection = NORTH; walkY(Int::plus); y += path.distance }
          }
        }
        RIGHT -> {
          when (facingDirection) {
            NORTH -> { facingDirection = EAST; walkX(Int::plus); x += path.distance}
            EAST -> { facingDirection = SOUTH; walkY(Int::minus); y -= path.distance }
            SOUTH -> { facingDirection = WEST; walkX(Int::minus); x -= path.distance }
            WEST -> { facingDirection = NORTH; walkY(Int::plus); y += path.distance }
          }
        }
      }

      return locations
    }
  }
}
