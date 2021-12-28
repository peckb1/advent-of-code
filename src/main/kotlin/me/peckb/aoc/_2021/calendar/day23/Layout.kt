package me.peckb.aoc._2021.calendar.day23

import kotlin.math.abs

data class Layout(val hallway: List<Char>, private val rooms: List<List<Char>>) {
  companion object {
    private val VALID_HALLWAY_INDICES = listOf(0, 1, 3, 5, 7, 9, 10)
    private val MOVEMENT_COSTS = mapOf('A' to 1, 'B' to 10, 'C' to 100, 'D' to 1000)

    fun fromInput(input: List<String>) : Layout {
      val hallway = input[1].replace("#", "").toList()
      val rooms = input.subList(2, input.size - 1).map {
        it.replace("#", " ").drop(1).dropLast(1).toList()
      }

      return Layout(hallway, rooms)
    }
  }

  private val destinations = mapOf(
    'A' to Room('A', 2, rooms.map { it[2] }),
    'B' to Room('B', 4, rooms.map { it[4] }),
    'C' to Room('C', 6, rooms.map { it[6] }),
    'D' to Room('D', 8, rooms.map { it[8] })
  )

  fun neighbors(): List<Pair<Layout, Int>> {
    val neighbors = mutableListOf<Pair<Layout, Int>>()

    // people inside their rooms who need to go to the hallway
    destinations.values.forEach insideRoomCheck@ { room ->
      val roomNeedsToVacate = room.roomSpaces.any { occupant -> occupant != '.' && occupant != room.roomOwner}
      if (!roomNeedsToVacate) return@insideRoomCheck

      val personToLeave = room.roomSpaces.withIndex().first { occupant -> occupant.value != '.' }
      personToLeave.also { (occupantIndex, roomOccupant) ->
        VALID_HALLWAY_INDICES.forEach { hallwayIndex ->
          if (hallway.pathClear(room.roomIndex, hallwayIndex)) {
            val cost = (abs(room.roomIndex - hallwayIndex) + occupantIndex + 1) * MOVEMENT_COSTS.getValue(roomOccupant)

            val newHallway = hallway.joinToString("").toMutableList()
              .apply { this[hallwayIndex] = roomOccupant }
            val newRooms = rooms.map { it.joinToString("").toMutableList() }
              .apply { this[occupantIndex][room.roomIndex] = '.' }
            val newLayout = Layout(newHallway, newRooms)

            neighbors.add(newLayout to cost)
          }
        }
      }
    }

    // people inside the hallway that want to go back into their rooms
    VALID_HALLWAY_INDICES.forEach hallwayCheck@ { hallwayIndex ->
      val resident = hallway[hallwayIndex]
      // make sure hallway space has a resident
      if (hallway[hallwayIndex] == '.') return@hallwayCheck
      // make sure our target room has an empty space for us
      val room = destinations[resident]!!
      if (!room.roomSpaces.all { it == '.' || it == resident }) return@hallwayCheck

      if (!hallway.pathClear(hallwayIndex, room.roomIndex)) return@hallwayCheck

      val occupantIndex = destinations[resident]!!.roomSpaces.withIndex().last { it.value == '.' }.index
      val cost = (abs(room.roomIndex - hallwayIndex) + occupantIndex + 1) * MOVEMENT_COSTS.getValue(resident)

      val newHallway = hallway.joinToString("").toMutableList()
        .apply { this[hallwayIndex] = '.' }
      val newRooms = rooms.map { it.joinToString("").toMutableList() }
        .apply { this[occupantIndex][room.roomIndex] = resident }
      val newLayout = Layout(newHallway, newRooms)

      neighbors.add(newLayout to cost)
    }

    return neighbors
  }

  @Suppress("ReplaceRangeToWithUntil")
  private fun List<Char>.pathClear(sourceIndex: Int, destinationIndex: Int): Boolean {
    val range = if (sourceIndex < destinationIndex) {
      (sourceIndex+1)..destinationIndex
    } else {
      destinationIndex..(sourceIndex - 1)
    }
    return this.slice(range).all { it == '.' }
  }
}

class Room(val roomOwner: Char, val roomIndex: Int, val roomSpaces: List<Char>)

class LayoutWithCost(private val layout: Layout, private val cost: Int) : DijkstraNodeWithCost<Layout, Int> {
  override fun cost() = cost
  override fun node() = layout
  override fun compareTo(other: DijkstraNodeWithCost<Layout, Int>) = cost.compareTo(other.cost())
  override fun neighbors() = layout.neighbors().map { LayoutWithCost(it.first, it.second) }
}
