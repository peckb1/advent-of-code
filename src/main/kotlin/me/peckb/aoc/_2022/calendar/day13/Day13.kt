package me.peckb.aoc._2022.calendar.day13

import arrow.core.Either
import arrow.core.getOrElse
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalStateException


class Day13 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    input.chunked(3).map { (p1, p2, _) ->
      packet(p1) to packet(p2)
    }.mapIndexedNotNull { index, packetPair->
      val (packetListOne, packetListTwo) = packetPair
      when (inRightOrder(packetListOne, packetListTwo)) {
        true -> index + 1
        false -> null
        else -> throw IllegalStateException("Why do I not know the order")
      }
    }.sum()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val markerOne = listOf(PacketData(Either.Right(listOf(PacketData(Either.Left(2))))))
    val markerTwo = listOf(PacketData(Either.Right(listOf(PacketData(Either.Left(6))))))
    val allPackets: MutableList<List<PacketData>> = mutableListOf(
      markerOne,
      markerTwo
    )
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

  private fun inRightOrder(leftList: List<PacketData>, rightList: List<PacketData>): Boolean? {
    var inOrder: Boolean? = null
    var iCouldNotTell = false
    var index = 0
    while(inOrder == null && !iCouldNotTell) {
      val left = leftList.getOrNull(index)
      val right = rightList.getOrNull(index)
      if (left != null && right != null) {
        if (left.isInt() && right.isInt()) {
          if(left.intValue() > right.intValue()) {
            inOrder = false
          }
          if (left.intValue() < right.intValue()) {
            inOrder = true
          }
        } else if (left.isInt() && right.isList()) {
          val childOrder = inRightOrder(listOf(PacketData(Either.Left(left.intValue()))), right.listValue())
          childOrder?.also { inOrder = it }
        } else if (left.isList() && right.isInt()) {
          val childOrder = inRightOrder(left.listValue(), listOf(PacketData(Either.Left(right.intValue()))))
          childOrder?.also { inOrder = it }
        } else { // left.isList && right.isList
          val childOrder = inRightOrder(left.listValue(), right.listValue())
          childOrder?.also { inOrder = it }
        }
      } else if (left == null && right == null) {
        iCouldNotTell = true
      } else {
        inOrder = (left == null)
      }
      index++
    }
    return inOrder
  }


  data class PacketData(val data: Either<Int, List<PacketData>>) {
    fun isInt() = data.isLeft()
    fun isList() = data.isRight()

    fun intValue(): Int = data.swap().getOrElse { throw IllegalStateException() }
    fun listValue(): List<PacketData> = data.getOrElse { throw IllegalStateException() }
  }

  private fun packet(line: String): List<PacketData> {
    var index = 1
    val packetParents = ArrayDeque<MutableList<PacketData>>()
    var currentPacketData: MutableList<PacketData> = mutableListOf()
    // DEV NOTE: by keeping it less than `line.length - 1` we don't need to worry about an empty parent stack
    //           for our outermost packet data list
    while (index < line.length - 1) {
      when (val c = line[index]) {
        '[' -> {
          // start a list to add items to
          packetParents.add(currentPacketData)
          currentPacketData = mutableListOf()
          index++
        }

        ']' -> {
          val parent = packetParents.removeLast()
          parent.add(PacketData(Either.Right(currentPacketData)))
          currentPacketData = parent
          index++
        }

        in '0'..'9' -> {
          // scan to the next ',' and add that number to the current list of
          var endIndex = index + 1
          while (line[endIndex] != ',' && line[endIndex] != ']') {
            endIndex++
          }
          val nextData = PacketData(Either.Left(line.substring(index, endIndex).toInt()))
          currentPacketData.add(nextData)
          index = endIndex
        }

        ',' -> index ++
      }
    }

    return currentPacketData
  }

}
