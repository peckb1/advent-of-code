package me.peckb.aoc._2021.calendar.day16

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day16Test {
  companion object {
    const val DAY_16 = "src/test/resources/2021/day16.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day16: Day16

  @Test
  fun testDay16PartOne() {
    assertEquals(951, day16.partOne(DAY_16))
  }

  @Test
  fun testDay16PartTwo() {
    assertEquals(902198718880, day16.partTwo(DAY_16))
  }
}
