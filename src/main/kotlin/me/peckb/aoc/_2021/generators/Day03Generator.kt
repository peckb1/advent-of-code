package me.peckb.aoc._2021.generators

import java.io.File
import javax.inject.Inject

class Day03Generator @Inject constructor() : InputGenerator<T>() {
  override fun <Out> usingInput(filename: String, sequenceHandler: (Sequence<T>) -> Out): Out =
    sequenceHandler(File(filename).toLineSequence { T.fromLine(it) })
}

data class T() {
  companion object {
    fun fromLine(dataLine: String): T {

    }
  }

}
