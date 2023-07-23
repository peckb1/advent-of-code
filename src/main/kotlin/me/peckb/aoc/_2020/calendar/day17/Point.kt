package me.peckb.aoc._2020.calendar.day17

interface Point {
  val neighbors: List<Point>
}

data class Point3D(val x: Int, val y: Int, val z: Int) : Point {
  override val neighbors: List<Point> by lazy {
    (x - 1..x + 1).flatMap { x0 ->
      (y - 1..y + 1).flatMap { y0 ->
        (z - 1..z + 1).mapNotNull { z0 ->
          Point3D(x0, y0, z0).takeUnless { it == this }
        }
      }
    }
  }
}

data class Point4D(val x: Int, val y: Int, val z: Int, val aa: Int) : Point {
  override val neighbors: List<Point> by lazy {
    (x - 1..x + 1).flatMap { x0 ->
      (y - 1..y + 1).flatMap { y0 ->
        (z - 1..z + 1).flatMap { z0 ->
          (aa - 1..aa + 1).mapNotNull { aa0 ->
            Point4D(x0, y0, z0, aa0).takeUnless { it == this }
          }
        }
      }
    }
  }
}
