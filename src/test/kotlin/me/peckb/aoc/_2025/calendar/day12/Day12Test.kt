package me.peckb.aoc._2025.calendar.day12

import javax.inject.Inject

import me.peckb.aoc._2025.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day12Test {
  @Inject
  lateinit var day12: Day12

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay12PartOne() {
    assertEquals(589, day12.partOne(DAY_12))
  }

  companion object {
    private const val DAY_12: String = "advent-of-code-input/2025/day12.input"
  }
}
