package me.peckb.aoc._2022.calendar.day10

import javax.inject.Inject


import me.peckb.aoc._2022.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day10Test {
  @Inject
  lateinit var day10: Day10

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay10PartOne() {
    assertEquals(12520, day10.partOne(DAY_10))
  }

  @Test
  fun testDay10PartTwo() {
    val crtOutput = """
      #### #  # ###  #### ###    ##  ##  #    
      #    #  # #  #    # #  #    # #  # #    
      ###  #### #  #   #  #  #    # #    #    
      #    #  # ###   #   ###     # # ## #    
      #    #  # #    #    #    #  # #  # #    
      #### #  # #    #### #     ##   ### #### 
    """.trimIndent()
    assertEquals(crtOutput, day10.partTwo(DAY_10))
  }

  companion object {
    private const val DAY_10: String = "advent-of-code-input/2022/day10.input"
  }
}
