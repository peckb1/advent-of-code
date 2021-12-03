package me.peckb.aoc._2021.generators

import java.io.File
import javax.inject.Inject

class BitSetGenerator @Inject constructor() : InputGenerator<BitSet>() {
  override fun <Out> usingInput(filename: String, sequenceHandler: (Sequence<BitSet>) -> Out): Out =
    sequenceHandler(File(filename).toLineSequence { BitSet(it.toCharArray().map(::Bit)) })
}


data class BitSet(val bits: List<Bit>) {
  val size = bits.size

  fun first() = bits.first()
  fun get(index: Int) = bits[index]

  inline fun forEachIndexed(action: (index: Int, Bit) -> Unit) = bits.forEachIndexed(action)

  override fun toString() = bits.toString()
}

data class Bit(val char: Char) {
  val isSet = char == '1'

  override fun toString() = char.toString()
}
