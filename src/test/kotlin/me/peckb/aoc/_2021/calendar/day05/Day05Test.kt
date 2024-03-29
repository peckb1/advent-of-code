package me.peckb.aoc._2021.calendar.day05

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day05Test {
  companion object {
    const val DAY_05 = "advent-of-code-input/2021/day05.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day05: Day05

  @Test
  fun testDay05PartOne() {
    assertEquals(5585, day05.nonDiagonalOverlapCount(DAY_05))
  }

  @Test
  fun testDay05PartTwo() {
    assertEquals(17193, day05.fullOverlapCount(DAY_05))
  }
}
