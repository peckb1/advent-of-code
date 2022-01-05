package me.peckb.aoc._2016.calendar.day04

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day04 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::room) { input ->
    input.filter { it.isValid }.sumOf { it.sectorId }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::room) { input ->
    input.filter { it.isValid }.firstOrNull { it.decrypt() == NORTHPOLE_ROOM_NAME }?.sectorId
  }

  private fun room(line: String): Room {
    val encryptedData = line.substringBeforeLast("-").split("-")
    val sectorId = line.substringAfterLast("-").substringBefore("[").toInt()
    val checksum = line.substringAfter("[").dropLast(1)

    val unsortedCharData = mutableMapOf<Char, Int>()
    encryptedData.forEach { word ->
      word.forEach { c ->
        unsortedCharData.merge(c, 1, Int::plus)
      }
    }
    val sortedCounters = unsortedCharData.map { Counter(it.key, it.value) }.sortedDescending()

    return Room(encryptedData, sortedCounters, sectorId, checksum)
  }

  data class Room(val encryptedData: List<String>, val sortedCounters: List<Counter>, val sectorId: Int, val checksum: String) {
    fun decrypt(): String {
      return encryptedData.joinToString(" ") { word ->
        word.map { c ->
          ((c.code - ASCII_TABLE_SHIFT + sectorId) % ALPHABET_LENGTH + ASCII_TABLE_SHIFT).toChar()
        }.joinToString("")
      }
    }

    val isValid = sortedCounters.take(5).map { it.c }.joinToString("") == checksum
  }

  data class Counter(val c: Char, val count: Int) : Comparable<Counter> {
    override fun compareTo(other: Counter) =
      when (val countComparison = this.count.compareTo(other.count)) {
        0 -> -this.c.compareTo(other.c) // invert the comparison, as we want descending order
        else -> countComparison
      }
  }

  companion object {
    const val ASCII_TABLE_SHIFT = 97
    const val ALPHABET_LENGTH = 26
    const val NORTHPOLE_ROOM_NAME = "northpole object storage"
  }
}
