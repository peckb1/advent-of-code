package me.peckb.aoc._2022.calendar.day08

import javax.inject.Inject


import me.peckb.aoc._2022.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day08Test {
  @Inject
  lateinit var day08: Day08

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay08PartOne() {
    assertEquals(1736, day08.partOne(DAY_08))
  }

  @Test
  fun testDay08PartTwo() {
    assertEquals(268800, day08.partTwo(DAY_08))
  }

  companion object {
    private const val DAY_08: String = "advent-of-code-input/2022/day08.input"
  }
}
