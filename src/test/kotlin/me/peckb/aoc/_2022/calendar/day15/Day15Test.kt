package me.peckb.aoc._2022.calendar.day15

import javax.inject.Inject


import me.peckb.aoc._2022.DaggerTestDayComponent
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
    assertEquals(5125700L, day15.partOne(DAY_15))
  }

  @Test
  fun testDay15PartTwo() {
    assertEquals(11379394658764, day15.partTwo(DAY_15))
  }

  companion object {
    private const val DAY_15: String = "advent-of-code-input/2022/day15.input"
  }
}
