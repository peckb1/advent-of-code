package me.peckb.aoc._2024.calendar.day11

import javax.inject.Inject

import me.peckb.aoc._2024.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day11Test {
  @Inject
  lateinit var day11: Day11

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay11PartOne() {
    assertEquals(185205, day11.partOne(DAY_11))
  }

  @Test
  fun testDay11PartTwo() {
    assertEquals(221280540398419, day11.partTwo(DAY_11))
  }

  companion object {
    private const val DAY_11: String = "advent-of-code-input/2024/day11.input"
  }
}
