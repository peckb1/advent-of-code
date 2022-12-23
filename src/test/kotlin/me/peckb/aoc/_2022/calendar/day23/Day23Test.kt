package me.peckb.aoc._2022.calendar.day23

import javax.inject.Inject


import me.peckb.aoc._2022.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day23Test {
  @Inject
  lateinit var day23: Day23

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay23PartOne() {
    assertEquals(3990, day23.partOne(DAY_23))
  }

  @Test
  fun testDay23PartTwo() {
    assertEquals(1057, day23.partTwo(DAY_23))
  }

  companion object {
    private const val DAY_23: String = "advent-of-code-input/2022/day23.input"
  }
}
