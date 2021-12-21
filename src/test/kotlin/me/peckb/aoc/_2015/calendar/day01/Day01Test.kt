package me.peckb.aoc._2015.calendar.day01

import me.peckb.aoc._2015.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day01Test {
  companion object {
    const val DAY_01 = "src/test/resources/2015/day01.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day01: Day01

  @Test
  fun testDay01PartOne() {
    assertEquals(280, day01.partOne(DAY_01))
  }

  @Test
  fun testDay01PartTwo() {
    assertEquals(1797, day01.partTwo(DAY_01))
  }
}
