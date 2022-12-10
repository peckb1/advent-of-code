package me.peckb.aoc._2021.calendar.day25

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day25Test {
  companion object {
    const val DAY_25 = "advent-of-code-input/2021/day25.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day25: Day25

  @Test
  fun testDay25PartOne() {
    assertEquals(532, day25.partOne(DAY_25))
  }
}
