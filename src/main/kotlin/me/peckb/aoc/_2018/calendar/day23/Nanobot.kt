package me.peckb.aoc._2018.calendar.day23

data class Nanobot(val point: Point, val radius: Long) {
  fun intersects(them: Nanobot): Boolean {
    return point.distanceTo(them.point) <= radius + them.radius
  }
}