package me.peckb.aoc._2021.calendar.day16

import me.peckb.aoc._2021.calendar.day16.Instruction.Literal
import me.peckb.aoc._2021.calendar.day16.Instruction.Operator
import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import java.lang.StringBuilder
import javax.inject.Inject

class Day16 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    val CONVERSIONS = mapOf(
      '0' to "0000", '1' to "0001", '2' to "0010", '3' to "0011",
      '4' to "0100", '5' to "0101", '6' to "0110", '7' to "0111",
      '8' to "1000", '9' to "1001", 'A' to "1010", 'B' to "1011",
      'C' to "1100", 'D' to "1101", 'E' to "1110", 'F' to "1111"
    )
  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readOne { input ->
    val data = input.map { CONVERSIONS[it]!! }.joinToString("")
    val (_, instructions) = createInstructions(data, 0)
    instructions.sumOf { it.versionSum() }
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readOne { input ->
    val data = input.map { CONVERSIONS[it]!! }.joinToString("")
    val (_, instructions) = createInstructions(data, 0)
    instructions.first().value()
  }

  private fun createInstructions(data: String, startIndex: Int): Pair<Int, List<Instruction>> {
    var nextIndex = startIndex
    val version = data.substring(nextIndex, nextIndex + 3).toInt(2).also { nextIndex += 3 }
    val packetType = data.substring(nextIndex, nextIndex + 3).toInt(2).also { nextIndex += 3 }

    val instructions = mutableListOf<Instruction>()

    if (packetType == 4) {
      var done = false
      val bitStringBuilder = StringBuilder()
      while(!done) {
        data.substring(nextIndex, nextIndex + 5).also {
          nextIndex += 5
          done = it.first() == '0'
          bitStringBuilder.append(it.drop(1))
        }
      }
      instructions.add(Literal(version, packetType, bitStringBuilder.toString().toLong(2)))
    } else {
      val lengthId = Character.getNumericValue(data[nextIndex]).also { nextIndex++ }

      if (lengthId == 0) {
        val sizeOfSubPackets = data.substring(nextIndex, nextIndex + 15).toInt(2).also { nextIndex += 15 }
        val finalIndex = nextIndex + sizeOfSubPackets
        val children = mutableListOf<Instruction>()
        while (nextIndex != finalIndex) {
          createInstructions(data, nextIndex).also { (index, instructions) ->
            nextIndex = index
            children.addAll(instructions)
          }
        }
        instructions.add(Operator(version, packetType, children))
      } else {
        val numberOfSubPackets = data.substring(nextIndex, nextIndex + 11).toInt(2).also { nextIndex += 11 }
        val children = mutableListOf<Instruction>()
        repeat(numberOfSubPackets) {
          createInstructions(data, nextIndex).also { (index, instructions) ->
            nextIndex = index
            children.addAll(instructions)
          }
        }
        instructions.add(Operator(version, packetType, children))
      }
    }

    return nextIndex to instructions
  }
}
