package me.peckb.aoc._2024.calendar.day12

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.util.LinkedList
import java.util.Queue

class Day12 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (_, fields) = createFields(input)

    fields.sumOf { field -> field.perimeter * field.crops.size }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (area, knownFields) = createFields(input)

    // setup empty map
    val largeArea = mutableListOf<MutableList<Char>>()
    val height = area.size + 4
    val width = area[0].size + 4

    repeat(height) { largeArea.add(mutableListOf<Char>().apply { repeat(width) { add('.') } }) }

    knownFields.forEach { field ->
      field.crops.forEach { c -> largeArea[c.y + 2][c.x + 2] = c.type }
      field.crops.forEach { c ->
        if (c.onPerimeter) {
          val n = (c.y + 2) - 1 to (c.x + 2)
          val s = (c.y + 2) + 1 to (c.x + 2)
          val e = (c.y + 2)     to (c.x + 2) + 1
          val w = (c.y + 2)     to (c.x + 2) - 1

          // if the given neighbor matches, put an "edge" on the marker
          listOf(n, s, e, w).forEach { (y, x) -> if (largeArea[y][x] != c.type) { largeArea[y][x] = '+' } }
        }
      }

      // sweep for the sides going left to right
      field.doSweep(
        firstLoopSize = largeArea.size,
        secondLoopSize = largeArea[0].size,
        item = { y: Int, x: Int -> largeArea[y][x] },
        n1 = { y: Int, x: Int -> largeArea[y - 1][x] },
        n2 = { y: Int, x: Int -> largeArea[y + 1][x] },
      )

      // sweep for the sides going top to bottom
      field.doSweep(
        firstLoopSize = largeArea[0].size,
        secondLoopSize = largeArea.size,
        item = { x: Int, y: Int -> largeArea[y][x] },
        n1 = { x: Int, y: Int -> largeArea[y][x - 1] },
        n2 = { x: Int, y: Int -> largeArea[y][x + 1] },
      )

      // reset the map
      repeat(largeArea.size) { y -> repeat(largeArea[y].size) { x -> largeArea[y][x] = '.' } }
    }

    knownFields.sumOf { field -> field.sides * field.crops.size}
  }

  private fun createFields(input: Sequence<String>): Pair<MutableList<MutableList<Crop>>, MutableList<Field>> {
    val area = mutableListOf<MutableList<Crop>>()

    val unKnownCrops = mutableSetOf<Crop>()
    val knownFields = mutableListOf<Field>()

    input.forEachIndexed { yIndex, line ->
      val row = mutableListOf<Crop>()
      line.forEachIndexed { xIndex, char ->
        val crop = Crop(char, yIndex, xIndex)
        row.add(crop)
        unKnownCrops.add(crop)
      }
      area.add(row)
    }

    while (unKnownCrops.isNotEmpty()) {
      val nextAreaNodes: Queue<Crop> = LinkedList()
      nextAreaNodes.add(unKnownCrops.first())

      val field = Field()
      loop@ while(nextAreaNodes.isNotEmpty()) {
        val c = nextAreaNodes.remove()
        if (field.crops.contains(c)) { continue@loop }

        unKnownCrops.remove(c)
        field.crops.add(c)

        val n = area.get(c.y - 1, c.x)
        val s = area.get(c.y + 1, c.x)
        val e = area.get(c.y, c.x + 1)
        val w = area.get(c.y, c.x - 1)

        listOf(n, s, e, w).forEach { neighbor: Crop? ->
          if (neighbor == null) {
            field.perimeter++
            c.onPerimeter = true
          } else {
            if (neighbor.type == c.type) {
              nextAreaNodes.add(neighbor)
            } else {
              field.perimeter++
              c.onPerimeter = true
            }
          }
        }
      }

      knownFields.add(field)
    }

    return area to knownFields
  }

  private fun Field.doSweep(
    firstLoopSize: Int,
    secondLoopSize: Int,
    item: (Int, Int) -> Char,
    n1: (Int, Int) -> Char,
    n2: (Int, Int) -> Char,
  ) {
    val fieldType = crops.first().type

    // loop starting in one direction
    repeat(firstLoopSize) { f ->
      var n1SideFound = false
      var n2SideFound = false
      // for that direction, iterate through the other
      repeat(secondLoopSize) { s ->
        // if we found an "edge"
        if (item(f, s) == '+') {
          // check the first neighbor (ignoring the direction of travel)
          val n1Char = n1(f, s)
          if (n1Char == fieldType) {
            // if the neighbor matches our type, this is a side, track it if we weren't
            if (!n1SideFound) {
              n1SideFound = true
            }
          } else if (n1SideFound) {
            // if the neighbor wasn't our type; but we were tracking, we found the full side
            n1SideFound = false
            sides++
          }
          // check the second neighbor (ignoring the direction of travel)
          val n2Char = n2(f, s)
          if (n2Char == fieldType) {
            // if the neighbor matches our type, this is a side, track it if we weren't
            if (!n2SideFound) {
              n2SideFound = true
            }
          } else if (n2SideFound) {
            // if the neighbor wasn't our type; but we were tracking, we found the full side
            n2SideFound = false
            sides++
          }
        } else {
          if (n1SideFound) {
            // if the item wasn't a wall, and we were tracking, we found the full side
            n1SideFound = false
            sides++
          }
          if (n2SideFound) {
            // if the item wasn't a wall, and we were tracking, we found the full side
            n2SideFound = false
            sides++
          }
        }
      }
    }
  }
}

private fun MutableList<MutableList<Crop>>.get(y: Int, x: Int): Crop? {
  return if (y in indices && x in this[y].indices) { this[y][x] } else { null }
}

data class Crop(val type: Char, val y: Int, val x: Int) {
  var onPerimeter = false
}

data class Field(val crops: MutableSet<Crop> = mutableSetOf(), var perimeter: Long = 0L) {
  var sides = 0L
}
