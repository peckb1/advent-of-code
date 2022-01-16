package me.peckb.aoc._2018.calendar.day05

import javax.inject.Inject


import me.peckb.aoc._2018.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day05Test {
  @Inject
  lateinit var day05: Day05

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay05PartOne() {
    assertEquals(9238, day05.partOne(DAY_05))
  }

  @Test
  fun testDay05PartTwo() {
    assertEquals(4052, day05.partTwo(DAY_05))
  }

  companion object {
    private const val DAY_05: String = "src/test/resources/2018/day05.input"
  }
}
