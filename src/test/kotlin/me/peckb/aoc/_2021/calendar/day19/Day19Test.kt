package me.peckb.aoc._2021.calendar.day19

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day19Test {
  companion object {
    const val DAY_19 = "advent-of-code-input/2021/day19.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day19: Day19

  @Test
  fun testDay19PartOne() {
    assertEquals(430, day19.partOne(DAY_19))
  }

  @Test
  fun testDay19PartTwo() {
    assertEquals(11860, day19.partTwo(DAY_19))
  }
}
