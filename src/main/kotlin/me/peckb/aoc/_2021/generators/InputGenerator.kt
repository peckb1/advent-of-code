package me.peckb.aoc._2021.generators

import java.io.File

class InputGenerator private constructor(private val lines: Sequence<String>) {

  fun <Out> readOne(singleLineHandler: (String) -> Out) = singleLineHandler(lines.first())

  fun <Out> read(sequenceHandler: (Sequence<String>) -> Out) = sequenceHandler(lines)

  fun <In, Out> readAs(converter: (String) -> In, handler: (Sequence<In>) -> Out) = handler(lines.map(converter))

  class InputGeneratorFactory {
    fun forFile(fileName: String) = InputGenerator(File(fileName).bufferedReader().lineSequence())
  }
}
