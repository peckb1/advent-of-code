package me.peckb.aoc._2019.calendar.day07

import javax.inject.Inject


import me.peckb.aoc._2019.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day07Test {
  @Inject
  lateinit var day07: Day07

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay07PartOne() {
    assertEquals(19650, day07.partOne(DAY_07))
  }

  @Test
  fun testDay07PartTwo() {
    assertEquals(35961106, day07.partTwo(DAY_07))
  }

  companion object {
    private const val DAY_07: String = "advent-of-code-input/2019/day07.input"
  }
}
