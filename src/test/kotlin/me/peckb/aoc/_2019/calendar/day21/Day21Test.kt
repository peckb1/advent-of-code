package me.peckb.aoc._2019.calendar.day21

import javax.inject.Inject


import me.peckb.aoc._2019.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day21Test {
  @Inject
  lateinit var day21: Day21

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay21PartOne() {
    assertEquals(19360724, day21.partOne(DAY_21))
  }

  @Test
  fun testDay21PartTwo() {
    assertEquals(1140450681, day21.partTwo(DAY_21))
  }

  companion object {
    private const val DAY_21: String = "src/test/resources/2019/day21.input"
  }
}
