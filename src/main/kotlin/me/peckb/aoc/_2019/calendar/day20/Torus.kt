package me.peckb.aoc._2019.calendar.day20

import java.lang.IllegalArgumentException

class Torus(
  val map: List<List<Day20.Area>>,
  val outerNorthIndex: Int,
  val outerEastIndex: Int,
  val outerSouthIndex: Int,
  val outerWestIndex: Int,
  val innerNorthIndex: Int,
  val innerEastIndex: Int,
  val innerSouthIndex: Int,
  val innerWestIndex: Int,
) {
  fun print() {
    map.forEach { row ->
      row.forEach { area ->
        val c = when(area) {
          is Day20.Area.Path -> '.'
          is Day20.Area.Portal -> area.data
          Day20.Area.Nothingness -> ' '
          Day20.Area.Wall -> '#'
        }
        print(c)
      }
      println()
    }
  }

  companion object {
    fun setup(input: Sequence<String>): Torus {
      var outerNorthIndex = -1
      var outerEastIndex = -1
      var outerSouthIndex = -1
      var outerWestIndex = -1

      val map = input.mapIndexed { y, row ->
        row.mapIndexed { x, c ->
          when (c) {
            in ('A'..'Z') -> Day20.Area.Portal(c)
            ' ' -> Day20.Area.Nothingness
            '#' -> {
              if (outerNorthIndex == -1) {
                outerNorthIndex = y
                outerWestIndex = x
              }
              outerEastIndex = x
              outerSouthIndex = y
              Day20.Area.Wall
            }

            '.' -> Day20.Area.Path(x, y)
            else -> throw IllegalArgumentException("Unknown area: [#it]")
          }
        }
      }.toList()

      val (innerWestIndex, innerEastIndex) = findInnerEastWest(map)
      val (innerNorthIndex, innerSouthIndex) = findInnerNorthSouth(map, innerWestIndex, outerNorthIndex)

      return Torus(
        map,
        outerNorthIndex,
        outerEastIndex,
        outerSouthIndex,
        outerWestIndex,
        innerNorthIndex,
        innerEastIndex,
        innerSouthIndex,
        innerWestIndex,
      )
    }

    private fun findInnerNorthSouth(
      map: List<List<Day20.Area>>,
      innerWestIndex: Int,
      outerNorthIndex: Int
    ): Pair<Int, Int> {
      val maxY = map.size

      var innerNorthIndex = -1
      var innerSouthIndex = -1
      var foundCenter = false

      (outerNorthIndex until maxY).forEach loop@{ y ->
        if (y < innerNorthIndex) return@loop

        val area = map[y][innerWestIndex + 1]
        if (!foundCenter) {
          if (area is Day20.Area.Portal || area is Day20.Area.Nothingness) {
            innerNorthIndex = y - 1
            foundCenter = true
          }
        } else {
          if (area is Day20.Area.Wall) {
            innerSouthIndex = y
            return innerNorthIndex to innerSouthIndex
          }
          if (area is Day20.Area.Portal && map[y][y + 1] is Day20.Area.Portal) {
            innerSouthIndex = y + 2
            return innerNorthIndex to innerSouthIndex
          }
        }
      }

      return innerNorthIndex to innerSouthIndex
    }

    private fun findInnerEastWest(map: List<List<Day20.Area>>, midY: Int = map.size / 2) = map[midY].let let@{
      var innerWestIndex = -1
      var innerEastIndex = -1
      var foundFirstWall = false
      var foundCenter = false

      it.forEachIndexed loop@{ x, area ->
        if (x < innerWestIndex) return@loop
        if (!foundFirstWall) {
          if (area is Day20.Area.Wall) {
            foundFirstWall = true
          }
          if (area is Day20.Area.Nothingness) {
            foundFirstWall = true
            innerWestIndex = x + 2
          }
        } else if (!foundCenter) {
          if (area is Day20.Area.Nothingness || area is Day20.Area.Portal) {
            foundCenter = true
            innerWestIndex = x - 1
          }
        } else {
          if (area is Day20.Area.Wall) {
            innerEastIndex = x
            return@let innerWestIndex to innerEastIndex
          }
          if (area is Day20.Area.Portal && it[x + 1] is Day20.Area.Portal) {
            foundFirstWall = true
            innerEastIndex = x + 2
            return@let innerWestIndex to innerEastIndex
          }
        }
      }

      innerWestIndex to innerEastIndex
    }
  }
}
