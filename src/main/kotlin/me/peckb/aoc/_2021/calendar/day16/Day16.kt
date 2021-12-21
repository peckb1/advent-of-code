package me.peckb.aoc._2021.calendar.day16

import me.peckb.aoc._2021.calendar.day16.Instruction.Literal
import me.peckb.aoc._2021.calendar.day16.Instruction.Operator
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.StringBuilder
import java.util.concurrent.atomic.AtomicInteger
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
     data.createInstructions().versionSum()
  }

  fun evaluateInstructions(fileName: String) = generatorFactory.forFile(fileName).readOne { input ->
    val data = input.map { CONVERSIONS[it]!! }.joinToString("")
    data.createInstructions().value()
  }

  private fun String.createInstructions(index: AtomicInteger = AtomicInteger(0)): Instruction {
    val version = substring(index.get(), index.addAndGet(3)).toInt(2)
    val packetType = substring(index.get(), index.addAndGet(3)).toInt(2)

    return if (packetType == 4) {
      handleLiteral(version, packetType, index)
    } else {
      val lengthId = Character.getNumericValue(this[index.getAndIncrement()])
      if (lengthId == 0) {
        handleSizeBasedSubPackets(version, packetType, index)
      } else {
        handleCountBasedSubPackets(version, packetType, index)
      }
    }
  }

  private fun String.handleLiteral(version: Int, packetType: Int, index: AtomicInteger): Instruction {
    var done = false
    val bitStringBuilder = StringBuilder()

    while(!done) {
      substring(index.get(), index.addAndGet(5)).also {
        done = it.first() == '0'
        bitStringBuilder.append(it.drop(1))
      }
    }

    return Literal(version, packetType, bitStringBuilder.toString().toLong(2))
  }

  private fun String.handleSizeBasedSubPackets(version: Int, packetType: Int, index: AtomicInteger): Instruction {
    val sizeOfSubPackets = substring(index.get(), index.addAndGet(15)).toInt(2)
    val finalIndex = index.get() + sizeOfSubPackets
    val children = mutableListOf<Instruction>()
    while (index.get() != finalIndex) {
      children.add(createInstructions(index))
    }

    return Operator(version, packetType, children)
  }

  private fun String.handleCountBasedSubPackets(version: Int, packetType: Int, index: AtomicInteger): Instruction {
    val numberOfSubPackets = substring(index.get(), index.addAndGet(11)).toInt(2)
    val children = mutableListOf<Instruction>()
    repeat(numberOfSubPackets) {
      children.add(createInstructions(index))
    }

    return Operator(version, packetType, children)
  }
}
