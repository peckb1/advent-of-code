package me.peckb.aoc._2017.calendar.day12

import javax.inject.Inject


import me.peckb.aoc._2017.DaggerTestDayComponent
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
    assertEquals(169, day12.partOne(DAY_12))
  }

  @Test
  fun testDay12PartTwo() {
    assertEquals(179, day12.partTwo(DAY_12))
  }

  companion object {
    private const val DAY_12: String = "advent-of-code-input/2017/day12.input"
  }
}
