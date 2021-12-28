package me.peckb.aoc._2021.calendar.day23

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject
import kotlin.test.Ignore

internal class Day23Test {
  companion object {
    const val DAY_23 = "src/test/resources/2021/day23.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day23: Day23

  @Test
  fun testDay23PartOne() {
    assertEquals(16300, day23.partOne(DAY_23))
  }

  @Test
  fun testDay23PartTwo() {
    assertEquals(48676, day23.partTwo(DAY_23))
  }
}
