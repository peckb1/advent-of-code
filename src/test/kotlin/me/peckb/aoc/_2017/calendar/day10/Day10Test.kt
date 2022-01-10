package me.peckb.aoc._2017.calendar.day10

import javax.inject.Inject


import me.peckb.aoc._2017.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day10Test {
  @Inject
  lateinit var day10: Day10

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay10PartOne() {
    assertEquals(15990, day10.partOne(DAY_10))
  }

  @Test
  fun testDay10PartTwo() {
    assertEquals("90adb097dd55dea8305c900372258ac6", day10.partTwo(DAY_10))
  }

  companion object {
    private const val DAY_10: String = "src/test/resources/2017/day10.input"
  }
}
