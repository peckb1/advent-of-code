package me.peckb.aoc._2024.calendar.day09

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day09 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { data ->
    val memory = fillMemory(data)
    val lastFileIndex = (memory.lastIndex downTo 0).first { memory[it] is Space.Full }

    defrag(memory, lastFileIndex, splitFiles = true)

    checksum(memory)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { data ->
    val memory = fillMemory(data)
    val lastFileIndex = (memory.lastIndex downTo 0).first { memory[it] is Space.Full }

    defrag(memory, lastFileIndex, splitFiles = false)

    checksum(memory)
  }

  private fun fillMemory(line: String): MutableList<Space> {
    val memory = mutableListOf<Space>()

    var file = true
    var id = 0

    line.forEach { char ->
      val length = char.digitToInt()
      if (file) {
        memory.add(Space.Full(length, File(id++)))
        file = false
      } else {
        memory.add(Space.Empty(length))
        file = true
      }
    }

    return memory
  }

  private fun defrag(memory: MutableList<Space>, fileIndex: Int, splitFiles: Boolean) {
    var lastFileIndex = fileIndex
    var spaceIndex = 1

    while(spaceIndex < memory.size && (spaceIndex < lastFileIndex)) {
      val lastFile = (lastFileIndex downTo spaceIndex)
        .first { i -> memory[i] is Space.Full }
        .also { lastFileIndex = it }
        .let { memory[it] as Space.Full }

      val emptySpace = (spaceIndex until lastFileIndex)
        .first { i -> memory[i] is Space.Empty }
        .also { spaceIndex = it }
        .let { memory[it] as Space.Empty }

      val availableSpace = emptySpace.length
      val fileSize = lastFile.length

      if (fileSize < availableSpace) {
        emptySpace.length = (availableSpace - fileSize)
        memory.add(spaceIndex, Space.Full(fileSize, lastFile.file))
        spaceIndex++
        memory[lastFileIndex + 1] = Space.Empty(fileSize)

      } else if (fileSize == availableSpace) {
        emptySpace.length = 0
        memory.add(spaceIndex, Space.Full(availableSpace, lastFile.file))
        spaceIndex++
        memory[lastFileIndex + 1] = Space.Empty(fileSize)

      } else { // fileSize > availableSpace
        if (splitFiles) {
          emptySpace.length = 0
          memory.add(spaceIndex, Space.Full(availableSpace, lastFile.file))

          lastFile.length -= availableSpace
          lastFileIndex++

          spaceIndex++
          spaceIndex += 2
        } else {
          var tempSpaceIndex = spaceIndex + 1
          var nextEmptySpace = (spaceIndex until lastFileIndex).firstOrNull { i ->
            memory[i] is Space.Empty
          }?.let { tempSpaceIndex = it; memory[it] as Space.Empty }

          while (nextEmptySpace != null && tempSpaceIndex < lastFileIndex) {
            if (fileSize <= nextEmptySpace.length) {
              if (fileSize < nextEmptySpace.length) {
                nextEmptySpace.length = (nextEmptySpace.length - fileSize)
                memory.add(tempSpaceIndex, Space.Full(fileSize, lastFile.file))
                memory[++lastFileIndex] = Space.Empty(fileSize)

              } else { // amountNeededToTake = nextEmptySpace.length
                nextEmptySpace.length = 0
                memory.add(tempSpaceIndex, Space.Full(fileSize, lastFile.file))
                memory[++lastFileIndex] = Space.Empty(fileSize)
              }

              nextEmptySpace = null
            } else {
              nextEmptySpace = (tempSpaceIndex + 1 until lastFileIndex).firstOrNull { i ->
                memory[i] is Space.Empty
              }?.let { tempSpaceIndex = it; memory[it] as Space.Empty }
            }
          }

          lastFileIndex--
        }
      }
    }
  }

  private fun checksum(memory: MutableList<Space>): Long {
    var index = 0L
    return memory.asSequence()
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