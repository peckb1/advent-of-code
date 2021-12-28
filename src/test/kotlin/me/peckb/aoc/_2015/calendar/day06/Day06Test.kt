package me.peckb.aoc._2015.calendar.day06

import me.peckb.aoc._2015.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day06Test {
  companion object {
    const val DAY_06 = "src/test/resources/2015/day06.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day06: Day06

  @Test
  fun testDay06PartOne() {
    assertEquals(543903, day06.partOne(DAY_06))
  }

  @Test
  fun testDay06PartTwo() {
    assertEquals(14687245, day06.partTwo(DAY_06))
  }
}
