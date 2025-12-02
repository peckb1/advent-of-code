package me.peckb.aoc._2025.calendar.day02

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day02 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { idRangeList ->
    idRangeList.countInvalidIds(::isIdInvalid)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { idRangeList ->
    idRangeList.countInvalidIds(::isIdActuallyInvalid)
  }

  fun String.countInvalidIds(validation: (Long) -> Boolean): Long {
    return split(",")
      .map { it.split("-").let { (s, e) -> IdRange(s.toLong(), e.toLong()) } }
      .sumOf { idRange -> (idRange.start..idRange.end).filter { id -> validation(id) }.sum() }
  }

  private fun isIdInvalid(id: Long): Boolean {
    val idString = id.toString()

    // odd numbers cannot be the same pattern repeated
    if (idString.length %2 != 0) {
      return false
    }

    // so we have an even length ID
    val halfLength = idString.length / 2
    return idString.take(halfLength) == idString.drop(halfLength)
  }

  private fun isIdActuallyInvalid(id: Long): Boolean {
    val idString = id.toString()
    val fullLength = idString.length
    val halfLength = idString.length / 2

    return (1 .. halfLength).any { lengthToRepeat ->
      // if we can't form a full pattern of string length no sense trying
      if (fullLength % lengthToRepeat != 0) { return@any false }

      val data = idString.take(lengthToRepeat)
      var repeatedCount = 1
      while(repeatedCount * lengthToRepeat <= fullLength) {
        if (data.repeat(repeatedCount) == idString) { return@any true }
        repeatedCount++
      }
      return@any false
    }
  }
}

data class IdRange(val start: Long, val end: Long)
