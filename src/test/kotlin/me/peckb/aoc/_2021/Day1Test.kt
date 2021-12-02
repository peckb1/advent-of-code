package me.peckb.aoc._2021

import me.peckb.aoc._2021.calendar.Day1
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day1Test {
  companion object {
    const val DAY_1_PART_1 = "src/test/resources/2021/day1.first.input"
    const val DAY_1_PART_2 = "src/test/resources/2021/day1.second.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day1: Day1

  @Test
  fun testDay1PartOne() {
    assertEquals(1390, day1.largerMeasurements(DAY_1_PART_1))
  }

  @Test
  fun testDay1PartTwo() {
    assertEquals(1457, day1.largerGroupedMeasurements(DAY_1_PART_2))
  }
}