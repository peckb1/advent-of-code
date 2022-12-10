package me.peckb.aoc._2019.calendar.day09

import javax.inject.Inject


import me.peckb.aoc._2019.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day09Test {
  @Inject
  lateinit var day09: Day09

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay09PartOne() {
    val data = day09.partOne(DAY_09)
    assertEquals(1, data.size)
    assertEquals(3512778005, data.first())
  }

  @Test
  fun testDay09PartTwo() {
    val data = day09.partTwo(DAY_09)
    assertEquals(1, data.size)
    assertEquals(35920, data.first())
  }

  companion object {
    private const val DAY_09: String = "advent-of-code-input/2019/day09.input"
  }
}
