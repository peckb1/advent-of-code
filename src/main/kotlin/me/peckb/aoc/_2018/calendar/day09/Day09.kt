package me.peckb.aoc._2018.calendar.day09

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day09 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val (players, totalMarbles) = load(input)
    val scores = mutableMapOf<Int, Int>().apply { repeat(players) { put(it, 0) } }

    var currentPlayer = 0
    var currentMarbleIndex = 0
    val marbles = mutableListOf(0)
    (1..totalMarbles).forEach { marble ->
      if (marble % MARBLE_INTERVAL == 0) {
        val marbleIndexToRemove = (currentMarbleIndex - JUMP_BACK_COUNT).let { if (it < 0) (it + marbles.size) else it }
        val removedMarble = marbles.removeAt(marbleIndexToRemove)
        val score = marble + removedMarble
        scores.merge(currentPlayer, score, Int::plus)
        currentMarbleIndex = (marbleIndexToRemove % marbles.size)
      } else {
        val leftIndex = (currentMarbleIndex + 1) % marbles.size
        currentMarbleIndex = if (leftIndex == marbles.size - 1) {
          marbles.add(marble)
          marbles.size - 1
        } else {
          marbles.add(leftIndex + 1, marble)
          leftIndex + 1
        }
      }

      currentPlayer++
      currentPlayer %= scores.size
    }

    scores.maxOf { it.value }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val (players, totalMarbles) = load(input)

    val scores = mutableMapOf<Int, Long>().apply { repeat(players) { put(it, 0) } }
    val initial = Node(0).apply { parent = this; child = this; }

    var currentPlayer = 0
    var currentNode = initial

    (1..totalMarbles * 100).forEach { marbleValue ->
      if (marbleValue % MARBLE_INTERVAL == 0) {
        repeat(JUMP_BACK_COUNT) { currentNode = currentNode.parent }
        (marbleValue + currentNode.value).toLong().also { scores.merge(currentPlayer, it, Long::plus) }
        currentNode = currentNode.removeSelf()
      } else {
        currentNode = Node(marbleValue).insertAfter(currentNode.child)
      }

      currentPlayer++
      currentPlayer %= scores.size
    }

    scores.maxOf { it.value }
  }

  private fun load(input: String): Pair<Int, Int> {
    val parts = input.split(" ")
    val players = parts[0].toInt()
    val totalMarbles = parts[6].toInt()

    return players to totalMarbles
  }

  data class Node(val value: Int) {
    lateinit var parent: Node
    lateinit var child: Node

    fun removeSelf(): Node {
      parent.child = child
      child.parent = parent
      return child
    }

    fun insertAfter(newParent: Node) = apply {
      parent = newParent
      child = newParent.child
      newParent.child = this
      child.parent = this
    }
  }

  companion object {
    const val JUMP_BACK_COUNT = 7
    const val MARBLE_INTERVAL = 23
  }
}
