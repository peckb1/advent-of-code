package me.peckb.aoc._2019.calendar.day05

import javax.inject.Inject


import me.peckb.aoc._2019.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day05Test {
  @Inject
  lateinit var day05: Day05

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create()
      .inject(this)
  }

  @Test
  fun testDay05PartOne() {
    var lastOutput: Int? = null
    day05.partOne(
      filename = DAY_05,
      userInput = { 1 },
      outputHandler = { lastOutput = it }
    )
    assertEquals(12896948, lastOutput)
  }

  @Test
  fun testDay05PartTwo() {
    var lastOutput: Int? = null
    day05.partOne(
      filename = DAY_05,
      userInput = { 5 },
      outputHandler = { lastOutput = it }
    )
    assertEquals(7704130, lastOutput)
  }

  companion object {
    private const val DAY_05: String = "src/test/resources/2019/day05.input"
  }
}
