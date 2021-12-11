package me.peckb.aoc._2021.calendar.day11

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day11Test {
  companion object {
    const val DAY_11 = "src/test/resources/2021/day11.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day11: Day11

  @Test
  fun testDay11PartOne() {
    assertEquals(1755, day11.partOne(DAY_11))
  }

  @Test
  fun testDay11PartTwo() {
    assertEquals(212, day11.partTwo(DAY_11))
  }
}
