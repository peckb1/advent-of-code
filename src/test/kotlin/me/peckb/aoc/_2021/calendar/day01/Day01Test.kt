package me.peckb.aoc._2021.calendar.day01

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day01Test {
  companion object {
    const val DAY_01 = "src/test/resources/2021/day01.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day01: Day01

  @Test
  fun testDay01PartOne() {
    assertEquals(1390, day01.largerMeasurements(DAY_01))
  }

  @Test
  fun testDay01PartTwo() {
    assertEquals(1457, day01.largerGroupedMeasurements(DAY_01))
  }
}
