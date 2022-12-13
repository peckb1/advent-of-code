package me.peckb.aoc._2022.calendar.day13

import me.peckb.aoc._2022.calendar.day13.Day13.PacketData.IntValue
import me.peckb.aoc._2022.calendar.day13.Day13.PacketData.ListValue
import me.peckb.aoc._2022.calendar.day13.Day13.PacketMatcher.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalStateException


class Day13 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    input.chunked(3)
      .map { (p1, p2, _) -> packet(p1) to packet(p2) }
      .mapIndexedNotNull { index, packetPair ->
        when (inRightOrder(packetPair.first, packetPair.second)) {
          true -> index + 1
          false -> null
          else -> throw IllegalStateException("Why do I not know the order")
        }
      }.sum()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val markerOne = ListValue(listOf(IntValue(2)))
    val markerTwo = ListValue(listOf(IntValue(6)))
    val allPackets: MutableList<PacketData> = mutableListOf(markerOne, markerTwo)

    input.chunked(3).forEach { (p1, p2, _) ->
      allPackets.add(packet(p1))
      allPackets.add(packet(p2))
    }

    allPackets.sortWith { packetDataOne, packetDataTwo ->
      when (inRightOrder(packetDataOne, packetDataTwo)) {
        true -> -1
        false -> 1
        null -> 0
      }
    }

    (allPackets.indexOf(markerOne) + 1) * (allPackets.indexOf(markerTwo) + 1)
  }

  private fun inRightOrder(left: PacketData, right: PacketData): Boolean? {
    return when(val pm = PacketMatcher.from(left, right)) {
      is IntInt -> pm.left.comparedTo(pm.right)
      is IntList -> inRightOrder(pm.left.wrapInList(), pm.right)
      is ListInt -> inRightOrder(pm.left, pm.right.wrapInList())
      is ListList -> {
        var index = 0
        var childSearchHasNotFinished = true
        var inOrder: Boolean? = null
        while (inOrder == null && childSearchHasNotFinished) {
          val leftChild = pm.left.value.getOrNull(index)
          val rightChild = pm.right.value.getOrNull(index)

          if (leftChild != null && rightChild != null) {
            inOrder = inRightOrder(leftChild, rightChild)
          } else if (leftChild == null && rightChild == null) {
            childSearchHasNotFinished = false
          } else {
            inOrder = (leftChild == null)
          }

          index++
        }
        inOrder
      }
    }
  }

sealed class PacketData {
  data class ListValue(val value: List<PacketData>) : PacketData()
  data class IntValue(val value: Int) : PacketData() {
    fun wrapInList() = ListValue(listOf(this))

    fun comparedTo(other: IntValue): Boolean? = when (value.compareTo(other.value)) {
      -1 -> true
      1 -> false
      else -> null
    }
  }
}

sealed class PacketMatcher {
  data class IntInt(val left: IntValue, val right: IntValue) : PacketMatcher()
  data class IntList(val left: IntValue, val right: ListValue) : PacketMatcher()
  data class ListInt(val left: ListValue, val right: IntValue) : PacketMatcher()
  data class ListList(val left: ListValue, val right: ListValue) : PacketMatcher()

  companion object {
    fun from(left: PacketData, right: PacketData): PacketMatcher {
      return if (left is IntValue && right is IntValue) IntInt(left, right)
      else if (left is IntValue && right is ListValue) IntList(left, right)
      else if (left is ListValue && right is IntValue) ListInt(left, right)
      else ListList(left as ListValue, right as ListValue)
    }
  }
}

  private fun packet(line: String): PacketData {
    var index = 0
    val packetParents = ArrayDeque<MutableList<PacketData>>()
    var currentPacketData: MutableList<PacketData>? = null

    while (index < line.length) {
      when (line[index]) {
        '[' -> {
          // start a list to add items to
          currentPacketData?.also(packetParents::add)
          currentPacketData = mutableListOf()
          index++
        }

        ']' -> {
          // we finished a list, add it to our parent (if we have a parent to add to)
          packetParents.removeLastOrNull()?.also { parent ->
            currentPacketData?.also { parent.add(ListValue(it)) }
            currentPacketData = parent
          }
          index++
        }

        in '0'..'9' -> {
          // scan to the end of the number and add that number to the current list of
          var endIndex = index + 1
          while (line[endIndex] != ',' && line[endIndex] != ']') {
            endIndex++
          }
          val nextData = IntValue(line.substring(index, endIndex).toInt())
          currentPacketData?.add(nextData)
          index = endIndex
        }

        ',' -> index++
      }
    }

    return currentPacketData?.let(::ListValue) ?: throw IllegalStateException("There should always be one list")
  }
}
