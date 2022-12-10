package me.peckb.aoc._2015.calendar.day07

import me.peckb.aoc._2015.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day07Test {
  companion object {
    const val DAY_07 = "advent-of-code-input/2015/day07.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day07: Day07

  @Test
  fun testDay07PartOne() {
    assertEquals(16076, day07.partOne(DAY_07))
  }

  @Test
  fun testDay07PartTwo() {
    assertEquals(2797, day07.partTwo(DAY_07))
  }
}
