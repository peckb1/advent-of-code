package me.peckb.aoc

import java.io.File
import javax.inject.Inject

class IntInputGenerator @Inject constructor() : InputGenerator<Int> {
  override fun <Out> usingInput(filename: String, sequenceHandler: (Sequence<Int>) -> Out): Out =
    sequenceHandler(File(filename).toIntSequence())

  private fun File.toIntSequence() = this.bufferedReader().lineSequence().map { it.toInt() }
}
