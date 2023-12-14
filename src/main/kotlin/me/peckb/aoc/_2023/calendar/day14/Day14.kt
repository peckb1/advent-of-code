package me.peckb.aoc._2023.calendar.day14

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day14 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val mirrorDish = mutableListOf<MutableList<Char>>()
    input.forEach { line -> mirrorDish.add(line.toCharArray().toMutableList()) }

    tiltNorth(mirrorDish)

    calculateTotalLoad(mirrorDish)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val mirrorDish = mutableListOf<MutableList<Char>>()
    input.forEach { line -> mirrorDish.add(line.toCharArray().toMutableList()) }

    val images = linkedMapOf<String, Int>()

    var start = -1
    var end = -1
    var cycleCount = 0

    while(start == -1 && end == -1) {
      cycleCount++
      runCycle(mirrorDish)
      val encoding = encode(mirrorDish)

      if (images.containsKey(encoding)) {
        start = images[encoding]!!
        end = cycleCount
      } else {
        images[encoding] = cycleCount
      }
    }

    val cycleLength = end - start
    val numCycles = (1_000_000_000L - start) / cycleLength
    val indexInCycle = 1_000_000_000L - ((cycleLength * numCycles) + start)
    val imageIndexToFindTheLoadOf = start - 1 + indexInCycle.toInt()

    val mirrorToFindLoadFor = decode(images.keys.toList()[imageIndexToFindTheLoadOf], mirrorDish.size)
    calculateTotalLoad(mirrorToFindLoadFor)
  }

  private fun tiltNorth(mirrorDish: MutableList<MutableList<Char>>) {
    (1 until mirrorDish.size).forEach { rowIndex ->
      mirrorDish[rowIndex].forEachIndexed { colIndex, c ->
        if (c == 'O') {
          val firstBlocker = (rowIndex downTo 0).firstOrNull { mirrorDish[it][colIndex] == '#' }
          val firstEmpty = ((firstBlocker ?: 0) until rowIndex).firstOrNull { mirrorDish[it][colIndex] == '.' }

          firstEmpty?.let {
            mirrorDish[it][colIndex] = 'O'
            mirrorDish[rowIndex][colIndex] = '.'
          }
        }
      }
    }
  }

  private fun calculateTotalLoad(mirrorDish: MutableList<MutableList<Char>>): Long {
    return mirrorDish.reversed().withIndex().sumOf { (index, row) ->
      val multiplier = index + 1L
      row.count { it == 'O' } * multiplier
    }
  }

  private fun runCycle(mirrorDish: MutableList<MutableList<Char>>) {
    repeat(4) {
      tiltNorth(mirrorDish)
      rotateMirror(mirrorDish)
    }
  }

  private fun encode(mirrorDish: MutableList<MutableList<Char>>): String {
    return mirrorDish.joinToString("") { it.joinToString("") }
  }

  private fun decode(it: String, size: Int): MutableList<MutableList<Char>> {
    return it.chunked(size).map { it.toMutableList() }.toMutableList()
  }

  private fun rotateMirror(mirrorDish: MutableList<MutableList<Char>>) {
    val newRows = mutableListOf<MutableList<Char>>()
    (0 until mirrorDish[0].size).forEach { colIndex ->
      val newRow = mutableListOf<Char>()
      mirrorDish.indices.reversed().forEach { rowIndex ->
        newRow.add(mirrorDish[rowIndex][colIndex])
      }
      newRows.add(newRow)
    }
    newRows.forEachIndexed { rowIndex, row ->
      mirrorDish[rowIndex] = row
    }
  }
}
