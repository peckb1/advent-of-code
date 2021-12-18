package me.peckb.aoc._2021.calendar.day17

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day17Test {
  companion object {
    const val DAY_17 = "src/test/resources/2021/day17.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day17: Day17

  @Test
  fun testDay17PartOne() {
    assertEquals(5050, day17.maxHeight(DAY_17))
  }

  @Test
  fun testDay17PartTwo() {
    assertEquals(2223, day17.totalNumberOfLaunchVelocities(DAY_17))
  }
}
