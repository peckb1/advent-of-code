package me.peckb.aoc._2021.calendar.day16

import me.peckb.aoc._2021.calendar.day16.Day16.Instruction.Literal
import me.peckb.aoc._2021.calendar.day16.Day16.Instruction.Operator
import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import java.lang.StringBuilder
import javax.inject.Inject

class Day16 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    val CONVERSIONS = mapOf(
      '0' to "0000",
      '1' to "0001",
      '2' to "0010",
      '3' to "0011",
      '4' to "0100",
      '5' to "0101",
      '6' to "0110",
      '7' to "0111",
      '8' to "1000",
      '9' to "1001",
      'A' to "1010",
      'B' to "1011",
      'C' to "1100",
      'D' to "1101",
      'E' to "1110",
      'F' to "1111"
    )
  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readOne { input ->
    val data = input.flatMap { CONVERSIONS[it]!!.split("") }.joinToString("")
    val (_, instructions) = createInstructions(data, 0)
    instructions.sumOf { it.versionSum() }
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readOne { input ->
    val data = input.flatMap { CONVERSIONS[it]!!.split("") }.joinToString("")
    val (_, instructions) = createInstructions(data, 0)
    instructions.first().value()
  }

  private fun createInstructions(data: String, startIndex: Int): Pair<Int, List<Instruction>> {
    var nextIndex = startIndex
    val version = data.substring(nextIndex, nextIndex + 3).toInt(2)
    nextIndex += 3
    val packetType = data.substring(nextIndex, nextIndex + 3).toInt(2)
    nextIndex += 3

    val instructions = mutableListOf<Instruction>()

    if (packetType == 4) {
      // literal
      var done = false
      val bitStringBuilder = StringBuilder()
      while(!done) {
        val next = data.substring(nextIndex, nextIndex + 5)
        nextIndex += 5
        done = next.first() == '0'
        bitStringBuilder.append(next.drop(1))
      }
      val literal = Literal(version, packetType, bitStringBuilder.toString().toLong(2))
      instructions.add(literal)
    } else {
      // operator
      val lengthId = Character.getNumericValue(data[nextIndex])
      nextIndex++
      if (lengthId == 0) {
        val sizeOfSubPackets = data.substring(nextIndex, nextIndex + 15).toInt(2)
        nextIndex += 15
        var indexAfterFetchingInstruction = nextIndex
        val children = mutableListOf<Instruction>()
        while (indexAfterFetchingInstruction != nextIndex + sizeOfSubPackets) {
          val results = createInstructions(data, indexAfterFetchingInstruction)
          indexAfterFetchingInstruction = results.first
          children.addAll(results.second)
        }
        val operator = Operator(version, packetType, children)
        instructions.add(operator)
        nextIndex = indexAfterFetchingInstruction
      } else {
        val numberOfSubPackets = data.substring(nextIndex, nextIndex + 11).toInt(2)
        nextIndex += 11
        var indexAfterFetchingInstruction = nextIndex
        val children = mutableListOf<Instruction>()
        repeat(numberOfSubPackets) {
          val results = createInstructions(data, indexAfterFetchingInstruction)
          indexAfterFetchingInstruction = results.first
          children.addAll(results.second)
        }
        val operator = Operator(version, packetType, children)
        instructions.add(operator)
        nextIndex = indexAfterFetchingInstruction
      }
    }

    return nextIndex to instructions
  }

  sealed class Instruction(val version: Int, val packetType: Int) {
    abstract fun versionSum(): Int
    abstract fun value(): Long

    class Operator(version: Int, packetType: Int, val data: List<Instruction>) : Instruction(version, packetType) {
      override fun versionSum(): Int {
        return version + data.sumOf { it.versionSum() }
      }

      override fun value(): Long {
        return when (packetType) {
          0 -> data.sumOf { it.value() }
          1 -> data.fold(1) { acc, i -> acc * i.value() }
          2 -> data.minOf { it.value() }
          3 -> data.maxOf { it.value() }
          5 -> {
            val (first, second) = data
            if (first.value() > second.value()) 1 else 0
          }
          6 -> {
            val (first, second) = data
            if (first.value() < second.value()) 1 else 0
          }
          7 -> {
            val (first, second) = data
            if (first.value() == second.value()) 1 else 0
          }
          else -> throw Exception("Invalid Operator")
        }
      }

      override fun toString(): String {
        return "Op($version, $packetType: $data)"
      }


    }
    class Literal(version: Int, packetType: Int, val data: Long) : Instruction(version, packetType) {
      override fun versionSum(): Int {
        return version
      }

      override fun value(): Long {
        return data
      }

      override fun toString(): String {
        return "Lit($version, $packetType: $data)"
      }
    }
  }
}
