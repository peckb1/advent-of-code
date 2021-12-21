package me.peckb.aoc.generators

import java.io.BufferedReader
import java.io.File

class InputGenerator private constructor(private val lines: Sequence<String>) {

  fun <Out> readOne(singleLineHandler: (String) -> Out) = singleLineHandler(lines.first())

  fun <Out> read(sequenceHandler: (Sequence<String>) -> Out) = sequenceHandler(lines)

  fun <In, Out> readAs(converter: (String) -> In, handler: (Sequence<In>) -> Out) = handler(lines.map(converter))

  class InputGeneratorFactory {
    fun forFile(fileName: String): InputGenerator {
      val file = File(fileName)
      val bufferedReader = BufferedReader(file.reader())

      return InputGenerator(bufferedReader.lineSequence())
    }
  }
}
