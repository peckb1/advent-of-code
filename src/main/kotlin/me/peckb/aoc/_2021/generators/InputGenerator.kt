package me.peckb.aoc._2021.generators

import java.io.File

class InputGenerator private constructor(private val file: File) {
  fun <Out> readOne(singleLineHandler: (String) -> Out) =
    file.useLines { singleLineHandler(it.first()) }

  fun <Out> read(sequenceHandler: (Sequence<String>) -> Out) =
    file.useLines { sequenceHandler(it) }

  fun <In, Out> readAs(converter: (String) -> In, handler: (Sequence<In>) -> Out) =
    file.useLines { handler(it.map(converter)) }

  class InputGeneratorFactory {
    fun forFile(fileName: String) = InputGenerator(File(fileName))
  }
}
