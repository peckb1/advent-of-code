package me.peckb.aoc._2024.calendar.day15

import javax.inject.Inject

import me.peckb.aoc._2024.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day15Test {
  @Inject
  lateinit var day15: Day15

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay15PartOne() {
    assertEquals(1475249, day15.partOne(DAY_15))
  }

  @Test
  fun testDay15PartTwo() {
    assertEquals(-1, day15.partTwo(DAY_15))
  }

  companion object {
    private const val DAY_15: String = "advent-of-code-input/2024/day15.input"
  }
}
