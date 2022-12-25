package me.peckb.aoc._2022.calendar.day25

import javax.inject.Inject


import me.peckb.aoc._2022.DaggerTestDayComponent
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
  fun testDay25PartZero() {
    assertEquals("2=2-1-010==-0-1-=--2", day25.partZero(DAY_25))
  }

  @Test
  fun testDay25PartOne() {
    assertEquals("2=2-1-010==-0-1-=--2", day25.partOne(DAY_25))
  }

  companion object {
    private const val DAY_25: String = "advent-of-code-input/2022/day25.input"
  }
}
