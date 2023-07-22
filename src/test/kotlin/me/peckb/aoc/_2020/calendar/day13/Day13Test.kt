package me.peckb.aoc._2020.calendar.day13

import javax.inject.Inject


import me.peckb.aoc._2020.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day13Test {
  @Inject
  lateinit var day13: Day13

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay13PartOne() {
    assertEquals(3464, day13.partOne(DAY_13))
  }

  @Test
  fun testDay13PartTwo() {
    assertEquals(760_171_380_521_445, day13.partTwo(DAY_13))
  }

  companion object {
    private const val DAY_13: String = "advent-of-code-input/2020/day13.input"
  }
}
