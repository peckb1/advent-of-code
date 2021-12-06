package me.peckb.aoc._2021.calendar.day06

import me.peckb.aoc._2021.generators.InputGenerator
import javax.inject.Inject

class Day06 @Inject constructor(private val generatorFactory: InputGenerator.InputGeneratorFactory) {
  companion object {
    const val PART_ONE_OBSERVATION_PERIOD = 80
    const val PART_TWO_OBSERVATION_PERIOD = 256
  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val population = mutableListOf<Fish>()
    input.first().split(",").forEach { age ->
      population.add(Fish(age.toInt()))
    }

    repeat(PART_ONE_OBSERVATION_PERIOD) {
      val newFishSpawned = mutableListOf<Fish>()
      population.forEach { fish ->
        fish.age()?.let { newFish -> newFishSpawned.add(newFish) }
      }
      population.addAll(newFishSpawned)
    }

    population.size
  }


  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val population = mutableListOf<Fish>()
    input.first().split(",").forEach { age ->
      population.add(Fish(age.toInt()))
    }

    val parentsGivingBirth: MutableMap<Int, Pair<Long, Long>> = mutableMapOf(
      0 to (0L to 0L),
      1 to (0L to 0L),
      2 to (0L to 0L),
      3 to (0L to 0L),
      4 to (0L to 0L),
      5 to (0L to 0L),
      6 to (0L to 0L)
    )

    population.map { fish ->
      val myChildDay = fish.myAge()
      parentsGivingBirth[myChildDay] = (parentsGivingBirth[myChildDay]!!.first + 1) to 0L
    }

    var today = -1
    var total: Long = population.size.toLong()
    repeat(PART_TWO_OBSERVATION_PERIOD) { day ->
      (today++)
      today %= 7
      val tomorrow = (today + 9) % 7 // the "counter" going to 8 starting the next day - means we need to jump two days ahead, next week
      val parentsGivingBirthToday = parentsGivingBirth[today]!!
      if (parentsGivingBirthToday.first > 0) {
        // a parent is giving birth today, add a child
        total += parentsGivingBirthToday.first
        // if we have any waiting children for "next week" add them to today
        // and then reset those "waiting" for next week
        parentsGivingBirth[today] = parentsGivingBirthToday.first + parentsGivingBirth[today]!!.second to 0
        // add today's count to tomorrows "waiting"
        parentsGivingBirth[tomorrow] = parentsGivingBirth[tomorrow]!!.first to parentsGivingBirthToday.first
      } else {
        // no one is giving birth today, so just move our waiting to available next week
        parentsGivingBirth[today] = parentsGivingBirth[today]!!.first + parentsGivingBirth[today]!!.second to 0
      }
    }

    total
  }

  data class Fish(private var daysUntilSpawn: Int = 8) {
    fun age() : Fish? {
      daysUntilSpawn--
      return if (daysUntilSpawn < 0) { Fish().also { this.daysUntilSpawn = 6 } } else { null }
    }

    fun myAge() = daysUntilSpawn
  }
}
