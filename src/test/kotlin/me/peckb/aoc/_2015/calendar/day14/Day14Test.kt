package me.peckb.aoc._2015.calendar.day14

import javax.inject.Inject


import me.peckb.aoc._2015.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day14Test {
  @Inject
  lateinit var day14: Day14

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay14PartOne() {
    assertEquals(2655, day14.partOne(DAY_14))
  }

  @Test
  fun testDay14PartTwo() {
    assertEquals(1059, day14.partTwo(DAY_14))
  }

  companion object {
    private const val DAY_14: String = "src/test/resources/2015/day14.input"
  }
}
