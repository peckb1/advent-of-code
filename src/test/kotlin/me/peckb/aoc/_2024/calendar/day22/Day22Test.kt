package me.peckb.aoc._2024.calendar.day22

import javax.inject.Inject

import me.peckb.aoc._2024.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day22Test {
  @Inject
  lateinit var day22: Day22

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay22PartOne() {
    assertEquals(16039090236, day22.partOne(DAY_22))
  }

  @Test
  fun testDay22PartTwo() {
    assertEquals(1808, day22.partTwo(DAY_22))
  }

  companion object {
    private const val DAY_22: String = "advent-of-code-input/2024/day22.input"
  }
}
