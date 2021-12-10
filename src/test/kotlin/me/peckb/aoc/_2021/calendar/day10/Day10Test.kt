package me.peckb.aoc._2021.calendar.day10

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day10Test {
  companion object {
    const val DAY_10 = "src/test/resources/2021/day10.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day10: Day10

  @Test
  fun testDay10PartOne() {
    assertEquals(-1, day10.partOne(DAY_10))
  }

  @Test
  fun testDay10PartTwo() {
    assertEquals(-1, day10.partTwo(DAY_10))
  }
}
