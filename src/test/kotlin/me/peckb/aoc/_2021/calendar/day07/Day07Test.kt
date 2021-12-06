package me.peckb.aoc._2021.calendar.day07

import me.peckb.aoc._2021.DaggerTestDayComponent
import me.peckb.aoc._2021.calendar.day07.Day07
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day07Test {
  companion object {
    const val DAY_07 = "src/test/resources/2021/day07.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day07: Day07

  @Test
  fun testDay05PartOne() {
    assertEquals(-1, day07.partOne(DAY_07))
  }

  @Test
  fun testDay05PartTwo() {
    assertEquals(-1, day07.partTwo(DAY_07))
  }
}
