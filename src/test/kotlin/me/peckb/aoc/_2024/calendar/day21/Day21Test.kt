package me.peckb.aoc._2024.calendar.day21

import javax.inject.Inject

import me.peckb.aoc._2024.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day21Test {
  @Inject
  lateinit var day21: Day21

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay21PartOne() {
    assertEquals(246_990, day21.partOne(DAY_21))
  }

  @Test
  fun testDay21PartTwo() {
    assertEquals(306_335_137_543_664, day21.partTwo(DAY_21))
  }

  companion object {
    private const val DAY_21: String = "advent-of-code-input/2024/day21.input"
  }
}
