package me.peckb.aoc._2020.calendar.day23

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.StringBuilder

class Day23 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { cupLabeling ->
    val cups = mutableMapOf<Int, Cup>()
    var currentCup: Cup? = null
    var lastCup: Cup? = null

    cupLabeling.forEach { cupCodeChar ->
      val id = Character.getNumericValue(cupCodeChar)
      Cup(id).also { newCup ->
        cups[id] = newCup
        if (currentCup == null) { currentCup = newCup }
        lastCup?.also {
          it.clockwiseCup = newCup
        }
        lastCup = newCup
      }
    }

    lastCup?.also { it.clockwiseCup = currentCup!! }

    cups.runGame(100, currentCup!!)

    cupOrder(cups).drop(1).dropLast(1)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { cupLabeling ->
    val cups = mutableMapOf<Int, Cup>()
    var currentCup: Cup? = null
    var lastCup: Cup? = null

    fun setupNewCup (id: Int) {
      Cup(id).also { newCup ->
        cups[id] = newCup
        if (currentCup == null) { currentCup = newCup }
        lastCup?.also {
          it.clockwiseCup = newCup
        }
        lastCup = newCup
      }
    }

    cupLabeling.forEach { cupCodeChar -> setupNewCup(Character.getNumericValue(cupCodeChar)) }
    lastCup?.also { it.clockwiseCup = currentCup!! }

    val previousMaxCup = cups.keys.maxOrNull()!!

    repeat(1_000_000 - previousMaxCup) { setupNewCup(previousMaxCup + it + 1) }
    lastCup?.also { it.clockwiseCup = currentCup!! }

    cups.runGame(10_000_000, currentCup!!)

    cups[1]?.clockwiseCup?.id!!.toLong() * cups[1]?.clockwiseCup?.clockwiseCup?.id!!.toLong()
  }

  private fun Map<Int, Cup>.runGame(rounds: Int, startCup: Cup) {

    val allCupIds = keys
    val maxCupId = allCupIds.maxOrNull()!!
    val minCupId = allCupIds.minOrNull()!!

    var currentCup = startCup

    repeat(rounds) {
      // pick up the next three cups
      val firstCupToPickup = currentCup.clockwiseCup
      val secondCupToPickup = firstCupToPickup.clockwiseCup
      val thirdCupToPickup   = secondCupToPickup.clockwiseCup

      val cupsInHand = setOfNotNull(firstCupToPickup.id, secondCupToPickup.id, thirdCupToPickup.id)
      var destinationCupId = currentCup.id
      do {
        destinationCupId -= 1
        if (destinationCupId < minCupId) destinationCupId = maxCupId
      } while(cupsInHand.contains(destinationCupId))

      val destinationCup = this[destinationCupId]!!

      // from:
      //   q -> C -> [a, b, c] -> r
      //   x -> D -> y
      val r = thirdCupToPickup.clockwiseCup
      val y = destinationCup.clockwiseCup
      // to:
      //   q -> C -> r
      //   x -> D -> [a, b, c] -> y
      currentCup.clockwiseCup = r
      destinationCup.clockwiseCup = firstCupToPickup
      thirdCupToPickup.clockwiseCup = y

      currentCup = currentCup.clockwiseCup
    }
  }

  private fun cupOrder(cups: MutableMap<Int, Cup>): String {
    var cup = cups[1]!!
    val sb = StringBuilder().apply {
      append(cup.id)
    }
    repeat(cups.size) {
      sb.append("${cup.clockwiseCup.id}")
      cup = cup.clockwiseCup
    }
    return sb.toString()
  }

  data class Cup(val id: Int) {
    lateinit var clockwiseCup: Cup

    override fun toString(): String {
      return id.toString()
    }
  }
}
