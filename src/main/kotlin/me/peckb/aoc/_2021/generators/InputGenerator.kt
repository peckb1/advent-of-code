package me.peckb.aoc._2021.generators

import java.io.File

abstract class InputGenerator<In> {
  abstract fun <Out> usingInput(filename: String, sequenceHandler: (Sequence<In>) -> Out): Out

  protected fun File.toLineSequence(converter: (String) -> In) =
    this.bufferedReader().lineSequence().map { converter(it) }
}
