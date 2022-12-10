package me.peckb.aoc._2015.calendar.day02

import me.peckb.aoc._2015.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day02Test {
  companion object {
    const val DAY_02 = "advent-of-code-input/2015/day02.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day02: Day02

  @Test
  fun testDay02PartOne() {
    assertEquals(1586300, day02.partOne(DAY_02))
  }

  @Test
  fun testDay02PartTwo() {
    assertEquals(3737498, day02.partTwo(DAY_02))
  }
}
