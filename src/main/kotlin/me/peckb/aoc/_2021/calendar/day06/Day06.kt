package me.peckb.aoc._2021.calendar.day06

import me.peckb.aoc._2021.generators.InputGenerator
import java.time.DayOfWeek
import java.time.DayOfWeek.*
import javax.inject.Inject

class Day06 @Inject constructor(private val generatorFactory: InputGenerator.InputGeneratorFactory) {
  companion object {
    const val PART_ONE_OBSERVATION_PERIOD = 80
    const val PART_TWO_OBSERVATION_PERIOD = 256
  }

  /** BRUTE FORCE */
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

  /** COUNT THE BIRTH DAYS! */
  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val birthDays: MutableMap<DayOfWeek, BirthWindow> = mutableMapOf(
      MONDAY to BirthWindow(),
      TUESDAY to BirthWindow(),
      WEDNESDAY to BirthWindow(),
      THURSDAY to BirthWindow(),
      FRIDAY to BirthWindow(),
      SATURDAY to BirthWindow(),
      SUNDAY to BirthWindow()
    )

    val population = input.first().split(",").map { Fish(it.toInt()) }.apply {
      this.map { birthDays[of(it.myAge() + 1)]?.addBirth() }
    }

    var today = SUNDAY
    var total: Long = population.size.toLong()

    repeat(PART_TWO_OBSERVATION_PERIOD) {
      today = today.plus(1)
      val todaysBirths = birthDays[today]!!.todaysBirths

      if (todaysBirths > 0) {
        // a parent is giving birth today, add the children to our count
        total += todaysBirths
        // add today's count to next birth windows "waiting"
        // DEV NOTE: the counter needs to be "8" tomorrow, so we add "9" today
        birthDays[today.plus(9)]?.setNextWeeksBirth(todaysBirths)
      }
      // if we have any waiting children for "next week" add them to today
      // and then reset those "waiting" for next week
      birthDays[today]?.moveNextWeekToToday()
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

  data class BirthWindow(var todaysBirths: Long = 0, private var nextWeeksBirths: Long = 0) {
    fun addBirth() {
      todaysBirths++
    }

    fun setNextWeeksBirth(birthCount: Long) {
      nextWeeksBirths = birthCount
    }

    fun moveNextWeekToToday() {
      todaysBirths += nextWeeksBirths
      nextWeeksBirths = 0
    }
  }
}
