package me.peckb.aoc._2021.calendar.day24

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day24Test {
  companion object {
    const val DAY_24 = "src/test/resources/2021/day24.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day24: Day24

  @Test
  fun testDay24PartOne() {
    assertEquals(-1, day24.partOne(DAY_24))
  }

  @Test
  fun testDay24PartTwo() {
    assertEquals(-1, day24.partTwo(DAY_24))
  }
}
