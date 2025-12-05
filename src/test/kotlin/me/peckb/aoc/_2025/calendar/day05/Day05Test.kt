package me.peckb.aoc._2025.calendar.day05

import javax.inject.Inject

import me.peckb.aoc._2025.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day05Test {
  @Inject
  lateinit var day05: Day05

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay05PartOne() {
    assertEquals(798, day05.partOne(DAY_05))
  }

  @Test
  fun testDay05PartTwo() {
    assertEquals(366181852921027, day05.partTwo(DAY_05))
  }

  companion object {
    private const val DAY_05: String = "advent-of-code-input/2025/day05.input"
  }
}
