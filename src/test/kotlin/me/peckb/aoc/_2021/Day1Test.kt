package me.peckb.aoc._2021

import me.peckb.aoc._2021.calendar.Day1
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day1Test {

  private lateinit var day1: Day1

  @BeforeEach
  fun setup() {
    day1 = DaggerDayFactory.create().day1()
  }

  @Test
  fun testDay1PartOne() {
    assertNotNull(day1)
  }
}