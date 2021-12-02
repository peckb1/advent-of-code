package me.peckb.aoc._2021

import me.peckb.aoc._2021.calendar.Day1
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day1Test {
  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day1: Day1

  @Test
  fun testDay1PartOne() {
    assertEquals(1390, day1.largerMeasurements("src/test/resources/2021/day1.first.input"))
  }

  @Test
  fun testDay1PartTwo() {
    assertEquals(1457, day1.largerGroupedMeasurements("src/test/resources/2021/day1.second.input"))
  }
}