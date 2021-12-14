package me.peckb.aoc._2021.calendar.day14

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day14 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {

  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::day14) { input ->
    val data = input.toList()

    val initialSequence = ArrayList<Char>().apply {
      data.first().forEach { add(it) }
    }

    val instructions = data.drop(2).map {
      val (pattern, insertion) = it.split(" -> ")
      val (before, after) = pattern.toCharArray()
      Instruction(before, after, insertion.first())
    }.groupBy {
      it.beforeChar
    }.mapValues { (_, v) ->
      v.groupBy {
        it.afterChar
      }
    }

    repeat(10) {
      val actions = mutableListOf<Pair<Int, Char>>()
      actions.clear()
      (0 until initialSequence.size - 1).forEach { index ->
        val b = initialSequence[index]
        val a = initialSequence[index + 1]

        instructions.get(b)?.get(a)?.first()?.also {
          actions.add(index + 1 to it.insertion)
        }
      }
      actions.forEachIndexed { i, t ->
        val (index, a) = t
        initialSequence.add(index + i, a)
      }
    }

    val xx = initialSequence.groupingBy {
      it
    }.eachCount()

    val sorted = xx.entries.sortedByDescending { it.value }

    sorted.first().value - sorted.last().value
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day14) { input ->
    val data = input.toList()

    val initialSequence = data.first().windowed(2).map {
      val (before, after) = it.toCharArray()
      (before to after)
    }.groupBy {
      it.first
    }.mapValues { (_, v) ->
      v.groupBy { it.second }.mapValues {
        it.value.size.toLong()
      }.toMutableMap()
    }.toMutableMap()

    val instructions = data.drop(2)
      .map {
        val (pattern, insertion) = it.split(" -> ")
        val (before, after) = pattern.toCharArray()
        Instruction(before, after, insertion.first())
      }

    val counts = mutableMapOf<Char, Long>().apply {
      data.first().forEach {
        this.compute(it) { _, v ->
          (v ?: 0) + 1
        }
      }
    }


    repeat(40) {
      val addActions = mutableListOf<Triple<Char, Char, Long>>()
      val removeActions = mutableListOf<Triple<Char, Char, Long>>()
      instructions.forEach { instruction ->
        val (b, a, insert) = instruction
        val count = initialSequence.get(b)?.get(a)
        count?.also { c ->
          // if (c > 0) {
            addActions.add(Triple(b, insert, c))
            addActions.add(Triple(insert, a, c))
            removeActions.add(Triple(b, a, c))
            counts.compute(insert) { _, v ->
              (v ?: 0) + c
            }
          // }
        }
      }
      addActions.forEach { (b, a, c) ->
        initialSequence.compute(b) { _, v1 ->
          (v1 ?: mutableMapOf()).also { map ->
            map.compute(a) { _, count  ->
              (count ?: 0) + c
            }
          }
        }
      }
      removeActions.forEach { (b, a, c) ->
        initialSequence.compute(b) { _, v1 ->
          (v1 ?: mutableMapOf()).also { map ->
            map.compute(a) { _, count  ->
              (count ?: 0) - c
            }
          }
        }
      }
      addActions.clear()
      removeActions.clear()
    }

    val sorted = counts.entries.sortedByDescending { it.value }

    sorted.first().value - sorted.last().value
  }

  fun day14(line: String) = line

  data class Instruction(val beforeChar: Char, val afterChar: Char, val insertion: Char)
}
