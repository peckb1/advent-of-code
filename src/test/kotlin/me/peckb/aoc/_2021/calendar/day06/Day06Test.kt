package me.peckb.aoc._2021.calendar.day06

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day06Test {
  companion object {
    const val DAY_06 = "src/test/resources/2021/day06.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day06: Day06

  @Test
  fun testDay06PartOne() {
    assertEquals(359344, day06.spawnSomeFish(DAY_06))
  }

  @Test
  fun testDay06PartTwo() {
    assertEquals(1629570219571, day06.spawnAllTheFish(DAY_06))
  }
}
