package me.peckb.aoc._2018.calendar.day23

import kotlin.math.abs

data class Point(val x: Long, val y: Long, val z: Long) {
  fun distanceTo(them: Point): Long = abs(x - them.x) + abs(y - them.y) + abs(z - them.z)

  fun neighbors(radius: Long): Iterable<Point> {
    val me = this
    return buildList {
      (-1L..1L).forEach { x ->
        (-1L..1L).forEach { y ->
          (-1L..1L).forEach { z ->
            add(Point(me.x + x * radius, me.y + y * radius, me.z + z * radius))
          }
        }
      }
    }
  }
}