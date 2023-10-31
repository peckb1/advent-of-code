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

    val allCupIds = cups.keys.toSet()
    val maxCupId = allCupIds.maxOrNull()!!
    val minCupId = allCupIds.minOrNull()!!

    repeat(100) {

      // pick up the next three cups
      val firstCupToPickup = currentCup?.clockwiseCup!!
      val secondCupToPickup = firstCupToPickup.clockwiseCup!!
      val thirdCupToPickup   = secondCupToPickup.clockwiseCup!!

      val cupsInHand = setOfNotNull(firstCupToPickup.id, secondCupToPickup.id, thirdCupToPickup.id)
      val possibleDestinationCups = allCupIds.minus(cupsInHand)
      var destinationCupId = currentCup!!.id
      do {
        destinationCupId -= 1
        if (destinationCupId < minCupId) destinationCupId = maxCupId
      } while(!possibleDestinationCups.contains(destinationCupId))

      val destinationCup = cups[destinationCupId]!!

//      println("\tCurrent: $currentCup Destination $destinationCup")

      // from:
      //   q -> C -> a, b, c -> r
      //   x -> D -> y
      val q = currentCup?.counterClockwiseCup!!
      val C = currentCup!!
      val a = firstCupToPickup
      val b = secondCupToPickup
      val c = thirdCupToPickup
      val r = thirdCupToPickup.clockwiseCup!!

      val x = destinationCup.counterClockwiseCup!!
      val D = destinationCup
      val y = destinationCup.clockwiseCup!!
      // to:
      //   q -> C -> r
      //   x -> D -> a, b, c -> y
      // TODO: why does setting q or x pairs again cause a problem??
//      q.clockwiseCup = C; C.counterClockwiseCup = q
      C.clockwiseCup = r; r.counterClockwiseCup = C

//      x.clockwiseCup = D; D.counterClockwiseCup = x
      D.clockwiseCup = a; a.counterClockwiseCup = D
      c.clockwiseCup = y; y.counterClockwiseCup = c


      currentCup = currentCup?.clockwiseCup
    }

    cupOrder(cups).drop(1).dropLast(1)
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

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { cupLabeling ->
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

    val previousMaxCup = cups.keys.maxOrNull()!!
    repeat(1_000_000 - previousMaxCup) { iteration ->
      val id = previousMaxCup + iteration + 1
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

    val allCupIds = cups.keys.toSet()
    val maxCupId = allCupIds.maxOrNull()!!
    val minCupId = allCupIds.minOrNull()!!

    println("I have ${allCupIds.size} cups starting at $minCupId going to $maxCupId")

    repeat(10_000_000) {
      if (it % 100_000 == 0) println("Round: $it")
      // pick up the next three cups
      val firstCupToPickup = currentCup?.clockwiseCup!!
      val secondCupToPickup = firstCupToPickup.clockwiseCup!!
      val thirdCupToPickup   = secondCupToPickup.clockwiseCup!!

      val cupsInHand = setOfNotNull(firstCupToPickup.id, secondCupToPickup.id, thirdCupToPickup.id)
//      val possibleDestinationCups = allCupIds.minus(cupsInHand)
      var destinationCupId = currentCup!!.id
      do {
        destinationCupId -= 1
        if (destinationCupId < minCupId) destinationCupId = maxCupId
      } while(cupsInHand.contains(destinationCupId))

      val destinationCup = cups[destinationCupId]!!

//      println("\tCurrent: $currentCup Destination $destinationCup")

      // from:
      //   q -> C -> a, b, c -> r
      //   x -> D -> y
      val q = currentCup?.counterClockwiseCup!!
      val C = currentCup!!
      val a = firstCupToPickup
      val b = secondCupToPickup
      val c = thirdCupToPickup
      val r = thirdCupToPickup.clockwiseCup!!

      val x = destinationCup.counterClockwiseCup!!
      val D = destinationCup
      val y = destinationCup.clockwiseCup!!
      // to:
      //   q -> C -> r
      //   x -> D -> a, b, c -> y
      // TODO: why does setting q or x pairs again cause a problem??
//      q.clockwiseCup = C; C.counterClockwiseCup = q
      C.clockwiseCup = r; r.counterClockwiseCup = C

//      x.clockwiseCup = D; D.counterClockwiseCup = x
      D.clockwiseCup = a; a.counterClockwiseCup = D
      c.clockwiseCup = y; y.counterClockwiseCup = c


      currentCup = currentCup?.clockwiseCup
    }

    cups[1]?.clockwiseCup?.id!!.toLong() * cups[1]?.clockwiseCup?.clockwiseCup?.id!!.toLong()
  }

  data class Cup(val id: Int) {
    var clockwiseCup: Cup? = null
    var counterClockwiseCup: Cup? = null

    override fun toString(): String {
      return id.toString()
    }
  }
}
