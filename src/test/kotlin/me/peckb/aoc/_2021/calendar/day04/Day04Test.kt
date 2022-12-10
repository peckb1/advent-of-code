package me.peckb.aoc._2021.calendar.day04

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day04Test {
  companion object {
    const val DAY_04 = "advent-of-code-input/2021/day04.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day04: Day04

  @Test
  fun testDay04PartOne() {
    assertEquals(6592, day04.partOne(DAY_04))
  }

  @Test
  fun testDay04PartTwo() {
    assertEquals(31755, day04.partTwo(DAY_04))
  }
}
