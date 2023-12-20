package me.peckb.aoc._2023.calendar.day20

import javax.inject.Inject


import me.peckb.aoc._2023.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day20Test {
  @Inject
  lateinit var day20: Day20

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay20PartOne() {
    assertEquals(938_065_580, day20.partOne(DAY_20))
  }

  @Test
  fun testDay20PartTwo() {
    assertEquals(250_628_960_065_793, day20.partTwo(DAY_20))
  }

  companion object {
    private const val DAY_20: String = "advent-of-code-input/2023/day20.input"
  }
}
