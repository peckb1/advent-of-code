package me.peckb.aoc._2019.calendar.day15

import kotlinx.coroutines.runBlocking
import me.peckb.aoc._2019.calendar.day15.Day15.Movement.*
import me.peckb.aoc._2019.calendar.day15.Day15.Status.*
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer.Companion.asMutableMap
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.GenericIntDijkstra
import me.peckb.aoc.pathing.GenericIntDijkstra.DijkstraNode

@Suppress("IfThenToElvis", "KotlinConstantConditions")
class Day15 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val (shipArea, oxygenRoom) = mapShip(input)

    val startNode = ShipRoom(START_LOCATION, START_LOCATION)
      .withMap(area = shipArea)
    val endNode = ShipRoom(oxygenRoom.x, oxygenRoom.y)
    val paths =  Dijkstra().solve(startNode, endNode)

    paths[endNode]
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val (shipArea, oxygenRoom) = mapShip(input)

    val startNode = ShipRoom(oxygenRoom.x, oxygenRoom.y)
      .withMap(area = shipArea)
    val paths =  Dijkstra().solve(startNode)

    paths.entries.last().value
  }

  class Dijkstra : GenericIntDijkstra<ShipRoom>()

  data class ShipRoom(val x: Int, val y: Int) : DijkstraNode<ShipRoom> {
    lateinit var area: Array<Array<Status>>

    fun withMap(area: Array<Array<Status>>) = apply {
      this.area = area
    }

    override fun neighbors(): Map<ShipRoom, Int> = listOf(
      y - 1 to x, // NORTH
      y to x + 1, // EAST
      y + 1 to x, // WEST
      y to x - 1, // SOUTH
    ).filter {
      val status = area[it.first][it.second]
      status == EMPTY_ROOM || status == OXYGEN_SYSTEM
    }.map {
      ShipRoom(it.second, it.first).withMap(area)
    }.associateWith { 1 }
  }

  private fun mapShip(input: String): Pair<Array<Array<Status>>, Location> {
    val operations = IntcodeComputer.operations(input).asMutableMap()
    val computer = IntcodeComputer()

    val foundRooms = mutableMapOf<Pair<Int, Int>, Location>()
    val unknownAreasNearKnownLocations = mutableMapOf<Location, ArrayDeque<Movement>>()
    val startLocation = Location(START_LOCATION, START_LOCATION).also { start ->
      unknownAreasNearKnownLocations[start] = ArrayDeque(Movement.validMovements())
    }
    val shipArea = Array(SHIP_SIZE) { Array(SHIP_SIZE) { UNKNOWN } }.also {
      it[startLocation.y][startLocation.x] = EMPTY_ROOM
    }

    var currentLocation = startLocation
    var directionAttemptingToTravel = LOST

    fun userInput(): Long {
      val unknownAreasNearMe = unknownAreasNearKnownLocations[currentLocation] ?:
      throw IllegalStateException("I should not have been travelled to without a list of neighbors (even if empty)")

      val possibleNeighborToVisit = unknownAreasNearMe.removeFirstOrNull()

      return if (possibleNeighborToVisit != null) {
        // explore forward!
        possibleNeighborToVisit.also { directionAttemptingToTravel = it }.code
      } else {
        // backtrack up the "tree"
        val myParent = currentLocation.parent
        if (myParent == null) {
          // we've made it all the way back to the start with no other places to go!
          // if we send an invalid movement code we can stop the program.
          LOST.code
        } else {
          myParent.movementFrom(currentLocation).code
        }
      }
    }

    fun handleOutput(statusCode: Long) {
      when (val travelStatus = Status.fromCode(statusCode)) {
        UNKNOWN -> throw IllegalStateException("We have an unknown travel status!")
        WALL -> {
          currentLocation.move(directionAttemptingToTravel).also { wall ->
            shipArea[wall.y][wall.x] = WALL
            unknownAreasNearKnownLocations[wall] = ArrayDeque()
          }
        }
        EMPTY_ROOM, OXYGEN_SYSTEM -> {
          // we have found a room, is it new or a back track?
          val maybeNewLocation = currentLocation.move(directionAttemptingToTravel)
          if (!unknownAreasNearKnownLocations.contains(maybeNewLocation)) {
            // it's a new location!
            maybeNewLocation.parent = currentLocation
            currentLocation = maybeNewLocation
            shipArea[currentLocation.y][currentLocation.x] = travelStatus
            foundRooms[currentLocation.y to currentLocation.x] = currentLocation

            // let's also add any new places to explore for this room which we have not
            // already had a chance to see
            val movementsThatWouldFindNewRooms = Movement.validMovements()
              .filter { unknownAreasNearKnownLocations[currentLocation.move(it)] == null }
            unknownAreasNearKnownLocations[currentLocation] = ArrayDeque(movementsThatWouldFindNewRooms)
          } else {
            currentLocation = currentLocation.parent!!
          }
        }
      }
    }

    runBlocking {
      computer.runProgram(operations, ::userInput, ::handleOutput)
    }

    val oxygenRoom = foundRooms.values.first { shipArea[it.y][it.x]== OXYGEN_SYSTEM }

    return shipArea to oxygenRoom
  }

  enum class Movement(val code: Long) {
    LOST(0) { override fun oppositeMovement() = LOST },
    NORTH(1) { override fun oppositeMovement() = SOUTH },
    EAST(4) { override fun oppositeMovement() = WEST },
    SOUTH(2) { override fun oppositeMovement() = NORTH },
    WEST(3) { override fun oppositeMovement() = EAST };

    abstract fun oppositeMovement(): Movement

    companion object {
      fun validMovements(): List<Movement> = listOf(NORTH, EAST, SOUTH, WEST)
    }
  }

  enum class Status(val code: Long) {
    UNKNOWN(-1),
    WALL(0),
    EMPTY_ROOM(1),
    OXYGEN_SYSTEM(2);

    companion object {
      fun fromCode(code: Long) = entries.first { it.code == code }
    }
  }

  data class Location(val x: Int, val y: Int) {
    var parent: Location? = null

    fun move(movement: Movement): Location {
      return when (movement) {
        NORTH -> copy(y = y - 1)
        SOUTH -> copy(y = y + 1)
        WEST -> copy(x = x - 1)
        EAST -> copy(x = x + 1)
        LOST -> throw IllegalStateException("I don't know how to move when I'm lost")
      }
    }

    fun movementFrom(sourceDestination: Location): Movement {
      return if (x == sourceDestination.x) {
        if (y < sourceDestination.y) {
          NORTH
        } else if (y > sourceDestination.y){
          SOUTH
        } else {
          throw java.lang.IllegalStateException("Neighbor is me!")
        }
      } else if (y == sourceDestination.y) {
        if (x < sourceDestination.x) {
          WEST
        } else if (x > sourceDestination.x) { // DEV NOTE: technically always true if we get here
          EAST
        } else {
          throw java.lang.IllegalStateException("Neighbor is me!")
        }
      } else {
        throw java.lang.IllegalStateException("Neighbor is more than one 'step' away")
      }
    }
  }

  companion object {
    // Technically these would work with larger values
    // but this happens to be the size of my map XD
    private const val SHIP_SIZE = 41
    private const val START_LOCATION = 21
  }
}
