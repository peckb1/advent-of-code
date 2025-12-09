package me.peckb.aoc._2025.calendar.day09

import javax.inject.Inject

import me.peckb.aoc._2025.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day09Test {
  @Inject
  lateinit var day09: Day09

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay09PartOne() {
    assertEquals(4749929916, day09.partOne(DAY_09))
  }

  @Test
  fun testDay09PartTwo() {
    assertEquals(1572047142, day09.partTwo(DAY_09))
  }

  companion object {
    private const val DAY_09: String = "advent-of-code-input/2025/day09.input"
  }
}
