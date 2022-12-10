package me.peckb.aoc._2021.calendar.day13

import me.peckb.aoc._2021.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

internal class Day13Test {
  companion object {
    const val DAY_13 = "advent-of-code-input/2021/day13.input"
  }

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Inject internal lateinit var day13: Day13

  @Test
  fun testDay13PartOne() {
    assertEquals(631, day13.oneFoldCount(DAY_13))
  }

  @Test
  fun testDay13PartTwo() {
    val expected = """
      #### #### #    ####   ##  ##  ###  ####  
      #    #    #    #       # #  # #  # #     
      ###  ###  #    ###     # #    #  # ###   
      #    #    #    #       # # ## ###  #     
      #    #    #    #    #  # #  # # #  #     
      #### #    #### #     ##   ### #  # #     
    """.trimIndent().lines()
    val actual = day13.everyFoldCode(DAY_13).lines()

    expected.indices.forEach {
      assertEquals(expected[it], actual[it])
    }
  }
}
