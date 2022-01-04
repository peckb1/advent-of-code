package me.peckb.aoc._2015.calendar.day22

import javax.inject.Inject


import me.peckb.aoc._2015.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day22Test {
  @Inject
  lateinit var day22: Day22
  @Inject
  lateinit var day22Try2: Day22Try2

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay22PartOne() {
    assertEquals(1269, day22Try2.partOne(DAY_22))
  }

  @Test
  fun testDay22PartTwo() {
    assertEquals(-1, day22Try2.partTwo(DAY_22))
  }

  companion object {
    private const val DAY_22: String = "src/test/resources/2015/day22.input"
  }
}
