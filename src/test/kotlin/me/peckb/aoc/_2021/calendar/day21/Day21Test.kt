package me.peckb.aoc._2021.calendar.day21

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day21Test {
  companion object {
    const val DAY_21 = "src/test/resources/2021/day21.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day21: Day21

  @Test
  fun testDay21PartOne() {
    assertEquals(1073709, day21.partOne(DAY_21))
  }

  @Test
  fun testDay21PartTwo() {
    assertEquals(148747830493442, day21.partTwo(DAY_21))
  }
}
