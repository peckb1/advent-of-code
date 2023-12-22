package me.peckb.aoc._2023.calendar.day22

data class Position(var x: Int, var y: Int, var z: Int) {
  fun drop(deltaZ: Int) {
    z -= deltaZ
  }
}
