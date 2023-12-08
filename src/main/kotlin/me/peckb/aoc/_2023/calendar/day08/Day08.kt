package me.peckb.aoc._2023.calendar.day08

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import org.apache.commons.math3.util.ArithmeticUtils.lcm

class Day08 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (steps, nodes) = createData(input)

    var location = "AAA"
    val stepsUntilEnd = steps.takeWhile { direction ->
      when (direction) {
        'L' -> location = nodes[location]?.leftId!!
        'R' -> location = nodes[location]?.rightID!!
      }

      location != "ZZZ"
    }.count()

    stepsUntilEnd + 1
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (steps, nodes) = createData(input)

    val startNodes = nodes.keys.filter { it.endsWith('A') }

    val stepsForEachStart = startNodes.map { startNodeId ->
      var currentNode = startNodeId
      val stepsUtilEnd = steps.takeWhile { stepToTake ->
        when (stepToTake) {
          'L' -> currentNode = nodes[currentNode]?.leftId!!
          'R' -> currentNode = nodes[currentNode]?.rightID!!
        }
        !currentNode.endsWith('Z')
      }.count()

      stepsUtilEnd + 1L
    }

    stepsForEachStart.fold(1L) { acc, next -> lcm(acc, next) }
  }

  data class Node(val id: String, val leftId: String, val rightID: String)

  private fun createData(input: Sequence<String>): Pair<Sequence<Char>, MutableMap<String, Node>> {
    val iterator = input.iterator()
    val steps = iterator.next().toCharArray()
      .also { iterator.next() } // skip blank line
      .asSequence()
      .repeat()

    val nodes = mutableMapOf<String, Node>()

    while(iterator.hasNext()) {
      val nodeString = iterator.next()
      val (nodeId, nodeLR) = nodeString.split(" = ")

      val (left, right) = nodeLR.split(", ")
      val node = Node(nodeId, left.drop(1), right.dropLast(1))

      nodes[nodeId] = node
    }

    return steps to nodes
  }

  fun <T> Sequence<T>.repeat() : Sequence<T> = sequence {
    while(true) yieldAll(this@repeat)
  }
}
