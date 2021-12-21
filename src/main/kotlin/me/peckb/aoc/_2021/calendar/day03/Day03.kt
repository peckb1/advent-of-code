package me.peckb.aoc._2021.calendar.day03

import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import me.peckb.aoc._2021.calendar.day03.Day03.Bit
import me.peckb.aoc.generators.InputGenerator
import javax.inject.Inject

private typealias BitSet = List<Bit>

class Day03 @Inject constructor(private val generatorFactory: InputGenerator.InputGeneratorFactory) {
  fun powerConsumption(filename: String) = generatorFactory.forFile(filename).readAs(::bitSet) { input ->
    val inputList = input.toList()

    val bitCounter = Array(inputList.first().size) { BitCount() }

    inputList.forEach { bitSet ->
      bitSet.forEachIndexed { index, bit ->
        if (bit.isSet) {
          bitCounter[index].incrementSetCount()
        } else {
          bitCounter[index].incrementUnsetCount()
        }
      }
    }

    val gammaString = CharArray(bitCounter.size) { if (bitCounter[it].setCount >= bitCounter[it].unsetCount) { '1' } else { '0' } }
    val epsilonString = CharArray(bitCounter.size) { if (bitCounter[it].setCount >= bitCounter[it].unsetCount) { '0' } else { '1' } }

    val gamma = Integer.parseInt(String(gammaString), 2)
    val epsilon = Integer.parseInt(String(epsilonString), 2)

    gamma * epsilon
  }

  fun lifeSupportRating(filename: String) = generatorFactory.forFile(filename).readAs(::bitSet) { input ->
    runBlocking {
      val (setBits, unsetBits) = input.partition { bitSet -> bitSet.first().isSet }
      val bitSetSize = setBits.first().size

      val deferredOxygen = async(Default) {
        val decider = { setBitSet: List<BitSet>, unsetBitSet: List<BitSet> ->
          if (setBitSet.size >= unsetBitSet.size) { setBitSet } else { unsetBitSet }
        }
        findSet(decider(setBits, unsetBits), bitSetSize, decider).asInt(bitSetSize)
      }

      val deferredCo2 = async(Default) {
        val decider = { setBitSet: List<BitSet>, unsetBitSet: List<BitSet> ->
          if (setBitSet.size >= unsetBitSet.size) { unsetBitSet } else { setBitSet }
        }
        findSet(decider(setBits, unsetBits), bitSetSize, decider).asInt(bitSetSize)
      }

      val (oxygen, c02) = awaitAll(deferredOxygen, deferredCo2)

      oxygen * c02
    }
  }

  private fun bitSet(line: String) = line.toCharArray().map(::Bit)

  private data class BitCount(private var unsetBits: Int = 0, private var setBits: Int = 0) {
    val unsetCount get() = unsetBits
    val setCount get() = setBits

    fun incrementUnsetCount() = unsetBits++
    fun incrementSetCount() = setBits++
  }

  private fun findSet(initialBitSet: List<BitSet>, bitSetSize: Int, selector: (List<BitSet>, List<BitSet>) -> List<BitSet>): BitSet {
    var bitSet = initialBitSet

    for(index in 1 until bitSetSize) {
      if (bitSet.size == 1) break
      val (set, unset) = bitSet.partition { it[index].isSet }
      bitSet = selector(set, unset)
    }

    return bitSet.first()
  }

  private fun BitSet.asInt(bitSetSize: Int): Int {
    val oxyGenString = CharArray(bitSetSize) { this[it].char }
    return Integer.parseInt(String(oxyGenString), 2)
  }

  internal data class Bit(val char: Char) {
    val isSet = char == '1'
  }
}
