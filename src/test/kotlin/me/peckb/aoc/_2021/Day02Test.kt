package me.peckb.aoc._2021

import me.peckb.aoc._2021.calendar.Day02
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day02Test {
  companion object {
    const val DAY_02 = "src/test/resources/2021/day02.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day02: Day02

  @Test
  fun testDay02PartOne() {
    assertEquals(2322630, day02.travel(DAY_02))
  }

  @Test
  fun testDay02PartTwo() {
    assertEquals(2105273490, day02.travelAndAim(DAY_02))
  }
}
