package me.peckb.aoc._2021.calendar.day16

import me.peckb.aoc._2021.calendar.day16.Instruction.Literal
import me.peckb.aoc._2021.calendar.day16.Instruction.Operator
import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import java.lang.StringBuilder
import javax.inject.Inject

class Day16 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    val CONVERSIONS = mapOf(
      '0' to "0000", '4' to "0100", '8' to "1000", 'C' to "1100",
      '1' to "0001", '5' to "0101", '9' to "1001", 'D' to "1101",
      '2' to "0010", '6' to "0110", 'A' to "1010", 'E' to "1110",
      '3' to "0011", '7' to "0111", 'B' to "1011", 'F' to "1111"
    )
  }

  fun versionSum(fileName: String) = generatorFactory.forFile(fileName).readOne { input ->
    val data = input.map { CONVERSIONS[it]!! }.joinToString("")
    val instructions = mutableListOf<Instruction>()
    data.createInstructions { instructions.addAll(it) }
    instructions.sumOf { it.versionSum() }
  }

  fun evaluateInstructions(fileName: String) = generatorFactory.forFile(fileName).readOne { input ->
    val data = input.map { CONVERSIONS[it]!! }.joinToString("")
    val instructions = mutableListOf<Instruction>()
    data.createInstructions { instructions.addAll(it) }
    instructions.first().value()
  }

  private fun String.createInstructions(startIndex: Int = 0, handler: (List<Instruction>) -> Unit): Int {
    var nextIndex = startIndex
    val version = substring(nextIndex, nextIndex + 3).toInt(2).also { nextIndex += 3 }
    val packetType = substring(nextIndex, nextIndex + 3).toInt(2).also { nextIndex += 3 }

    val instructions = mutableListOf<Instruction>()

    nextIndex = if (packetType == 4) {
      handleLiteral(version, packetType, nextIndex) { instructions.add(it) }
    } else {
      val lengthId = Character.getNumericValue(this[nextIndex]).also { nextIndex++ }
      if (lengthId == 0) {
        handleSizeBasedSubPackets(version, packetType, nextIndex) { instructions.add(it) }
      } else {
        handleCountBasedSubPackets(version, packetType, nextIndex) { instructions.add(it) }
      }
    }

    handler(instructions)

    return nextIndex
  }

  private fun String.handleLiteral(version: Int, packetType: Int, index: Int, handler: (Instruction) -> Unit): Int {
    var nextIndex = index
    var done = false
    val bitStringBuilder = StringBuilder()

    while(!done) {
      substring(nextIndex, nextIndex + 5).also {
        done = it.first() == '0'
        bitStringBuilder.append(it.drop(1))
        nextIndex += 5
      }
    }

    handler(Literal(version, packetType, bitStringBuilder.toString().toLong(2)))

    return nextIndex
  }

  private fun String.handleSizeBasedSubPackets(version: Int, packetType: Int, index: Int, handler: (Instruction) -> Unit): Int {
    var nextIndex = index
    val sizeOfSubPackets = substring(nextIndex, nextIndex + 15).toInt(2).also { nextIndex += 15 }
    val finalIndex = nextIndex + sizeOfSubPackets
    val children = mutableListOf<Instruction>()
    while (nextIndex != finalIndex) {
      nextIndex = createInstructions(nextIndex) { children.addAll(it) }
    }

    handler(Operator(version, packetType, children))

    return nextIndex
  }

  private fun String.handleCountBasedSubPackets(
    version: Int,
    packetType: Int,
    index: Int,
    handler: (Instruction) -> Unit
  ): Int {
    var nextIndex = index
    val numberOfSubPackets = substring(nextIndex, nextIndex + 11).toInt(2).also { nextIndex += 11 }
    val children = mutableListOf<Instruction>()
    repeat(numberOfSubPackets) {
      nextIndex = createInstructions(nextIndex) { children.addAll(it) }
    }

    handler(Operator(version, packetType, children))

    return nextIndex
  }
}
