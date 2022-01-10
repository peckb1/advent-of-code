package me.peckb.aoc._2017.calendar.day08

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.Long.Companion.MIN_VALUE
import kotlin.math.max

class Day08 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::instruction) { input ->
    val (registers, _) = followInstructions(input)

    registers.maxOf { it.value }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::instruction) { input ->
    val (_, maxValue) = followInstructions(input)
    maxValue
  }

  private fun followInstructions(input: Sequence<Instruction>): Pair<Map<String, Long>, Long> {
    val registers = mutableMapOf<String, Long>().withDefault { 0L }

    var maxValue = MIN_VALUE

    input.forEach { instruction ->
      val conditionValue = registers.getValue(instruction.registerForCondition)
      if (instruction.conditionCheck(conditionValue)) {
        val modifyingValue = registers.getValue(instruction.registerToModify)
        val newValue = instruction.modifyingOperation(modifyingValue)
        maxValue = max(maxValue, newValue)
        registers[instruction.registerToModify] = newValue
      }
    }

    return registers to maxValue
  }

  private fun instruction(line: String): Instruction {
    // c dec -10 if a >= 1
    val parts = line.split(" ")
    val registerToModify = parts[0]
    val genericOperation: (Long, Long) -> Long = when(parts[1]) {
      "inc" -> Long::plus
      "dec" -> Long::minus
      else -> throw IllegalArgumentException("Unknown Operation: $line")
    }
    val modifyingValue = parts[2].toLong()
    val modifyingOperation = { value: Long -> genericOperation(value, modifyingValue) }
    val registerForCondition = parts[4]

    val conditionValue = parts[6].toLong()
    val conditionCheck = { value: Long ->
      when (parts[5]) {
        "<" -> value < conditionValue
        "<=" -> value <= conditionValue
        "==" -> value == conditionValue
        "!=" -> value != conditionValue
        ">=" -> value >= conditionValue
        ">" -> value > conditionValue
        else -> throw IllegalArgumentException("Unknown Condition: $line")
      }
    }
    return Instruction(registerToModify, modifyingOperation, registerForCondition, conditionCheck)
  }

  data class Instruction(
    val registerToModify: String,
    val modifyingOperation: (Long) -> Long,
    val registerForCondition: String,
    val conditionCheck: (Long) -> Boolean
  )
}
