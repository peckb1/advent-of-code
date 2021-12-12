package me.peckb.aoc._2021.calendar.day12

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day12Test {
  companion object {
    const val DAY_12 = "src/test/resources/2021/day12.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day12: Day12

  @Test
  fun testDay12PartOne() {
    assertEquals(4754, day12.findPathsSingleSmallCave(DAY_12))
  }

  @Test
  fun testDay12PartTwo() {
    assertEquals(143562, day12.findPathsOneDoubleSmallCave(DAY_12))
  }
}
