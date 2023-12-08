package me.peckb.aoc._2023.calendar.day08

import javax.inject.Inject


import me.peckb.aoc._2023.DaggerTestDayComponent
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
    assertEquals(22357, day08.partOne(DAY_08))
  }

  @Test
  fun testDay08PartTwo() {
    assertEquals(10_371_555_451_871, day08.partTwo(DAY_08))
  }

  companion object {
    private const val DAY_08: String = "advent-of-code-input/2023/day08.input"
  }
}
