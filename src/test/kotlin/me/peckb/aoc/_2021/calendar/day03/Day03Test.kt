package me.peckb.aoc._2021.calendar.day03

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day03Test {
  companion object {
    const val DAY_03 = "advent-of-code-input/2021/day03.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day03: Day03

  @Test
  fun testDay03PartOne() {
    assertEquals(2972336, day03.powerConsumption(DAY_03))
  }

  @Test
  fun testDay03PartTwo() {
    assertEquals(3368358, day03.lifeSupportRating(DAY_03))
  }
}
