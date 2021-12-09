package me.peckb.aoc._2021.calendar.day09

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day09Test {
  companion object {
    const val DAY_09 = "src/test/resources/2021/day09.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day09: Day09

  @Test
  fun testDay09PartOne() {
    assertEquals(-1, day09.partOne(DAY_09))
  }

  @Test
  fun testDay09PartTwo() {
    assertEquals(-1, day09.partTwo(DAY_09))
  }
}
