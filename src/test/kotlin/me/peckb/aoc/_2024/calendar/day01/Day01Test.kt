package me.peckb.aoc._2024.calendar.day01

import javax.inject.Inject

import me.peckb.aoc._2024.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day01Test {
  @Inject
  lateinit var day01: Day01

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay01PartOne() {
    assertEquals(1651298, day01.partOne(DAY_01))
  }

  @Test
  fun testDay01PartTwo() {
    assertEquals(21306195, day01.partTwo(DAY_01))
  }

  companion object {
    private const val DAY_01: String = "advent-of-code-input/2024/day01.input"
  }
}
