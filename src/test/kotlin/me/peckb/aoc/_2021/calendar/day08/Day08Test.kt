package me.peckb.aoc._2021.calendar.day08

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day08Test {
  companion object {
    const val DAY_08 = "advent-of-code-input/2021/day08.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day08: Day08

  @Test
  fun testDay08PartOne() {
    assertEquals(392, day08.findUniqueNumbers(DAY_08))
  }

  @Test
  fun testDay08PartTwo() {
    assertEquals(1004688, day08.sumAllOutputs(DAY_08))
  }
}
