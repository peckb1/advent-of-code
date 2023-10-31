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
          newCup.counterClockwiseCup = it
          it.clockwiseCup = newCup
        }
        lastCup = newCup
      }
    }

    lastCup?.also {
      currentCup?.counterClockwiseCup = it
      it.clockwiseCup = currentCup
    }

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
          newCup.counterClockwiseCup = it
          it.clockwiseCup = newCup
        }
        lastCup = newCup
      }
    }

    cupLabeling.forEach { cupCodeChar ->
      setupNewCup(Character.getNumericValue(cupCodeChar))
    }

    lastCup?.also {
      currentCup?.counterClockwiseCup = it
      it.clockwiseCup = currentCup
    }

    val previousMaxCup = cups.keys.maxOrNull()!!
    repeat(1_000_000 - previousMaxCup) { iteration ->
      setupNewCup(previousMaxCup + iteration + 1)
    }

    lastCup?.also {
      currentCup?.counterClockwiseCup = it
      it.clockwiseCup = currentCup
    }

    cups.runGame(10_000_000, currentCup!!)

    cups[1]?.clockwiseCup?.id!!.toLong() * cups[1]?.clockwiseCup?.clockwiseCup?.id!!.toLong()
  }

  private fun Map<Int, Cup>.runGame(rounds: Int, startCup: Cup) {

    val allCupIds = keys
    val maxCupId = allCupIds.maxOrNull()!!
    val minCupId = allCupIds.minOrNull()!!

    var currentCup = startCup

    repeat(rounds) { round ->
      // pick up the next three cups
      val firstCupToPickup = currentCup.clockwiseCup!!
      val secondCupToPickup = firstCupToPickup.clockwiseCup!!
      val thirdCupToPickup   = secondCupToPickup.clockwiseCup!!

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
      val C = currentCup
      val a = firstCupToPickup
      val c = thirdCupToPickup
      val r = thirdCupToPickup.clockwiseCup!!

      val D = destinationCup
      val y = destinationCup.clockwiseCup!!
      // to:
      //   q -> C -> r
      //   x -> D -> [a, b, c] -> y
      C.clockwiseCup = r; r.counterClockwiseCup = C
      D.clockwiseCup = a; a.counterClockwiseCup = D
      c.clockwiseCup = y; y.counterClockwiseCup = c

      currentCup = currentCup.clockwiseCup!!
    }
  }

  private fun cupOrder(cups: MutableMap<Int, Cup>): String {
    var cup = cups[1]!!
    val sb = StringBuilder().apply {
      append(cup.id)
    }
    repeat(cups.size) {
      sb.append("${cup.clockwiseCup?.id}")
      cup = cup.clockwiseCup!!
    }
    return sb.toString()
  }

  data class Cup(val id: Int) {
    var clockwiseCup: Cup? = null
    var counterClockwiseCup: Cup? = null

    override fun toString(): String {
      return id.toString()
    }
  }
}
