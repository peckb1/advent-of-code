package me.peckb.aoc._2021.calendar

import me.peckb.aoc._2021.generators.Bits
import me.peckb.aoc._2021.generators.InputGenerator
import javax.inject.Inject

class Day03 @Inject constructor(private val inputGenerator: InputGenerator<Bits>) {

  /**
   *
   */
  fun partOne(filename: String) = inputGenerator.usingInput(filename) { input ->
    val data = input.fold(mutableMapOf<Int, Pair<Int, Int>>()) { map, nextBits ->
      map.also {
        nextBits.bits.forEachIndexed { bitIndex, bitValue->
          var pair = it[bitIndex] ?: Pair(0,0)
          pair = if (bitValue) {
            pair.copy(second = pair.second + 1)
          } else {
            pair.copy(first = pair.first + 1)
          }
          it[bitIndex] = pair
        }
      }
    }

    val gammaString = CharArray(data.size)
    val epsilonString = CharArray(data.size)
    data.forEach { (index, pair) ->
      gammaString[index] = if (pair.first > pair.second) { '0' } else { '1' }
      epsilonString[index] = if (pair.first > pair.second) { '1' } else { '0' }
    }

    val gamma = Integer.parseInt(String(gammaString), 2)
    val epsilon = Integer.parseInt(String(epsilonString), 2)

    gamma * epsilon
  }

  /**
   *
   */
  fun partTwo(filename: String) = inputGenerator.usingInput(filename) { input ->
    // gonna need to iterate over this multiple times me thinks
    val inputList = input.toList()
    var oxygenList = inputList
    var co2List = inputList

    for(index in 0 until inputList[0].bits.size) {
      var groupings = mutableMapOf<Boolean, MutableList<Bits>>(
        true to mutableListOf(),
        false to mutableListOf()
      )
      var counts = mutableMapOf<Boolean, Int>(
        true to 0,
        false to 0
      )

      if (oxygenList.size != 1) {
        oxygenList.forEach { bits ->
          val bit = bits.bits[index]
          groupings[bit]!!.add(bits)
          counts[bit] = counts[bit]!! + 1
        }

        if (counts[true]!! >= counts[false]!!) {
          oxygenList = groupings[true]!!
        } else {
          oxygenList = groupings[false]!!
        }
      }

      groupings = mutableMapOf<Boolean, MutableList<Bits>>(
        true to mutableListOf(),
        false to mutableListOf()
      )
      counts = mutableMapOf<Boolean, Int>(
        true to 0,
        false to 0
      )

      if (co2List.size != 1) {
        co2List.forEach { bits ->
          val bit = bits.bits[index]
          groupings[bit]!!.add(bits)
          counts[bit] = counts[bit]!! + 1
        }

        if (counts[false]!! <= counts[true]!!) {
          co2List = groupings[false]!!
        } else {
          co2List = groupings[true]!!
        }
      }

      4
    }

    val oxygenString = CharArray(inputList[0].bits.size)
    val co2String = CharArray(inputList[0].bits.size)

    oxygenList.first().bits.forEachIndexed { index, bit ->
      oxygenString[index] = if (bit) { '1' } else { '0' }
    }
    co2List.first().bits.forEachIndexed { index, bit ->
      co2String[index] = if (bit) { '1' } else { '0' }
    }

    val oxygenRating = Integer.parseInt(String(oxygenString), 2)
    val c02Rating = Integer.parseInt(String(co2String), 2)

    oxygenRating * c02Rating
  }
}
