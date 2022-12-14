package me.peckb.aoc._2022.calendar.day14

import javax.inject.Inject


import me.peckb.aoc._2022.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day14Test {
  @Inject
  lateinit var day14: Day14

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay14PartOne() {
    assertEquals(614, day14.partOne(DAY_14))
  }

  @Test
  fun testDay14PartTwo() {
    assertEquals(26170, day14.partTwo(DAY_14))
  }

  companion object {
    private const val DAY_14: String = "advent-of-code-input/2022/day14.input"
  }
}
