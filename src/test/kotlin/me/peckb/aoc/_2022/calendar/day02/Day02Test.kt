package me.peckb.aoc._2022.calendar.day02

import javax.inject.Inject


import me.peckb.aoc._2022.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day02Test {
  @Inject
  lateinit var day02: Day02

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay02PartOne() {
    assertEquals(10994, day02.partOne(DAY_02))
  }

  @Test
  fun testDay02PartTwo() {
    assertEquals(12526, day02.partTwo(DAY_02))
  }

  companion object {
    private const val DAY_02: String = "src/test/resources/2022/day02.input"
  }
}
