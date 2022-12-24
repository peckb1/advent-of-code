package me.peckb.aoc._2022.calendar.day24

import javax.inject.Inject


import me.peckb.aoc._2022.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day24Test {
  @Inject
  lateinit var day24: Day24

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay24PartOne() {
    assertEquals(266, day24.partOne(DAY_24))
  }

  @Test
  fun testDay24PartTwo() {
    assertEquals(853, day24.partTwo(DAY_24))
  }

  companion object {
    private const val DAY_24: String = "advent-of-code-input/2022/day24.input"
  }
}
