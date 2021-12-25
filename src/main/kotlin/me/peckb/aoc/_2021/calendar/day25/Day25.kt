package me.peckb.aoc._2021.calendar.day25

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import me.peckb.aoc._2021.calendar.day25.Day25.Direction.EAST
import me.peckb.aoc._2021.calendar.day25.Day25.Direction.SOUTH
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day25 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    const val COROUTINES = 1000
  }

  enum class Direction {
    EAST, SOUTH
  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val world = setupWorld(input)

    var cucumbersStillMoving = true
    var waitTime = 0

    while(cucumbersStillMoving) {
      waitTime++

      val eastMoves = world.floor.moveCucumbers(world.eastCucumbers)
      val southMoves = world.floor.moveCucumbers(world.southCucumbers)

      cucumbersStillMoving = eastMoves || southMoves
    }

    waitTime
  }

  private fun setupWorld(input: Sequence<String>): World {
    val eastFacingCucumbers = mutableListOf<SeaCucumber>()
    val southFacingCucumbers = mutableListOf<SeaCucumber>()

    val encodedWorld = input.toList()
    val floor = Array(encodedWorld.size) { Array<SeaCucumber?>(encodedWorld[0].length) { null } }

    encodedWorld.indices.forEach { y ->
      encodedWorld[y].forEachIndexed { x, c ->
        when (c) {
          'v' -> SeaCucumber(SOUTH, y, x).apply {
            southFacingCucumbers.add(this)
            floor[y][x] = this
          }
          '>' -> SeaCucumber(EAST, y, x).apply {
            eastFacingCucumbers.add(this)
            floor[y][x] = this
          }
        }
      }
    }

    return World(floor, eastFacingCucumbers, southFacingCucumbers)
  }

  private fun Array<Array<SeaCucumber?>>.moveCucumbers(cucumbers: List<SeaCucumber>): Boolean {
    val floor = this

    return runBlocking {
      val movements =
        cucumbers.chunked(COROUTINES).map {
          async {
            it.map { it.movement(floor) }
          }
        }.awaitAll()

      val movesMade = movements.flatMap { movementList ->
        movementList.mapNotNull { it?.invoke() }
      }
      movesMade.isNotEmpty()
    }
  }

  class World(val floor: Array<Array<SeaCucumber?>>, val eastCucumbers: List<SeaCucumber>, val southCucumbers: List<SeaCucumber>)

  data class SeaCucumber(val direction: Direction, var y: Int, var x: Int) {
    fun movement(world: Array<Array<SeaCucumber?>>): (() -> Unit)? {
      return when (direction) {
        EAST -> {
          val newX = (x + 1) % world[y].size
          if (world[y][newX] == null) {
            {
              world[y][newX] = this
              world[y][x] = null
              this.x = newX
            }
          } else { null }
        }
        SOUTH -> {
          val newY = (y + 1) % world.size
          if (world[newY][x] == null) {
            {
              world[newY][x] = this
              world[y][x] = null
              this.y = newY
            }
          } else { null }
        }
      }
    }
  }
}
