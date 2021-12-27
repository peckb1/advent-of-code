package me.peckb.aoc._2021.calendar.day23

import me.peckb.aoc._2021.calendar.day23.Movement.EnterHallFromRoom
import me.peckb.aoc._2021.calendar.day23.Movement.EnterRoomFromHall
import me.peckb.aoc._2021.calendar.day23.Movement.EnterRoomFromRoom
import java.util.PriorityQueue
import java.util.concurrent.atomic.AtomicInteger
import kotlin.Int.Companion.MAX_VALUE
import kotlin.math.min

class Floor(val hallway: Array<Resident?>, val rooms: Map<Resident, Array<Resident?>>) {
  fun cloneFloor() = Floor(hallway.copyOf(), rooms.mapValues { it.value.copyOf() })

  fun availableMoves(): PriorityQueue<Movement> {
    val queue = PriorityQueue<Movement>(compareBy { it.cost })

    generateEnterRoomFromHallMoves().forEach { queue.add(it) }
    val roomToRoomMoves = generateEnterRoomFromRoomMoves().onEach { queue.add(it) }
    generateEnterHallFromRoomMoves(roomToRoomMoves).forEach { queue.add(it) }

    return queue
  }

  fun makeMove(movement: Movement) {
    when (movement) {
      is EnterHallFromRoom -> {
        hallway[movement.hallIndex] = rooms[movement.roomOwner]!![movement.roomIndex]
        rooms[movement.roomOwner]!![movement.roomIndex] = null
      }
      is EnterRoomFromHall -> {
        rooms[movement.roomOwner]!![movement.roomIndex] = hallway[movement.hallIndex]
        hallway[movement.hallIndex] = null
      }
      is EnterRoomFromRoom -> {
        rooms[movement.destinationRoomOwner]!![movement.destinationRoomIndex] = rooms[movement.sourceRoomOwner]!![movement.sourceRoomIndex]
        rooms[movement.sourceRoomOwner]!![movement.sourceRoomIndex] = null
      }
    }
  }

  fun undoMove(movement: Movement) {
    when (movement) {
      is EnterHallFromRoom -> {
        rooms[movement.roomOwner]!![movement.roomIndex] = hallway[movement.hallIndex]
        hallway[movement.hallIndex] = null
      }
      is EnterRoomFromHall -> {
        hallway[movement.hallIndex] = rooms[movement.roomOwner]!![movement.roomIndex]
        rooms[movement.roomOwner]!![movement.roomIndex] = null
      }
      is EnterRoomFromRoom -> {
        rooms[movement.sourceRoomOwner]!![movement.sourceRoomIndex] = rooms[movement.destinationRoomOwner]!![movement.destinationRoomIndex]
        rooms[movement.destinationRoomOwner]!![movement.destinationRoomIndex] = null
      }
    }
  }

  fun findCheapestRearrangement(currentMoves: ArrayDeque<Movement>, costSoFar: Int, cheapestCost: AtomicInteger) : Pair<Int, List<Movement>>? {
    val moves = availableMoves()

    var cheapestRouteFound: Pair<Int, List<Movement>>? = null

    moveCheck@ while (moves.isNotEmpty()) {
      val nextMove = moves.remove()
      if (costSoFar + nextMove.cost > cheapestCost.get()) {
        continue@moveCheck
      }

      makeMove(nextMove)
      currentMoves.add(nextMove)

      if (isComplete()) {
        return ((costSoFar + nextMove.cost) to currentMoves.toList()).also {
          cheapestCost.getAndUpdate { current -> min(current, it.first) }
          currentMoves.removeLast()
          undoMove(nextMove)
        }
      } else {
        val possibleRoute = findCheapestRearrangement(currentMoves, costSoFar + nextMove.cost, cheapestCost)
        if (cheapestRouteFound == null) {
          cheapestRouteFound = possibleRoute
        }
        else if (cheapestRouteFound.first > possibleRoute?.first ?: MAX_VALUE) {
          cheapestRouteFound = possibleRoute
        }
      }

      currentMoves.removeLast()
      undoMove(nextMove)
    }

    return cheapestRouteFound
  }

  private fun generateEnterHallFromRoomMoves(roomToRoomMoves: List<EnterRoomFromRoom>): List<EnterHallFromRoom> {
    val peopleToIgnore = roomToRoomMoves.map { it.sourceRoomOwner to it.sourceRoomIndex }

    return mutableListOf<EnterHallFromRoom>().apply {
      rooms.forEach { room ->
        room.value.forEachIndexed { roomIndex, resident ->
          if (!peopleToIgnore.contains(resident to roomIndex)) {
            resident?.let { addAll(findValidEnterHallwayMovements(room.key, room.key.doorwayIndex, roomIndex, it)) }
          }
        }
      }
    }
  }

  private fun findValidEnterHallwayMovements(roomOwner: Resident, hallIndex: Int, roomIndex: Int, resident: Resident): List<EnterHallFromRoom> {
    // confirm that I want to leave
    val nonOwnersBehindMe = (roomIndex+1 until rooms[roomOwner]!!.size).any { rooms[roomOwner]!![it] != roomOwner }
    val shouldLeave = resident != roomOwner || nonOwnersBehindMe
    if (!shouldLeave) return emptyList()

    // confirm that the room from me to doorway is empty
    val neighborsInMyWay = (0 until roomIndex).mapNotNull { rooms[roomOwner]!![it] }
    val pathToDoorwayClear = neighborsInMyWay.isEmpty() && hallway[hallIndex] == null
    if (!pathToDoorwayClear) return emptyList()

    val costToEnterDoorway = resident.movementCost * (roomIndex + 1)
    val movements = mutableListOf<EnterHallFromRoom>()

    // see if we can walk to the left outside of the doorway
    val leftSteps = hallIndex - 1 downTo 0
    val rightSteps = hallIndex + 1 until hallway.size

    movements.tryFindEnterHallFromRoomMovements(resident, costToEnterDoorway, roomOwner, roomIndex, leftSteps)
    movements.tryFindEnterHallFromRoomMovements(resident, costToEnterDoorway, roomOwner, roomIndex, rightSteps)

    return movements
  }

  private fun MutableList<EnterHallFromRoom>.tryFindEnterHallFromRoomMovements(resident: Resident, costToEnterDoorway: Int, roomOwner: Resident, roomIndex: Int, possibleValues: IntProgression) {
    var steps = 0
    hallwaySearch@ for (possibleHallIndex in possibleValues) {
      steps++
      if (hallway[possibleHallIndex] == null) {
        val cost = (steps * resident.movementCost) + costToEnterDoorway
        add(EnterHallFromRoom(roomOwner, roomIndex, possibleHallIndex, cost))
      } else {
        break@hallwaySearch
      }
    }
  }

  private fun generateEnterRoomFromRoomMoves(): List<EnterRoomFromRoom> {
    return mutableListOf<EnterRoomFromRoom>().apply {
      rooms.forEach { room ->
        room.value.forEachIndexed { roomIndex, resident ->
          resident?.also { findValidRoomTransferMovements(room.key, room.key.doorwayIndex, roomIndex, it)?.also(::add) }
        }
      }
    }
  }

  private fun findValidRoomTransferMovements(roomOwner: Resident, hallIndex: Int, roomIndex: Int, resident: Resident): EnterRoomFromRoom? {
    // confirm that I want to leave
    val imInMyOwnRoom = roomOwner == resident
    val nonOwnersBehindMe = (roomIndex+1 until rooms[roomOwner]!!.size).mapNotNull { rooms[roomOwner]!![it] }
    val shouldLeave = !imInMyOwnRoom && (resident != roomOwner || nonOwnersBehindMe.isNotEmpty())
    if (!shouldLeave) return null

    // confirm that the room from me to doorway is empty
    val neighborsInMyWay = (0 until roomIndex).mapNotNull { rooms[roomOwner]!![it] }
    val pathToDoorwayClear = neighborsInMyWay.isEmpty() && hallway[hallIndex] == null
    if (!pathToDoorwayClear) return null

    // confirm that the path from my room to my target room is clear
    val pathToTargetRoom = roomOwner.pathToRoom(resident)
    val someoneInTheWay = pathToTargetRoom.any { hallway[it] != null }
    if (someoneInTheWay) return null

    // confirm that the only existing people in my target room want to stay there
    val nonResidentsPresent = rooms[resident]!!.any { it != null && it != resident }
    if (nonResidentsPresent) return null

    val targetRoomIndex = ((rooms[resident]!!.size - 1) downTo 0).first { rooms[resident]!![it] == null }
    val cost = (pathToTargetRoom.count() + targetRoomIndex + roomIndex + 1) * resident.movementCost
    return EnterRoomFromRoom(roomOwner, roomIndex, resident, targetRoomIndex, cost)
  }

  private fun generateEnterRoomFromHallMoves(): List<EnterRoomFromHall> {
    return mutableListOf<EnterRoomFromHall>().apply {
      hallway.forEachIndexed { hallwayIndex, hallwaySpace ->
        hallwaySpace?.also { findValidEnterRoomMovements(hallwayIndex, it)?.also(::add) }
      }
    }
  }

  private fun findValidEnterRoomMovements(hallIndex: Int, resident: Resident): EnterRoomFromHall? {
    // confirm that the path to my target room is clear
    val step = if (hallIndex < resident.doorwayIndex) 1 else -1
    val pathToTargetRoom = IntProgression.fromClosedRange(hallIndex + step, resident.doorwayIndex, step)
    val neighborsInMyWay = pathToTargetRoom.any { hallway[it] != null }
    if (neighborsInMyWay) return null

    // confirm that the only existing people in my target room want to stay there
    val nonResidentsPresent = rooms[resident]!!.any { it != null && it != resident }
    if (nonResidentsPresent) return null

    val targetRoomIndex = ((rooms[resident]!!.size - 1) downTo 0).first { rooms[resident]!![it] == null }
    val cost = (pathToTargetRoom.count() + targetRoomIndex + 1) * resident.movementCost
    return EnterRoomFromHall(hallIndex, resident, targetRoomIndex, cost)
  }

  private fun isComplete() = rooms.all { room ->
    room.value.all { it == room.key }
  }

  override fun toString(): String {
    val hallwayShortForm = hallway.map { it?.representation ?: '.' }.joinToString("")
    val roomData = (0 until rooms[Resident.AMBER]!!.size).map { roomIndex ->
      rooms.values.map { room -> room[roomIndex]?.representation ?: '.' }.joinToString(" ")
    }.fold("") { acc, next -> "$acc\n  $next"}
    return "$hallwayShortForm$roomData"
  }
}
