package me.peckb.aoc._2019.calendar.day06

import javax.inject.Inject


import me.peckb.aoc._2019.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day06Test {
  @Inject
  lateinit var day06: Day06

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay06PartOne() {
    assertEquals(261306, day06.partOne(DAY_06))
  }

  @Test
  fun testDay06PartTwo() {
    assertEquals(382, day06.partTwo(DAY_06))
  }

  companion object {
    private const val DAY_06: String = "src/test/resources/2019/day06.input"
  }
}
