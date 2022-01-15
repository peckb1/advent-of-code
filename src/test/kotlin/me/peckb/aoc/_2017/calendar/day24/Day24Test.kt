package me.peckb.aoc._2017.calendar.day24

import javax.inject.Inject


import me.peckb.aoc._2017.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day24Test {
  @Inject
  lateinit var day24: Day24

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay24PartOne() {
    assertEquals(1868, day24.partOne(DAY_24))
  }

  @Test
  fun testDay24PartTwo() {
    assertEquals(1841, day24.partTwo(DAY_24))
  }

  companion object {
    private const val DAY_24: String = "src/test/resources/2017/day24.input"
  }
}
