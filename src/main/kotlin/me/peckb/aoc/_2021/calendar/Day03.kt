package me.peckb.aoc._2021.calendar

import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import me.peckb.aoc._2021.generators.BitSet
import me.peckb.aoc._2021.generators.InputGenerator
import javax.inject.Inject

class Day03 @Inject constructor(private val inputGenerator: InputGenerator<BitSet>) {
  data class BitCount(private var unsetBits: Int = 0, private var setBits: Int = 0) {
    val unsetCount get() = unsetBits
    val setCount get() = setBits

    fun incrementUnsetCount() = unsetBits++
    fun incrementSetCount() = setBits++
  }

  fun powerConsumption(filename: String) = inputGenerator.usingInput(filename) { inputSequence ->
    val inputList = inputSequence.toList()

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

    val gammaString = CharArray(bitCounter.size) {
      if (bitCounter[it].setCount >= bitCounter[it].unsetCount) { '1' } else { '0' }
    }
    val epsilonString = CharArray(bitCounter.size) {
      if (bitCounter[it].setCount >= bitCounter[it].unsetCount) { '0' } else { '1' }
    }

    val gamma = Integer.parseInt(String(gammaString), 2)
    val epsilon = Integer.parseInt(String(epsilonString), 2)

    gamma * epsilon
  }

  fun lifeSupportRating(filename: String) = inputGenerator.usingInput(filename) { inputSequence ->
    val (setBits, unsetBits) = inputSequence.partition { bitSet -> bitSet.first().isSet }
    val bitSetSize = setBits.first().size

    val (oxygenBitSet, c02BitSet) = runBlocking {
      val deferredOxygen = async(context = Default) {
        val mySet = if (setBits.size > unsetBits.size) { setBits } else { unsetBits }
        findSet(mySet, bitSetSize) { setBitSet, unsetBitSet ->
          if (setBitSet.size >= unsetBitSet.size) { setBitSet } else { unsetBitSet }
        }
      }

      val deferredCo2 = async(context = Default) {
        val mySet = if (setBits.size > unsetBits.size) { unsetBits } else { setBits }
        findSet(mySet, bitSetSize) { setBitSet, unsetBitSet ->
          if (setBitSet.size >= unsetBitSet.size) { unsetBitSet } else { setBitSet }
        }
      }
      awaitAll(deferredOxygen, deferredCo2)
    }

    val oxyGenString = CharArray(bitSetSize) { oxygenBitSet.get(it).char }
    val c02String = CharArray(bitSetSize) { c02BitSet.get(it).char }

    val oxygen = Integer.parseInt(String(oxyGenString), 2)
    val c02 = Integer.parseInt(String(c02String), 2)

    oxygen * c02
  }

  private fun findSet(initialBitSet: List<BitSet>, bitSetSize: Int, selector: (List<BitSet>, List<BitSet>) -> List<BitSet>): BitSet {
    var bitSet = initialBitSet

    for(index in 1 until bitSetSize) {
      if (bitSet.size == 1) break
      val (set, unset) = bitSet.partition { it.get(index).isSet }
      bitSet = selector(set, unset)
    }

    return bitSet.first()
  }
}
