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

      // left to right sweep first
      val fieldType = field.crops.first().type
      repeat(largeArea.size) { y ->
        var topEdge = false
        var bottomEdge = false
        repeat(largeArea[y].size) { x ->
          val item = largeArea[y][x]
          if (item == '+') {
            val top =  largeArea[y-1][x]
            val bottom = largeArea[y+1][x]
            if (top == fieldType) {
              if (!topEdge) {
                topEdge = true
              }
            } else {
              if (topEdge) {
                topEdge = false; field.sides++
              }
            }

            if (bottom == fieldType) {
              if (!bottomEdge) {
                bottomEdge = true
              }
            } else {
              if (bottomEdge) {
                bottomEdge = false; field.sides++
              }
            }
          } else {
            if (topEdge)    { topEdge = false;    field.sides ++ }
            if (bottomEdge) { bottomEdge = false; field.sides ++ }
          }
        }
      }
      // top to bottom sweep next
      repeat(largeArea[0].size) { x ->
        var leftEdgeFound = false
        var rightEdgeFound = false
        repeat(largeArea.size) { y ->
          val item = largeArea[y][x]
          if (item == '+') {
            val left =  largeArea[y][x - 1]
            val right = largeArea[y][x + 1]
            if (left == fieldType) {
              if (!leftEdgeFound) {
                leftEdgeFound = true
              }
            } else {
              if (leftEdgeFound) {
                leftEdgeFound = false
                field.sides ++
              }
            }
            if (right == fieldType) {
              if (!rightEdgeFound) {
                rightEdgeFound = true
              }
            } else {
              if (rightEdgeFound) {
                rightEdgeFound = false
                field.sides ++
              }
            }

          } else {
            if (rightEdgeFound) {
              rightEdgeFound = false
              field.sides ++
            }
            if (leftEdgeFound) {
              leftEdgeFound = false
              field.sides ++
            }
          }
        }
      }

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
      val nextAreaNodes: Queue<Crop> = LinkedList<Crop>()
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
