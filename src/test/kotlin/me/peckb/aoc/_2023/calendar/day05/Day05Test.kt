package me.peckb.aoc._2023.calendar.day05

import javax.inject.Inject


import me.peckb.aoc._2023.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day05Test {
  @Inject
  lateinit var day05: Day05

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay05PartOne() {
    assertEquals(462648396, day05.partOne(DAY_05))
  }

  @Test
  fun testDay05PartTwo() {
    assertEquals(2520479, day05.partTwo(DAY_05))
  }

  companion object {
    private const val DAY_05: String = "advent-of-code-input/2023/day05.input"
  }
}
