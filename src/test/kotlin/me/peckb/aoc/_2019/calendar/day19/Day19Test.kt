package me.peckb.aoc._2019.calendar.day19

import javax.inject.Inject


import me.peckb.aoc._2019.DaggerTestDayComponent
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
    assertEquals(229, day19.partOne(DAY_19))
  }

  @Test
  fun testDay19PartTwo() {
    assertEquals(6950903, day19.partTwo(DAY_19))
  }

  companion object {
    private const val DAY_19: String = "advent-of-code-input/2019/day19.input"
  }
}
