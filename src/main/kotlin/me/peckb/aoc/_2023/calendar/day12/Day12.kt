package me.peckb.aoc._2023.calendar.day12

import arrow.core.flatten
import arrow.core.replicate
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import org.apache.commons.lang3.tuple.Triple

class Day12 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::spring) { input ->
    input.sumOf { spring -> count(spring.conditions, spring.groupings) }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::spring) { input ->
    input.sumOf { spring ->
      val bigConditions = "${spring.conditions}?".repeat(5).dropLast(1)
      val bigGroupings = spring.groupings.replicate(5).flatten()
      count(bigConditions, bigGroupings)
    }
  }

  private fun count(
    conditions: String,
    groupings: List<Int>,
    cache: MutableMap<Triple<Int, Int, Int>, Long> = mutableMapOf(),
    index: Int = 0,
    blocksFound: Int = 0,
    currentBlockSize: Int = 0
  ): Long {
    val cacheKey = Triple.of(index, blocksFound, currentBlockSize)

    // if we already have seen this pattern - don't bother doing the work again
    // without this cache - the larger items take a while to figure out
    cache[cacheKey]?.let { return it }

    // if we're at the end of the conditions string, check to see if we found something!
    if (index == conditions.length) {
      val foundNoExtraDamagedInValidPattern = blocksFound == groupings.size && currentBlockSize == 0
      val finishedAtTheEndOfThePattern = blocksFound == groupings.size - 1 && groupings[blocksFound] == currentBlockSize

      return if (foundNoExtraDamagedInValidPattern || finishedAtTheEndOfThePattern) 1 else 0
    }

    var total: Long = 0
    val c = conditions[index]

    // first check to see if we have an empty, or something that could be used an empty
    // to complete a block we're in the process of building
    // technically we can do either the `.` or `#` sections in any order; as long as we do both
    if ((c == '.' || c == '?')) {
      val noBlockBeingBuiltYet = currentBlockSize == 0
      val haveNotFoundAllBlocksYet = blocksFound < groupings.size
      // keep the index check as a lazy check to avoid going overboard when we've found a lot more blocks than we need to
      val currentBuildingBlockMatchesPattern by lazy { groupings[blocksFound] == currentBlockSize }

      if (noBlockBeingBuiltYet) {
        // if we haven't starting building a block yet - don't treat the '?' as a block
        // and just keep moving our index
        total += count(conditions, groupings, cache, index + 1, blocksFound, 0)
      } else if (haveNotFoundAllBlocksYet && currentBuildingBlockMatchesPattern) {
        // if we have not found all the blocks yet, and the current building block matches the next pattern
        // we're on the hunt for, mark that we found a block and keep moving our index
        total += count(conditions, groupings, cache, index + 1, blocksFound + 1, 0)
      }
    }

    // Once we are done finishing recursing from blank spaces, or completed blocks we can actually build up the blocks!
    // So if we find a damage section, or a wildcard we can add to our damaged section
    // technically we can do either the `.` or `#` sections in any order; as long as we do both
    if (c == '#' || c == '?') {
      total += count(conditions, groupings, cache, index + 1, blocksFound, currentBlockSize + 1)
    }

    // And once we're done recursing add the following index+blocksFound+currentBlockSize to the cache
    // allowing us to know if we hit it again, we can not bother going down the rabbit hole
    cache[cacheKey] = total

    // and subsequently return what we found
    return total
  }

  private fun spring(line: String): Spring {
    val (conditionData, groupingData) = line.split(" ")
    val groupings = groupingData.split(",").map { it.toInt() }

    return Spring(conditionData, groupings)
  }

  data class Spring(val conditions: String, val groupings: List<Int>)
}
