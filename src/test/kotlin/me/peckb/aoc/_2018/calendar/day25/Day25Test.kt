package me.peckb.aoc._2018.calendar.day25

import javax.inject.Inject


import me.peckb.aoc._2018.DaggerTestDayComponent
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
    assertEquals(-1, day25.partOne(DAY_25))
  }

  companion object {
    private const val DAY_25: String = "src/test/resources/2018/day25.input"
  }
}
