package me.peckb.aoc._2021.generators

import java.io.File

class InputGenerator private constructor(private val lines: Sequence<String>) {

  fun <Out> read(sequenceHandler: (Sequence<String>) -> Out) =
    sequenceHandler(lines)

  fun <In, Out> readAs(lineConverter: (String) -> In, sequenceHandler: (Sequence<In>) -> Out) =
    sequenceHandler(lines.map(lineConverter))

  class InputGeneratorFactory {
    fun forFile(fileName: String) = InputGenerator(File(fileName).bufferedReader().lineSequence())
  }
}
