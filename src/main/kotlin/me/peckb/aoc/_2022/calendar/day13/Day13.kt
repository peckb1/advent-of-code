package me.peckb.aoc._2022.calendar.day13

import me.peckb.aoc._2022.calendar.day13.Day13.PacketData.Companion.fromIntValue
import me.peckb.aoc._2022.calendar.day13.Day13.PacketData.Companion.fromListValue
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
    val markerOne = fromListValue(listOf(fromIntValue(2)))
    val markerTwo = fromListValue(listOf(fromIntValue(6)))
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
    var inOrder: Boolean? = null
    if (left.intValue != null && right.intValue != null) {
      when (left.intValue.compareTo(right.intValue)) {
        -1 -> inOrder = true
        0 -> { /* no op, keep scanning */ }
        1 -> inOrder = false
      }
    } else if (left.intValue != null && right.listValue != null) {
      inRightOrder(fromListValue(listOf(left)), right)?.also { inOrder = it }
    } else if (left.listValue != null && right.intValue != null) {
      inRightOrder(left, fromListValue(listOf(right)))?.also { inOrder = it }
    } else if (left.listValue != null && right.listValue != null) {
      var index = 0
      var childSearchHasNotFinished = true
      while(inOrder == null && childSearchHasNotFinished) {
        val leftChild = left.listValue.getOrNull(index)
        val rightChild = right.listValue.getOrNull(index)

        if (leftChild != null && rightChild != null) {
          inRightOrder(leftChild, rightChild)?.also { inOrder = it }
        } else if (leftChild == null && rightChild == null) {
          childSearchHasNotFinished = false
        } else {
          inOrder = (leftChild == null)
        }

        index++
      }
    }
    return inOrder
  }


  class PacketData private constructor(val intValue: Int?, val listValue: List<PacketData>?) {
    companion object {
      fun fromIntValue(intValue: Int) = PacketData(intValue, null)
      fun fromListValue(listValue: List<PacketData>) = PacketData(null, listValue)
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
            currentPacketData?.also { parent.add(fromListValue(it)) }
            currentPacketData = parent
          }
          index++
        }

        in '0'..'9' -> {
          // scan to the next ',' and add that number to the current list of
          var endIndex = index + 1
          while (line[endIndex] != ',' && line[endIndex] != ']') {
            endIndex++
          }
          val nextData = fromIntValue(line.substring(index, endIndex).toInt())
          currentPacketData?.add(nextData)
          index = endIndex
        }

        ',' -> index ++
      }
    }

    return currentPacketData?.let(::fromListValue) ?: throw IllegalStateException("There should always be one list")
  }
}
