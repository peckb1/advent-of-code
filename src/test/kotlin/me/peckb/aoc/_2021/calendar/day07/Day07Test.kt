package me.peckb.aoc._2021.calendar.day07

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day07Test {
  companion object {
    const val DAY_07 = "advent-of-code-input/2021/day07.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day07: Day07

  @Test
  fun testDay07PartOne() {
    assertEquals(355150, day07.findSimpleCrabCost(DAY_07))
  }

  @Test
  fun testDay07PartTwo() {
    assertEquals(98368490, day07.findComplexCrabCost(DAY_07))
  }
}
