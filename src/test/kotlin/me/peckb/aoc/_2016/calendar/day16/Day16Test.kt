package me.peckb.aoc._2016.calendar.day16

import javax.inject.Inject


import me.peckb.aoc._2016.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day16Test {
  @Inject
  lateinit var day16: Day16

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay16PartOne() {
    assertEquals("10010100110011100", day16.partOne(DAY_16))
  }

  @Test
  fun testDay16PartTwo() {
    assertEquals("01100100101101100", day16.partTwo(DAY_16))
  }

  companion object {
    private const val DAY_16: String = "src/test/resources/2016/day16.input"
  }
}
