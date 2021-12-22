package me.peckb.aoc._2021.calendar.day22

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day22Test {
  companion object {
    const val DAY_22 = "src/test/resources/2021/day22.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day22: Day22

  @Test
  fun testDay22PartOne() {
    assertEquals(612714, day22.partOne(DAY_22))
  }

  @Test
  fun testDay22PartTwo() {
    assertEquals(1311612259117092, day22.partTwo(DAY_22))
  }
}
