package me.peckb.aoc._2021.calendar.day23

enum class Resident(val representation: Char, val doorwayIndex: Int, val movementCost: Int) {
  AMBER('A', 2, 1),
  BRONZE('B', 4, 10),
  COPPER('C', 6, 100),
  DESERT('D', 8, 1000);

  fun pathToRoom(targetRoom: Resident): IntProgression {
    return when (this) {
      AMBER -> when (targetRoom) {
        AMBER, BRONZE, COPPER, DESERT -> doorwayIndex..targetRoom.doorwayIndex
      }
      BRONZE -> when (targetRoom) {
        AMBER, BRONZE -> targetRoom.doorwayIndex..doorwayIndex
        COPPER, DESERT -> doorwayIndex..targetRoom.doorwayIndex
      }
      COPPER -> when (targetRoom) {
        AMBER, BRONZE -> targetRoom.doorwayIndex..doorwayIndex
        COPPER, DESERT -> doorwayIndex..targetRoom.doorwayIndex
      }
      DESERT -> when (targetRoom) {
        AMBER, BRONZE, COPPER, DESERT -> targetRoom.doorwayIndex..doorwayIndex
      }
    }
  }

  companion object {
    fun fromResident(resident: Char) = values().first { it.representation == resident }
  }
}
