package me.peckb.aoc._2021.generators

import java.io.File
import javax.inject.Inject

class Day03Generator @Inject constructor() : InputGenerator<Bits>() {
  override fun <Out> usingInput(filename: String, sequenceHandler: (Sequence<Bits>) -> Out): Out =
    sequenceHandler(File(filename).toLineSequence {
      Bits(it.toCharArray().map { binaryChar -> binaryChar == '1' })
    })
}

data class Bits(val bits: List<Boolean>)