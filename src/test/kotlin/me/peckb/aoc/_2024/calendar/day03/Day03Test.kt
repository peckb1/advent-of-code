package me.peckb.aoc._2024.calendar.day03

import javax.inject.Inject

import me.peckb.aoc._2024.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day03Test {
  @Inject
  lateinit var day03: Day03

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay03PartOne() {
    assertEquals(173517243, day03.partOne(DAY_03))
  }

  @Test
  fun testDay03PartTwo() {
    assertEquals(100450138, day03.partTwo(DAY_03))
  }

  companion object {
    private const val DAY_03: String = "advent-of-code-input/2024/day03.input"
  }
}
