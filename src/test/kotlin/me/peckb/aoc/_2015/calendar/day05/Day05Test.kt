package me.peckb.aoc._2015.calendar.day05

import me.peckb.aoc._2015.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day05Test {
  companion object {
    const val DAY_05 = "src/test/resources/2015/day05.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day05: Day05

  @Test
  fun testDay05PartOne() {
    assertEquals(238, day05.partOne(DAY_05))
  }

  @Test
  fun testDay05PartTwo() {
    assertEquals(69, day05.partTwo(DAY_05))
  }
}
