package me.peckb.aoc._2016.calendar.day08

import me.peckb.aoc._2016.calendar.day08.Day08.Operation.Rect
import me.peckb.aoc._2016.calendar.day08.Day08.Operation.RotateColumn
import me.peckb.aoc._2016.calendar.day08.Day08.Operation.RotateRow
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day08 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::operation) { input ->
    val screen = Array(6) { Array(50) { OFF } }
    screen.applyOperations(input)
    screen.sumOf { row -> row.count { it == ON } }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::operation) { input ->
    val screen = Array(6) { Array(50) { OFF } }
    screen.applyOperations(input)
    screen.joinToString("\n") { it.joinToString("") }
  }

  private fun operation(line: String): Operation {
    val parts = line.split(" ")
    when (parts[0]) {
      "rect" -> return parts[1].split("x").map { it.toInt() }.let { Rect(it.first(), it.last()) }
      "rotate" -> {
        val index = parts[2].substringAfter("=").toInt()
        val count = parts[4].toInt()
        when (parts[1]) {
          "column" -> return RotateColumn(index, count)
          "row" -> return RotateRow(index, count)
        }
      }
    }
    throw IllegalArgumentException("Unknown Operation: $line")
  }

  sealed class Operation {
    data class Rect(val width: Int, val height: Int) : Operation()
    data class RotateColumn(val index: Int, val count: Int) : Operation()
    data class RotateRow(val index: Int, val count: Int) : Operation()
  }

  private fun Array<Array<Char>>.applyOperations(input: Sequence<Operation>) {
    input.forEach { operation ->
      when (operation) {
        is Rect -> {
          repeat(operation.height) { y ->
            repeat(operation.width) { x ->
              this[y][x] = ON
            }
          }
        }
        is RotateColumn -> {
          val newColumn = Array(this.size) { OFF }
          repeat(this.size) { y ->
            val itemToRotate = this[y][operation.index]
            val newPos = (y + operation.count) % this.size
            newColumn[newPos] = itemToRotate
          }
          newColumn.indices.forEach { y ->
            this[y][operation.index] = newColumn[y]
          }
        }
        is RotateRow -> {
          val newRow = Array(this[0].size) { OFF }
          repeat(this[0].size) { x ->
            val itemToRotate = this[operation.index][x]
            val newPos = (x + operation.count) % this[0].size
            newRow[newPos] = itemToRotate
          }
          newRow.indices.forEach { x ->
            this[operation.index][x] = newRow[x]
          }
        }
      }
    }
  }
  
  companion object {
    const val ON = '#'
    const val OFF = ' '
  }
}

