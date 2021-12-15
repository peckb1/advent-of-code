package me.peckb.aoc._2021.calendar.day15

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day15Test {
  companion object {
    const val DAY_15 = "src/test/resources/2021/day15.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day15: Day15

  @Test
  fun testDay15PartOne() {
    assertEquals(-1, day15.partOne(DAY_15))
  }

  @Test
  fun testDay15PartTwo() {
    assertEquals(-1, day15.partTwo(DAY_15))
  }
}
