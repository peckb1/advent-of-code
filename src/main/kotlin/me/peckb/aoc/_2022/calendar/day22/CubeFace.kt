package me.peckb.aoc._2022.calendar.day22

import me.peckb.aoc._2022.calendar.day22.Direction.*

data class CubeFace(val minX: Int, val maxX: Int, val minY: Int, val maxY: Int) {

  data class FaceTransition(val newX: Int, val newY: Int, val newDirection: Direction, var newFace: CubeFace)

  companion object {
    val FACE_ONE = CubeFace(50, 99, 0, 49)
    val FACE_TWO = CubeFace(100, 149, 0, 49)
    val FACE_THREE = CubeFace(50, 99, 50, 99)
    val FACE_FOUR = CubeFace(0, 49, 100, 149)
    val FACE_FIVE = CubeFace(50, 99, 100, 149)
    val FACE_SIX = CubeFace(0, 49, 150, 199)

    fun leftToRight(y: Int, source: CubeFace, destination: CubeFace): FaceTransition {
      val newX = destination.minX
      val newY = destination.maxY - (y - source.minY)
      val newDirection = RIGHT

      return FaceTransition(newX, newY, newDirection, destination)
    }

    fun leftToDown(y: Int, source: CubeFace, destination: CubeFace): FaceTransition {
      val newX = destination.minX + (y - source.minY)
      val newY = destination.minY
      val newDirection = DOWN

      return FaceTransition(newX, newY, newDirection, destination)
    }

    fun rightToLeft(y: Int, source: CubeFace, destination: CubeFace): FaceTransition {
      val newX = destination.maxX
      val newY = destination.maxY - (y - source.minY)
      val newDirection = LEFT

      return FaceTransition(newX, newY, newDirection, destination)
    }

    fun rightToUp(y: Int, source: CubeFace, destination: CubeFace): FaceTransition {
      val newX = destination.minX + (y - source.minY)
      val newY = destination.maxY
      val newDirection = UP

      return FaceTransition(newX, newY, newDirection, destination)
    }

    fun upToRight(x: Int, source: CubeFace, destination: CubeFace): FaceTransition {
      val newX = destination.minX
      val newY = destination.minY + (x - source.minX)
      val newDirection = RIGHT

      return FaceTransition(newX, newY, newDirection, destination)
    }

    fun upToUp(x: Int, source: CubeFace, destination: CubeFace): FaceTransition {
      val newX = destination.minX + (x - source.minX)
      val newY = destination.maxY
      val newDirection = UP

      return FaceTransition(newX, newY, newDirection, destination)
    }

    fun downToLeft(x: Int, source: CubeFace, destination: CubeFace): FaceTransition {
      val newX = destination.maxX
      val newY = destination.minY + (x - source.minX)
      val newDirection = LEFT

      return FaceTransition(newX, newY, newDirection, destination)
    }

    fun downToDown(x: Int, source: CubeFace, destination: CubeFace): FaceTransition {
      val newX = destination.minX + (x - source.minX)
      val newY = destination.minY
      val newDirection = DOWN

      return FaceTransition(newX, newY, newDirection, destination)
    }
  }
}
