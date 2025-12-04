package me.peckb.aoc._2025.calendar.day04

import javax.inject.Inject

import me.peckb.aoc._2025.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day04Test {
  @Inject
  lateinit var day04: Day04

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay04PartOne() {
    assertEquals(1508, day04.partOne(DAY_04))
  }

  @Test
  fun testDay04PartTwo() {
    assertEquals(8538, day04.partTwo(DAY_04))
  }

  companion object {
    private const val DAY_04: String = "advent-of-code-input/2025/day04.input"
  }
}
