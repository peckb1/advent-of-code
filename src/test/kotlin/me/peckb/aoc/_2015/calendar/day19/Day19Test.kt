package me.peckb.aoc._2015.calendar.day19

import javax.inject.Inject


import me.peckb.aoc._2015.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day19Test {
  @Inject
  lateinit var day19: Day19

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay19PartOne() {
    assertEquals(509, day19.partOne(DAY_19))
  }

  @Test
  fun testDay19PartTwo() {
    assertEquals(195, day19.partTwo(DAY_19))
  }

  companion object {
    private const val DAY_19: String = "advent-of-code-input/2015/day19.input"
  }
}
