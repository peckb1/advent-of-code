package me.peckb.aoc._2023.calendar.day17

import javax.inject.Inject


import me.peckb.aoc._2023.DaggerTestDayComponent
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
    assertEquals(-1, day17.partOne(DAY_17))
  }

  @Test
  fun testDay17PartTwo() {
    assertEquals(-1, day17.partTwo(DAY_17))
  }

  companion object {
    private const val DAY_17: String = "advent-of-code-input/2023/day17.input"
  }
}
