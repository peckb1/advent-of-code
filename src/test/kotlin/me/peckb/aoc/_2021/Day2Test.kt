package me.peckb.aoc._2021

import me.peckb.aoc._2021.calendar.Day2
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day2Test {
  companion object {
    const val DAY_2_PART_1 = "src/test/resources/2021/day2.first.input"
    const val DAY_2_PART_2 = "src/test/resources/2021/day2.second.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day2: Day2

  @Test
  fun testDay1PartOne() {
    assertEquals(2322630, day2.travel(DAY_2_PART_1))
  }

  @Test
  fun testDay1PartTwo() {
    assertEquals(2105273490, day2.travelAndAim(DAY_2_PART_2))
  }
}
