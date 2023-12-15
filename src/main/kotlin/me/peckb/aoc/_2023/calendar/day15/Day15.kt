package me.peckb.aoc._2023.calendar.day15

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day15 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { line ->
    line.split(",").map(::hash).sum()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { line ->
    val boxes = mutableMapOf<Int, MutableMap<String, Int>>().apply {
      // do we need all these boxes?
      // no, but the space is trivial, and it makes the lower part a little easier to read
      (0..256).forEach { this[it] = linkedMapOf() }
    }
    line.split(",").forEach { word ->
      val addIndex = word.indexOf("=")
      val removeIndex = word.indexOf("-")

      when {
        addIndex != -1 -> {
          val label = word.substring(0, addIndex)
          boxes.getValue(hash(label))
            .also { it[label] = word.substring(addIndex + 1).toInt() }
        }
        removeIndex != -1 -> {
          val label = word.substring(0, removeIndex)
          boxes.getValue(hash(label))
            .also { it.remove(label) }
        }
      }
    }

    boxes.entries.sumOf { (boxId, lenses) ->
      lenses.entries.withIndex().sumOf { (index, entry) ->
        (1 + boxId) * (1 + index) * entry.value
      }
    }
  }

  private fun hash(wordToHash: String) = wordToHash
    .map { it.code }
    .fold(0) { acc, next -> ((acc + next) * 17) % 256 }
}
