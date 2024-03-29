package me.peckb.aoc._2021.calendar.day18

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day18Test {
  companion object {
    const val DAY_18 = "advent-of-code-input/2021/day18.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day18: Day18

  @Test
  fun testDay18PartOne() {
    assertEquals(4202, day18.findMagnitude(DAY_18))
  }

  @Test
  fun testDay18PartTwo() {
    assertEquals(4779, day18.findBestTwoPartMagnitude(DAY_18))
  }
}
