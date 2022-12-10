package me.peckb.aoc._2019.calendar.day02

import javax.inject.Inject


import me.peckb.aoc._2019.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day02Test {
  @Inject
  lateinit var day02: Day02

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay02PartOne() {
    assertEquals(3765464, day02.partOne(DAY_02))
  }

  @Test
  fun testDay02PartTwo() {
    assertEquals(7610, day02.partTwo(DAY_02))
  }

  companion object {
    private const val DAY_02: String = "advent-of-code-input/2019/day02.input"
  }
}
