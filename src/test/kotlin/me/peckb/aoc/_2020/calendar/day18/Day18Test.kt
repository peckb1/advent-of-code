package me.peckb.aoc._2020.calendar.day18

import javax.inject.Inject


import me.peckb.aoc._2020.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day18Test {
  @Inject
  lateinit var day18: Day18

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay18PartOne() {
    assertEquals(36382392389406, day18.partOne(DAY_18))
  }

  @Test
  fun testDay18PartTwo() {
    assertEquals(381107029777968, day18.partTwo(DAY_18))
  }

  companion object {
    private const val DAY_18: String = "advent-of-code-input/2020/day18.input"
  }
}
