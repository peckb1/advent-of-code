package me.peckb.aoc._2021.generators

import java.io.File
import javax.inject.Inject

class PathGenerator @Inject constructor() : InputGenerator<Path>() {
  override fun <Out> usingInput(filename: String, sequenceHandler: (Sequence<Path>) -> Out): Out =
    sequenceHandler(File(filename).toLineSequence(Path::fromLine))
}

data class Path(val direction: Direction, val distance: Int) {
  companion object {
    fun fromLine(dataLine: String): Path {
      val dataParts = dataLine.split(" ")
      return Path(Direction.valueOf(dataParts.first().uppercase()), dataParts.last().toInt())
    }
  }
}

enum class Direction {
  FORWARD, DOWN, UP
}
