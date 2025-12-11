package me.peckb.aoc._2025.calendar.day11

import javax.inject.Inject

import me.peckb.aoc._2025.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day11Test {
  @Inject
  lateinit var day11: Day11

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay11PartOne() {
    assertEquals(423, day11.partOne(DAY_11))
  }

  @Test
  fun testDay11PartTwo() {
    assertEquals(333657640517376, day11.partTwo(DAY_11))
  }

  companion object {
    private const val DAY_11: String = "advent-of-code-input/2025/day11.input"
  }
}
