package me.peckb.aoc._2020.calendar.day16

import javax.inject.Inject


import me.peckb.aoc._2020.DaggerTestDayComponent
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
    assertEquals(21071, day16.partOne(DAY_16))
  }

  @Test
  fun testDay16PartTwo() {
    assertEquals(3429967441937, day16.partTwo(DAY_16))
  }

  companion object {
    private const val DAY_16: String = "advent-of-code-input/2020/day16.input"
  }
}
