package me.peckb.aoc._2024.calendar.day09

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day09 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { line ->
    val memory = mutableListOf<Space>()

    var lastFileIndex = 0
    var file = true
    var id = 0
    line.forEach { char ->
      val length = char.digitToInt()
      if (file) {
        memory.add(Space.Full(length, File(id)))

        lastFileIndex = memory.size - 1
        id++
        file = false
      } else {
        memory.add(Space.Empty(length))

        file = true
      }
    }

    var spaceIndex = 1
    while(spaceIndex < memory.size && (spaceIndex < lastFileIndex)) {
      val lastFile = memory[lastFileIndex] as Space.Full
      val emptySpace = memory[spaceIndex] as Space.Empty

      val amountWeCanTake = emptySpace.length
      val amountNeededToTake = lastFile.length

      if (amountNeededToTake < amountWeCanTake) {
        emptySpace.length = (amountWeCanTake - amountNeededToTake)
        memory.add(spaceIndex, Space.Full(amountNeededToTake, lastFile.file))
        spaceIndex++
        memory[++lastFileIndex] = Space.Empty(amountNeededToTake)
        lastFileIndex -= 2

        -1
      } else if (amountNeededToTake == amountWeCanTake) {
        emptySpace.length = 0
        memory.add(spaceIndex, Space.Full(amountWeCanTake, lastFile.file))
        spaceIndex++
        memory[++lastFileIndex] = Space.Empty(amountNeededToTake)
        lastFileIndex -= 2

        -1
      } else { // amountNeededToTake > amountWeCanTake
        emptySpace.length = 0
        memory.add(spaceIndex, Space.Full(amountWeCanTake, lastFile.file))

        lastFile.length -= amountWeCanTake
        spaceIndex++
        spaceIndex += 2
        lastFileIndex += 1

        -1
      }
    }

    // 00...111...2...333.44.5555.6666.777.888899

    // 0099811188827773336446555566

    var index = 0L
    memory.asSequence()
      .filterIsInstance<Space.Full>()
      .filter { it.length > 0 }
      .sumOf { (length, file) ->
        (1..length).sumOf {
          (index * file.id).also { index ++ }
        }
      }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { line ->
    val memory = mutableListOf<Space>()

    var lastFileIndex = 0
    var file = true
    var id = 0
    line.forEach { char ->
      val length = char.digitToInt()
      if (file) {
        memory.add(Space.Full(length, File(id)))

        lastFileIndex = memory.size - 1
        id++
        file = false
      } else {
        memory.add(Space.Empty(length))

        file = true
      }
    }

    var spaceIndex = 1
    while(spaceIndex < memory.size && (spaceIndex < lastFileIndex)) {
      val lastFile = (lastFileIndex downTo spaceIndex).first { i ->
        memory[i] is Space.Full
      }.let { lastFileIndex = it; memory[it] as Space.Full }
      val emptySpace = (spaceIndex until lastFileIndex).first { i ->
        memory[i] is Space.Empty
      }.let { spaceIndex = it; memory[it] as Space.Empty }

//      val lastFile = memory[lastFileIndex] as Space.Full
//      val emptySpace = memory[spaceIndex] as Space.Empty
//
      val amountWeCanTake = emptySpace.length
      val amountNeededToTake = lastFile.length

      if (amountNeededToTake < amountWeCanTake) {
        emptySpace.length = (amountWeCanTake - amountNeededToTake)
        memory.add(spaceIndex, Space.Full(amountNeededToTake, lastFile.file))
        spaceIndex++
        memory[lastFileIndex + 1] = Space.Empty(amountNeededToTake)

      } else if (amountNeededToTake == amountWeCanTake) {
        emptySpace.length = 0
        memory.add(spaceIndex, Space.Full(amountWeCanTake, lastFile.file))
        spaceIndex++
        memory[lastFileIndex + 1] = Space.Empty(amountNeededToTake)

      } else { // amountNeededToTake > amountWeCanTake
        var tempSpaceIndex = spaceIndex
        var nextEmptySpace = (spaceIndex until lastFileIndex).firstOrNull { i ->
          memory[i] is Space.Empty
        }?.let { tempSpaceIndex = it; memory[it] as Space.Empty }

        while (nextEmptySpace != null && tempSpaceIndex < lastFileIndex) {
          if (amountNeededToTake <= nextEmptySpace.length) {
            if (amountNeededToTake < nextEmptySpace.length) {
              nextEmptySpace.length = (nextEmptySpace.length - amountNeededToTake)
              memory.add(tempSpaceIndex, Space.Full(amountNeededToTake, lastFile.file))
              memory[++lastFileIndex] = Space.Empty(amountNeededToTake)

            } else { // amountNeededToTake = nextEmptySpace.length
              nextEmptySpace.length = 0
              memory.add(tempSpaceIndex, Space.Full(amountNeededToTake, lastFile.file))
              memory[++lastFileIndex] = Space.Empty(amountNeededToTake)
            }

            nextEmptySpace = null
          } else {
            nextEmptySpace = (tempSpaceIndex + 1 until lastFileIndex).firstOrNull { i ->
              memory[i] is Space.Empty
            }?.let { tempSpaceIndex = it; memory[it] as Space.Empty }
          }
        }

        lastFileIndex--


//        emptySpace.length = 0
//        memory.add(spaceIndex, Space.Full(amountWeCanTake, lastFile.file))
//
//        lastFile.length -= amountWeCanTake
//        spaceIndex++
//        spaceIndex += 2
//        lastFileIndex += 1

        -1
      }
    }

    var index = 0L
    memory.asSequence()
//      .filterIsInstance<Space.Full>()
      .filter { space ->
        when (space) {
          is Space.Empty -> space.length > 0
          is Space.Full -> space.length > 0
        }
      }
      .sumOf { space ->
        when (space) {
          is Space.Empty -> { index += space.length; 0 }
          is Space.Full -> (1..space.length).sumOf { (index * space.file.id).also { index ++ } }
        }
      }
  }
}

data class File(val id: Int)

sealed class Space {
  data class Empty(var length: Int) : Space()
  data class Full(var length: Int, val file: File) : Space()
}