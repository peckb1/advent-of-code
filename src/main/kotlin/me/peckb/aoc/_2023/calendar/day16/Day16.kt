package me.peckb.aoc._2023.calendar.day16

import me.peckb.aoc._2023.calendar.day16.Day16.Direction.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.util.LinkedList
import java.util.Queue

class Day16 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val caveSystem = mutableListOf<MutableList<CaveArea>>()
    input.forEach { caveSystem.add(it.map(::CaveArea).toMutableList()) }

    fireLaser(caveSystem, Location(0, -1) to EAST)

    countEnergizedNodes(caveSystem)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val caveSystem = mutableListOf<MutableList<CaveArea>>()
    input.forEach { caveSystem.add(it.map(::CaveArea).toMutableList()) }

    val topRow     = (1 until caveSystem.size - 1).map { Location(-1, it) to SOUTH }
    val bottomRow  = (1 until caveSystem.size - 1).map { Location(caveSystem[it].size, it) to NORTH }
    val leftColumn = (1 until caveSystem[0].size - 1).map { Location(it, -1) to EAST }
    val westColumn = (1 until caveSystem[0].size - 1).map { Location(caveSystem.size, it) to WEST }
    val corners = listOf(
      // upper left
      Location(0, -1) to EAST,
      Location(-1, 0) to SOUTH,
      // upper right
      Location(-1, caveSystem[0].size -1) to SOUTH,
      Location(0, caveSystem[0].size) to WEST,
      // lower left
      Location(caveSystem.size - 1, -1) to EAST,
      Location(caveSystem.size, 0) to NORTH,
      // lower right
      Location(caveSystem.size - 1, caveSystem[caveSystem.size - 1].size - 1) to WEST,
      Location(caveSystem.size, caveSystem[caveSystem.size - 1].size) to NORTH,
    )

    listOf(topRow, bottomRow, leftColumn, westColumn, corners).flatten().map {
      fireLaser(caveSystem, it)
      countEnergizedNodes(caveSystem).also { reset(caveSystem) }
    }.maxByOrNull { it }
  }

  fun fireLaser(caveSystem: List<List<CaveArea>>, laserStart : Pair<Location, Direction>) {
    val lasers: Queue<Pair<Location, Direction>> = LinkedList()
    lasers.add(laserStart)

    while(lasers.isNotEmpty()) {
      val laserData = lasers.remove()

      var direction = laserData.second
      val location = laserData.first

      var laserContinuing = true
      while (laserContinuing) {
        location.travel(direction)

        if (caveSystem.indices.contains(location.row) && caveSystem[location.row].indices.contains(location.col)) {
          val area = caveSystem[location.row][location.col]

          when (area.symbol) {
            '|' -> {
              when (direction) {
                NORTH -> {
                  if (area.southEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.southEntranceEnergized = true
                  }
                }
                SOUTH -> {
                  if (area.northEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.northEntranceEnergized = true
                  }
                }
                EAST -> {
                  if (area.westEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.westEntranceEnergized = true
                    lasers.add(location.copy() to NORTH)
                    lasers.add(location.copy() to SOUTH)
                    laserContinuing = false
                  }
                }
                WEST -> {
                  if (area.eastEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.eastEntranceEnergized = true
                    lasers.add(location.copy() to NORTH)
                    lasers.add(location.copy() to SOUTH)
                    laserContinuing = false
                  }
                }
              }
            }

            '-' -> {
              when (direction) {
                NORTH -> {
                  if (area.southEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.southEntranceEnergized = true
                    lasers.add(location.copy() to EAST)
                    lasers.add(location.copy() to WEST)
                    laserContinuing = false
                  }
                }
                SOUTH -> {
                  if (area.northEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.northEntranceEnergized = true
                    lasers.add(location.copy() to EAST)
                    lasers.add(location.copy() to WEST)
                    laserContinuing = false
                  }
                }
                EAST  -> {
                  if (area.westEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.westEntranceEnergized = true
                  }
                }
                WEST  -> {
                  if (area.eastEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.eastEntranceEnergized = true
                  }
                }
              }
            }
            '\\' -> {
              when (direction) {
                NORTH -> {
                  if (area.southEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.southEntranceEnergized = true
                    direction = WEST
                  }
                }
                SOUTH -> {
                  if (area.northEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.northEntranceEnergized = true
                    direction = EAST
                  }
                }
                EAST  -> {
                  if (area.westEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.westEntranceEnergized = true
                    direction = SOUTH
                  }
                }
                WEST  -> {
                  if (area.eastEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.eastEntranceEnergized = true
                    direction = NORTH
                  }
                }
              }
            }
            '/' -> {
              when (direction) {
                NORTH -> {
                  if (area.southEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.southEntranceEnergized = true
                    direction = EAST
                  }
                }
                SOUTH -> {
                  if (area.northEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.northEntranceEnergized = true
                    direction = WEST
                  }
                }
                EAST  -> {
                  if (area.westEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.westEntranceEnergized = true
                    direction = NORTH
                  }
                }
                WEST  -> {
                  if (area.eastEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.eastEntranceEnergized = true
                    direction = SOUTH
                  }
                }
              }
            }
            '.' -> {
              when (direction) {
                NORTH -> {
                  if (area.southEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.southEntranceEnergized = true
                  }
                }
                SOUTH -> {
                  if (area.northEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.northEntranceEnergized = true
                  }
                }
                EAST  -> {
                  if (area.westEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.westEntranceEnergized = true
                  }
                }
                WEST  -> {
                  if (area.eastEntranceEnergized) {
                    laserContinuing = false
                  } else {
                    area.eastEntranceEnergized = true
                  }
                }
              }
            }
            else -> throw IllegalStateException("Unknown CaveArea [$area]")
          }
        } else {
          laserContinuing = false
        }
      }
    }
  }

  private fun reset(caveSystem: MutableList<MutableList<CaveArea>>) {
    caveSystem.forEach { row ->
      row.forEach { it.reset() }
    }
  }

  private fun countEnergizedNodes(caveSystem: List<List<CaveArea>>): Long {
    return caveSystem.sumOf { row ->
      row.sumOf { caveArea ->
        if (caveArea.isEnergized()) 1L else 0L
      }
    }
  }

  data class CaveArea(
    val symbol: Char,
    var eastEntranceEnergized: Boolean = false,
    var westEntranceEnergized: Boolean = false,
    var northEntranceEnergized: Boolean = false,
    var southEntranceEnergized: Boolean = false,
  ) {
    fun isEnergized(): Boolean {
      return eastEntranceEnergized || westEntranceEnergized || northEntranceEnergized || southEntranceEnergized
    }

    fun reset() {
      eastEntranceEnergized = false
      westEntranceEnergized = false
      northEntranceEnergized = false
      southEntranceEnergized = false
    }

    override fun toString(): String {
      return if (isEnergized()) {
        "#"
      } else {
        "."
      }
    }
  }

  enum class Direction {
    NORTH, SOUTH, EAST, WEST
  }

  data class Location(var row: Int, var col: Int) {
    fun travel(direction: Direction) {
      when (direction) {
        NORTH -> row--
        SOUTH -> row++
        EAST  -> col++
        WEST  -> col--
      }
    }
  }
}
