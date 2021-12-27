package me.peckb.aoc._2021.calendar.day23

sealed class Movement(val cost: Int) {
  class EnterRoomFromHall(val hallIndex: Int, val roomOwner: Resident, val roomIndex: Int, cost: Int) : Movement(cost) {
    override fun toString(): String {
      return "H[$hallIndex] -> R[${roomOwner.doorwayIndex},$roomIndex]: $cost"
    }
  }
  class EnterHallFromRoom(val roomOwner: Resident, val roomIndex: Int, val hallIndex: Int, cost: Int): Movement(cost) {
    override fun toString(): String {
      return "R[${roomOwner.doorwayIndex},$roomIndex] -> H[$hallIndex]: $cost"
    }
  }
  class EnterRoomFromRoom(val sourceRoomOwner: Resident, val sourceRoomIndex: Int, val destinationRoomOwner: Resident, val destinationRoomIndex: Int, cost: Int) : Movement(cost) {
    override fun toString(): String {
      return "R[${sourceRoomOwner.doorwayIndex},$sourceRoomIndex] -> R[${destinationRoomOwner.doorwayIndex},$destinationRoomIndex]: $cost"
    }
  }
}
