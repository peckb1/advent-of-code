package me.peckb.aoc._2016.calendar.day25

import javax.inject.Inject


import me.peckb.aoc._2016.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day25Test {
  @Inject
  lateinit var day25: Day25

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay25PartOne() {
    assertEquals(175, day25.partOne(DAY_25))
  }

  companion object {
    private const val DAY_25: String = "advent-of-code-input/2016/day25.input"
  }
}
