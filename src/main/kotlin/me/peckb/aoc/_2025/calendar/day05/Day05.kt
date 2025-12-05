package me.peckb.aoc._2025.calendar.day05

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max
import kotlin.math.min

class Day05 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (ranges, inventory) = parseInput(input)

    inventory.count { ranges.any { r -> r.contains(it) } }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (ranges, _) = parseInput(input)

    do {
      val overlap = ranges.firstNotNullOfOrNull { r1 ->
        ranges.firstOrNull { it != r1 && it.overlaps(r1) }?.let { it to r1 }
      }

      overlap?.let { (r1, r2) ->
        ranges.remove(r1)
        ranges.remove(r2)
        ranges.add(min(r1.first, r2.first)..max(r1.last, r2.last))
      }
    } while (overlap != null)

    ranges.distinct().sumOf { it.last - it.first + 1 }
  }

  private fun parseInput(input: Sequence<String>): Pair<MutableList<LongRange>, MutableList<Long>> {
    val ranges = mutableListOf<LongRange>()
    val inventory = mutableListOf<Long>()
    var findingRanges = true
    input.forEach { line ->
      if (line.isEmpty()) {
        findingRanges = false
      } else {
        if (findingRanges) {
          line.split("-").let { (s, e) -> ranges.add(s.toLong()..e.toLong()) }
        } else {
          inventory.add(line.toLong())
        }
      }
    }

    return ranges to inventory
  }

  infix fun LongRange.overlaps(other: LongRange): Boolean {
    return this.first <= other.last && other.first <= this.last
  }
}
