package me.peckb.aoc._2018.calendar.day17

import javax.inject.Inject


import me.peckb.aoc._2018.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day17Test {
  @Inject
  lateinit var day17: Day17

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay17PartOne() {
    assertEquals(50838, day17.partOne(DAY_17))
  }

  @Test
  fun testDay17PartTwo() {
    assertEquals(43039, day17.partTwo(DAY_17))
  }

  companion object {
    private const val DAY_17: String = "advent-of-code-input/2018/day17.input"
  }
}
