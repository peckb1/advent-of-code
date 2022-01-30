package me.peckb.aoc._2018.calendar.day23

import javax.inject.Inject


import me.peckb.aoc._2018.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day23Test {
  @Inject
  lateinit var day23: Day23

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay23PartOne() {
    assertEquals(906, day23.partOne(DAY_23))
  }

  @Test
  fun testDay23PartTwo() {
    assertEquals(121493971, day23.partTwo(DAY_23))
  }

  companion object {
    private const val DAY_23: String = "src/test/resources/2018/day23.input"
  }
}
