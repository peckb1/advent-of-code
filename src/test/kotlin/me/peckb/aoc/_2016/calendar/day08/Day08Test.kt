package me.peckb.aoc._2016.calendar.day08

import javax.inject.Inject


import me.peckb.aoc._2016.DaggerTestDayComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Day08Test {
  @Inject
  lateinit var day08: Day08

  @BeforeEach
  fun setup() {
    DaggerTestDayComponent.create().inject(this)
  }

  @Test
  fun testDay08PartOne() {
    assertEquals(115, day08.partOne(DAY_08))
  }

  @Test
  fun testDay08PartTwo() {
    val data = """
#### #### #### #   ##  # #### ###  ####  ###   ## 
#    #    #    #   ## #  #    #  # #      #     # 
###  ###  ###   # # ##   ###  #  # ###    #     # 
#    #    #      #  # #  #    ###  #      #     # 
#    #    #      #  # #  #    # #  #      #  #  # 
#### #    ####   #  #  # #    #  # #     ###  ##  
    """.trimIndent()
    assertEquals(data, day08.partTwo(DAY_08))
  }

  companion object {
    private const val DAY_08: String = "src/test/resources/2016/day08.input"
  }
}
