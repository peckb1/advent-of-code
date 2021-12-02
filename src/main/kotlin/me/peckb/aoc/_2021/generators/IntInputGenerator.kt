package me.peckb.aoc._2021.generators

import java.io.File
import javax.inject.Inject

class IntInputGenerator @Inject constructor() : InputGenerator<Int>() {
  override fun <Out> usingInput(filename: String, sequenceHandler: (Sequence<Int>) -> Out): Out =
    sequenceHandler(File(filename).toLineSequence { it.toInt() })
}
