package me.peckb.aoc._2022.calendar.day20

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.abs

class Day20 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::number) { input ->
    val (zeroNode, circle) = setupData(input)
    rotate(circle)
    groveCoordinates(zeroNode).sumOf { circle[it].data }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::number) { input ->
    val key = 811589153L
    val (zeroNode, circle) = setupData(input)
    val originalValues = mutableMapOf<Int, Long>()

    circle.forEach { n ->
      originalValues[n.id] = n.data
      n.data = (n.data * key) % (circle.size - 1)
    }

    repeat(10) { rotate(circle) }

    groveCoordinates(zeroNode).sumOf { originalValues[it]!! * key }
  }

  private fun setupData(input: Sequence<Long>): Pair<Node, List<Node>> {
    var zeroNode: Node? = null

    val circle = input.mapIndexed { index, data ->
      Node(index, data).also { if (data == 0L) zeroNode = it }
    }.toList()

    circle.windowed(2).forEach { (a, b) ->
      a.next = b
      b.previous = a
    }
    (circle.first() to circle.last()).let { (a, b) ->
      a.previous = b
      b.next = a
    }

    return zeroNode!! to circle
  }

  private fun rotate(circle: List<Node>) {
    circle.forEach { node ->
      repeat(abs(node.data).toInt()) {
        if (node.data < 0) {
          swap(node.previous, node)
        } else {
          swap(node, node.next)
        }
      }
    }
  }

  // [x first second y] -> [x second first y]
  private fun swap(first: Node, second: Node) {
    val x = first.previous
    val y = second.next

    y.previous = first
    first.next = y
    first.previous = second
    second.next = first
    second.previous = x
    x.next = second
  }

  private fun groveCoordinates(zeroNode: Node): List<Int> {
    val numbers = mutableListOf<Int>()
    var current = zeroNode
    repeat(3000) {
      current = current.next
      if (((it + 1) % 1000) == 0) numbers.add(current.id)
    }
    return numbers
  }

  private fun number(line: String) = line.toLong()

  data class Node(val id: Int, var data: Long) {
    lateinit var previous: Node
    lateinit var next: Node
  }
}
