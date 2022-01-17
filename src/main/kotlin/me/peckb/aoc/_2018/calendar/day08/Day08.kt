package me.peckb.aoc._2018.calendar.day08

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day08 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val numbers = input.split(" ").map { it.toInt() }
    val root = findRoot(numbers)

    root.metadataSum
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val numbers = input.split(" ").map { it.toInt() }
    val root = findRoot(numbers)

    root.value
  }

  private fun findRoot(numbers: List<Int>) = findNode(numbers, 0).first

  private fun findNode(numbers: List<Int>, startIndex: Int): Pair<Node, Int> {
    val numberOfChildren = numbers[startIndex]
    val numberOfMetadata = numbers[startIndex + 1]

    var childEndedAt = startIndex + 2
    val children = (0 until numberOfChildren).map {
      val (node, index) = findNode(numbers, childEndedAt)
      node.also { childEndedAt = index }
    }
    val metadata = (0 until numberOfMetadata).map { indexModifier ->
      numbers[childEndedAt + indexModifier]
    }

    return Node(children, metadata) to childEndedAt + numberOfMetadata
  }

  data class Node(val children: List<Node>, val metadata: List<Int>) {
    val metadataSum: Int = metadata.sum() + children.sumOf { it.metadataSum }

    val value: Int = if (children.isEmpty()) { metadata.sum() } else {
      metadata.map { it - 1 }.sumOf { childIndex ->
        if (childIndex in children.indices) { children[childIndex].value } else { 0 }
      }
    }
  }
}
