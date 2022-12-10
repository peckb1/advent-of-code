package me.peckb.aoc._2021.calendar.day20

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day20Test {
  companion object {
    const val DAY_20 = "advent-of-code-input/2021/day20.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day20: Day20

  @Test
  fun testDay20PartOne() {
    assertEquals(5884, day20.partOne(DAY_20))
  }

  @Test
  fun testDay20PartTwo() {
    assertEquals(19043, day20.partTwo(DAY_20))
  }
}
