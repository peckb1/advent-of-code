package me.peckb.aoc._2020.calendar.day14

import arrow.core.fold
import arrow.core.foldLeft
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import org.apache.commons.math3.util.ArithmeticUtils.pow

class Day14 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val registers = mutableMapOf<String, List<Char>>()

    var currentMask = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
    input.forEach { line ->
      if (line.startsWith("mask")) {
        currentMask = line.split(" ").last()
      } else { // line == "mem[n] = x"
        val register = line.drop(4).takeWhile { it != ']' }
        val value = line.split(" ").last().toInt()

        val binaryValue = toBinary(value, currentMask.length)

        val updatedBinaryString = binaryValue.zip(currentMask).map { (valueBinary, maskOverride) ->
          when (maskOverride) {
            'X' -> valueBinary
            else -> maskOverride
          }
        }

        registers[register] = updatedBinaryString
      }
    }

    registers.fold(0L) { acc, (_, binaryString) ->
      acc + binaryString.joinToString("").toLong(2)
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val registers = mutableMapOf<String, Int>()

    var currentMask = "000000000000000000000000000000000000"
    input.forEach { line ->
      if (line.startsWith("mask")) {
        currentMask = line.split(" ").last()
      } else { // line == "mem[n] = x"
        val register = line.drop(4).takeWhile { it != ']' }.toInt()
        val value = line.split(" ").last().toInt()

        val binaryRegister = toBinary(register, currentMask.length)

        val floaterIndices = mutableListOf<Int>()
        val binaryRegisterWithFloaters = currentMask.indices.map { index ->
          when(currentMask[index]) {
            '1' -> currentMask[index]
            '0' -> binaryRegister[index]
            else -> {
              floaterIndices.add(index)
              'X'
            }
          }
        }

        val numberOfBitsChanging = floaterIndices.size
        val numberOfRegistryChanges = pow(2, numberOfBitsChanging)
        val newRegisters = Array(numberOfRegistryChanges) { binaryRegisterWithFloaters.toMutableList() }

        repeat(numberOfRegistryChanges) { n ->
          val bitsToReplaceWith = toBinary(n, numberOfBitsChanging)
          bitsToReplaceWith.forEachIndexed { index, bitChar ->
            newRegisters[n][floaterIndices[index]] = bitChar
          }
        }

        newRegisters.forEach { registerValueChars ->
          val registerValue = registerValueChars.joinToString("")
          registers[registerValue] = value
        }
      }
    }

    registers.fold(0L) { acc, (_, value) ->
      acc + value
    }
  }

  private fun toBinary(x: Int, len: Int): String {
    return String.format("%" + len + "s", Integer.toBinaryString(x)).replace(" ".toRegex(), "0")
  }
}
