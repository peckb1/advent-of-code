package me.peckb.aoc._2016.calendar.day04

import javax.inject.Inject


import me.peckb.aoc._2016.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day04Test {
  @Inject
  lateinit var day04: Day04

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay04PartOne() {
    assertEquals(409147, day04.partOne(DAY_04))
  }

  @Test
  fun testDay04PartTwo() {
    assertEquals(991, day04.partTwo(DAY_04))
  }

  companion object {
    private const val DAY_04: String = "src/test/resources/2016/day04.input"
  }
}
