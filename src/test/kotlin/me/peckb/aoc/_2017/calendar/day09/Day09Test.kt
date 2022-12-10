package me.peckb.aoc._2017.calendar.day09

import javax.inject.Inject


import me.peckb.aoc._2017.DaggerTestDayComponent
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
    assertEquals(12897, day09.partOne(DAY_09))
  }

  @Test
  fun testDay09PartTwo() {
    assertEquals(7031, day09.partTwo(DAY_09))
  }

  companion object {
    private const val DAY_09: String = "advent-of-code-input/2017/day09.input"
  }
}
