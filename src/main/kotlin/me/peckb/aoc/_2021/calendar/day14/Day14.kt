package me.peckb.aoc._2021.calendar.day14

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

typealias Action<T> = Pair<String, T>

class Day14 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    countHighMinusLow(findCounts(input.toList(), 10))
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    countHighMinusLow(findCounts(input.toList(), 40))
  }

  private fun findCounts(data: List<String>, iterations: Int): Map<Char, Long> {
    val characterPairs: MutableMap<String, Long> = createInitialPairCounts(data.first())
    val instructions: List<Instruction> = createInstructions(data.drop(2))
    val counts: MutableMap<Char, Long> = createInitialCounts(data.first())

    val addActions = mutableListOf<Action<Long>>()
    val removeActions = mutableListOf<Action<Long>>()

    repeat(iterations) {
      instructions.forEach { instruction ->
        // i.e. `AB` -> `C`
        val (pattern, insert) = instruction

        characterPairs[pattern]?.also { c ->
          // for a given pattern count `AB` -> x it means we have x `AB` pairs
          // so we need to add x `AC` pair counts
          addActions.add("${pattern.first()}$insert" to c)
          // we also need to add x `CB` pair counts
          addActions.add("$insert${pattern.last()}" to c)
          // but, we just split those pairs, so we need to remove x `AB` pairs
          removeActions.add(pattern to c)
          // also keep track of any new letters we just added
          counts.compute(insert.first()) { _, existingCount -> (existingCount ?: 0) + c }
        }
      }

      addActions.mutatePairs(characterPairs, ::add).also { addActions.clear() }
      removeActions.mutatePairs(characterPairs, ::minus).also { removeActions.clear() }
    }

    return counts
  }

  /**
   * { PP=2, PF=2, FC=1, CH=1, HP=1, FN=1, NC=1, CK=1, KO=2, OK=1, OS=1, SB=1, BV=1, VC=1, CF=1, FP=1 }
   */
  private fun createInitialPairCounts(data: String) = data
    .windowed(2)
    .groupBy { it }
    .mapValues { (_, pairing) -> pairing.size.toLong() }
    .toMutableMap()

  /**
   * [
   *   VC->N, SC->H, ..., CH->K, NH->P
   * ]
   */
  private fun createInstructions(instructions: List<String>) = instructions
    .map {
      val (pattern, insertion) = it.split(" -> ")
      Instruction(pattern, insertion)
    }

  /**
   * { P=5, F=3, C=3, H=1, N=1, K=2, O=2, S=1, B=1, V=1 }
   */
  private fun createInitialCounts(data: String) = mutableMapOf<Char, Long>().apply {
    data.forEach { compute(it) { _, v -> add(v, 1) } }
  }

  private fun <T> List<Action<T>>.mutatePairs(pairs: MutableMap<String, T>, maths: (T?, T) -> T) =
    forEach { (pattern, count) ->
      pairs.compute(pattern) { _, existingCount -> maths(existingCount, count) }
    }

  private fun countHighMinusLow(counts: Map<Char, Long>): Long {
    val sortedPairs = counts.entries.sortedByDescending { it.value }
    return sortedPairs.first().value - sortedPairs.last().value
  }

  data class Instruction(val pattern: String, val insertion: String)

  private fun add(n: Long?, m: Long) = (n ?: 0) + m

  private fun minus(n: Long?, m: Long) = (n ?: 0) - m
}
