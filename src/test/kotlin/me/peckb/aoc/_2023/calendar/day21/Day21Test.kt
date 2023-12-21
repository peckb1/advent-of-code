package me.peckb.aoc._2023.calendar.day21

import javax.inject.Inject


import me.peckb.aoc._2023.DaggerTestDayComponent
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
    assertEquals(3782, day21.partOne(DAY_21))
  }

  @Test
  fun testDay21PartTwo() {
    assertEquals(630661863455116, day21.partTwo(DAY_21))
  }

  companion object {
    private const val DAY_21: String = "advent-of-code-input/2023/day21.input"
  }
}
