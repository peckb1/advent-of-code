package me.peckb.aoc

interface InputGenerator<In> {
  fun <Out> usingInput(filename: String, sequenceHandler: (Sequence<In>) -> Out): Out
}
