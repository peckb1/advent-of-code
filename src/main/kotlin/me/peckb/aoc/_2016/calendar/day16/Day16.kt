package me.peckb.aoc._2016.calendar.day16

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day16 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    findChecksum(input, PART_ONE_DISK_SIZE)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    findChecksum(input, PART_TWO_DISK_SIZE)
  }

  private fun findChecksum(input: String, diskSize: Int): String {
    val data = StringBuilder(input)
    while (data.length < diskSize) {
      data.append('0')
      (data.length - 2 downTo 0).forEach { index ->
        data.append(if (data[index] == '1') '0' else '1')
      }
    }
    var length = diskSize
    while (length % 2 == 0) {
      (0 until length step 2).forEach { index ->
        data[index / 2] = if (data[index] == data[index + 1]) '1' else '0'
      }
      length /= 2
    }
    return data.substring(0 until length)
  }

  companion object {
    const val PART_ONE_DISK_SIZE = 272
    const val PART_TWO_DISK_SIZE = 35651584
  }
}
