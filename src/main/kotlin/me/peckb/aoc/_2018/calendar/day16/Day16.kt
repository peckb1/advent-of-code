package me.peckb.aoc._2018.calendar.day16

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class Day16 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    var inputIndex = 0
    val inputList = input.toList()
    var readingSamples = true

    var threeOrMorePossibilityCount = 0

    while (inputIndex < inputList.size) {
      if (readingSamples) {
        if (inputList[inputIndex + 1].isEmpty()) {
          readingSamples = false
          inputIndex += 2
        } else {
          val (before, instructionString, after) = inputList.slice(inputIndex..inputIndex + 2)
          val beforeRegisters = before.toRegisters()
          val afterRegisters = after.toRegisters()
          val (_, a, b, c) = instructionString.split(" ").map { it.toInt() }

          val possibleInstructions = Instruction::class.sealedSubclasses.filter { kClass ->
            val constructor = kClass.primaryConstructor
            val instruction = constructor?.call(a, b, c)
            instruction?.possibility(beforeRegisters, afterRegisters) ?: false
          }

          if (possibleInstructions.size >= 3) threeOrMorePossibilityCount++

          inputIndex += 4
        }
      } else {
        // IGNORE FOR PART ONE
        inputIndex = inputList.size
      }
    }

    threeOrMorePossibilityCount
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    var inputIndex = 0
    val inputList = input.toList()
    var readingSamples = true

    val registers = mutableListOf(0, 0, 0, 0)

    val everyInstruction = Instruction::class.sealedSubclasses
    val opcodes = mutableMapOf<Int, Set<KClass<out Instruction>>>()
    repeat(everyInstruction.size) { opcodes[it] = everyInstruction.toSet() }

    while (inputIndex < inputList.size) {
      if (readingSamples) {
        if (inputList[inputIndex + 1].isEmpty()) {
          readingSamples = false
          inputIndex += 2
        } else {
          val (before, instructionString, after) = inputList.slice(inputIndex..inputIndex + 2)
          val beforeRegisters = before.toRegisters()
          val afterRegisters = after.toRegisters()
          val (opcode, a, b, c) = instructionString.split(" ").map { it.toInt() }

          val possibleInstructions = opcodes[opcode]!!.filter { kClass ->
            val constructor = kClass.primaryConstructor
            val instruction = constructor?.call(a, b, c)
            instruction?.possibility(beforeRegisters, afterRegisters) ?: false
          }

          opcodes.merge(opcode, possibleInstructions.toSet(), Set<KClass<out Instruction>>::intersect)
          if (opcodes[opcode]!!.size == 1) {
            opcodes.forEach { (key, value) ->
              if (key != opcode) opcodes[key] = value.minus(opcodes[opcode]!!.first())
            }
          }

          inputIndex += 4
        }
      } else {
        val (opcode, a, b, c) = inputList[inputIndex].split(" ").map { it.toInt() }
        val instructionClass = opcodes[opcode]!!.first()
        val instruction = instructionClass.primaryConstructor!!.call(a, b, c)

        registers[c] = instruction.performAction(registers)

        inputIndex ++
      }
    }
  }

  private fun String.toRegisters(): List<Int> {
    return substringAfter("[").dropLast(1).split(", ").map { it.toInt() }
  }
}

