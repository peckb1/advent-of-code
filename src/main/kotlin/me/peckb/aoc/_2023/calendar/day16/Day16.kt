package me.peckb.aoc._2023.calendar.day16

import me.peckb.aoc._2023.calendar.day16.Direction.*
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

    val topRow     = (0 until caveSystem.size - 1).map { Location(-1, it) to SOUTH }
    val bottomRow  = (0 until caveSystem.size - 1).map { Location(caveSystem[it].size, it) to NORTH }
    val leftColumn = (0 until caveSystem[0].size - 1).map { Location(it, -1) to EAST }
    val rightColumn= (0 until caveSystem[0].size - 1).map { Location(caveSystem.size, it) to WEST }

    listOf(topRow, bottomRow, leftColumn, rightColumn).flatten().map {
      fireLaser(caveSystem, it)
      countEnergizedNodes(caveSystem).also { reset(caveSystem) }
    }.maxByOrNull { it }
  }

  private fun fireLaser(caveSystem: List<List<CaveArea>>, laserStart : Pair<Location, Direction>) {
    val lasers: Queue<Pair<Location, Direction>> = LinkedList()
    lasers.add(laserStart)

    while(lasers.isNotEmpty()) {
      val laserData = lasers.remove()
      val location = laserData.first

      var direction = laserData.second
      var laserContinuing = true

      while (laserContinuing) {
        location.travel(direction)

        if (caveSystem.indices.contains(location.row) && caveSystem[location.row].indices.contains(location.col)) {
          val area = caveSystem[location.row][location.col]
          val (newDirections, laserStopped, newDirection) = area.enterGoing(direction)

          direction = newDirection
          laserContinuing = !laserStopped
          newDirections.forEach { lasers.add(location.copy() to it) }

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
}
