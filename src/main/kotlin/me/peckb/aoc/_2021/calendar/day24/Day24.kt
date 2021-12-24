package me.peckb.aoc._2021.calendar.day24

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

typealias AddInstruction = Pair<Int, Int>

class Day24 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    const val MINIMUM_ALLOWED = 1
    const val MAXIMUM_ALLOWED = 9
  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    run(MAXIMUM_ALLOWED, input.chunked(18))
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    run(MINIMUM_ALLOWED, input.chunked(18))
  }

  private fun run(idealResult: Int, blocks: Sequence<List<String>>): Long {
    val result = MutableList(14) { 0 }
    val addYStack = ArrayDeque<AddInstruction>()

    blocks.forEachIndexed { mainResultIndex, instructions ->
      // note: the input has an equal number of `div z 26` and `div z 1`
      // the `div z 26` are the inputs that actually determine the values in the result
      // the `div z 1` values are purely derivative from the most previous, but not used
      // `add y [value]` instruction
      if (instructions[4] == "div z 26") {
        @Suppress("UnnecessaryVariable")
        val addXIndex = mainResultIndex
        val addXValue = instructions[5].substringAfterLast(" ").toInt()
        val (addYIndex, addYValue) = addYStack.removeFirst()
        // janky bit of "reading the input here"
        // whenever `div z 26` shows up, the `value` inside `add x [value]` is always negative
        // so we get the difference between the Y we want to add, and the X we want to subtract
        // NOTE: the y `value` is actually the value from the previous instruction
        val diff = addYValue + addXValue

        // part2 == true  -> min
        // part2 == false -> max
        //val best = if (part2) 1 else 9
        if (diff >= 0) {
          // if the difference is a positive number:
          //   the `min value` for our x index            would be `1 + diff`
          //   the `min value` for our derivative y index would be `1`
          //   the `max value` for our x index            would be `9`
          //   the `max value` for our derivative y index would be `9 - diff`
          // we can simplify those commands by
          //   always adding      for `addXIndex` (stopping at the max allowed)
          //   always subtracting for `addYIndex` (stopping at the min allowed)
          result[addXIndex] = min(idealResult + diff, MAXIMUM_ALLOWED)
          result[addYIndex] = max(idealResult - diff, MINIMUM_ALLOWED)
        } else {
          // if the difference is a negative number, we need to invert the positive difference logic:
          //   the `min value` for our x index            would be `1`
          //   the `min value` for our derivative y index would be `1 - diff` (diff is < 0, so we're adding)
          //   the `max value` for our x index            would be `9 + diff` (diff is < 0, so we're subtracting)
          //   the `max value` for our derivative y index would be `9`
          // we can simplify those commands by
          //   always "adding"      for `addXIndex` (stopping at the min allowed, since we're a negative)
          //   always "subtracting" for `addYIndex` (stopping at the max allowed, since we're a negative)
          result[addXIndex] = max(idealResult + diff, MINIMUM_ALLOWED)
          result[addYIndex] = min(idealResult - diff, MAXIMUM_ALLOWED)
        }
      } else {
        // build up a stack of all of the "add y [value]" in a pair of the
        // index (same as our output index) that we found the instruction,
        // and the [value] to add it to
        addYStack.addFirst(mainResultIndex to instructions[15].substringAfterLast(" ").toInt())
      }
    }

    return result.joinToString("").toLong()
  }
}
