package me.peckb.aoc._2015.calendar.day14

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day14 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::reindeer) { input ->
    runBlocking {
      val distances: List<Pair<Reindeer, Long>> = input.map { reindeer ->
        async(Dispatchers.Default) {
          reindeer to reindeer.at(RACE_TIME_SECONDS)
        }
      }.toList().awaitAll()

      distances.maxByOrNull { it.second }?.second
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::reindeer) { input ->
    runBlocking {
      val timesAtEverySecond = input.map { reindeer ->
        async(Dispatchers.Default) {
          (1..RACE_TIME_SECONDS).map { timeSeconds ->
            reindeer to reindeer.at(timeSeconds)
          }
        }
      }.toList().awaitAll()

      val points = mutableMapOf<String, Int>()
      repeat(RACE_TIME_SECONDS) { timeSeconds ->
        val furthestReindeerAtTime = timesAtEverySecond.maxByOrNull { reindeerTimes ->
          reindeerTimes[timeSeconds].second
        }?.first()
        furthestReindeerAtTime?.also {
          points.merge(it.first.name, 1) { totalPoints, one -> totalPoints + one}
        }
      }

      points.maxByOrNull { it.value }?.value
    }
  }

  private fun reindeer(line: String): Reindeer {
    // Comet can fly 14 km/s for 10 seconds, but then must rest for 127 seconds.
    val (nonRestInfo, restInfo) = line.split(" seconds, but then must rest for ")
    val restTimeSeconds = restInfo.substringBefore(" seconds.")
    val (nameAndSpeed, flyTimeSeconds) = nonRestInfo.split(" km/s for ")
    val (name, speedKmS) = nameAndSpeed.split(" ").let {
      it.first() to it.last()
    }

    return Reindeer(name, speedKmS.toInt(), flyTimeSeconds.toInt(), restTimeSeconds.toInt())
  }

  private data class Reindeer(val name: String, private val speedKmS: Int, private val flyTimeSeconds: Int, private val restTimeSeconds: Int) {
    fun at(raceTimeSeconds: Int): Long {
      var timeLeft = raceTimeSeconds
      var rested = true
      var distance = 0L
      while (timeLeft > 0) {
        if (rested) {
          val timeSpendFlying = if (timeLeft >= flyTimeSeconds) flyTimeSeconds else timeLeft
          timeLeft -= timeSpendFlying
          distance += timeSpendFlying * speedKmS
          rested = false
        } else {
          val timeSpendResting = if (timeLeft >= restTimeSeconds) restTimeSeconds else timeLeft
          timeLeft -= timeSpendResting
          rested = true
        }
      }
      return distance
    }
  }

  companion object {
    const val RACE_TIME_SECONDS = 2503
  }
}
