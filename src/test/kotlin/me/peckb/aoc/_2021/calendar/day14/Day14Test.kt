package me.peckb.aoc._2021.calendar.day14

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day14Test {
  companion object {
    const val DAY_14 = "src/test/resources/2021/day14.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day14: Day14

  @Test
  fun testDay14PartOne() {
    assertEquals(-1, day14.partOne(DAY_14))
  }

  @Test
  fun testDay14PartTwo() {
    assertEquals(-1, day14.partTwo(DAY_14))
  }
}
