package me.peckb.aoc._2017.calendar.day15

import javax.inject.Inject


import me.peckb.aoc._2017.DaggerTestDayComponent
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
    assertEquals(592, day15.partOne(DAY_15))
  }

  @Test
  fun testDay15PartTwo() {
    assertEquals(320, day15.partTwo(DAY_15))
  }

  companion object {
    private const val DAY_15: String = "advent-of-code-input/2017/day15.input"
  }
}
