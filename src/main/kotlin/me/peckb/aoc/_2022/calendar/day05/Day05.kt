package me.peckb.aoc._2022.calendar.day05

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day05 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    movePackages(input)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    movePackages(input, stackModifierFunction = List<Char>::reversed)
  }

  private fun movePackages(
    input: Sequence<String>,
    stackModifierFunction: ((List<Char>) -> List<Char>) = { it }
  ): String {
    val inputList = input.toList()
    val (stackCountRowIndex, stacks) = generateStacks(inputList)

    ((stackCountRowIndex + 2) until inputList.size).forEach { inputListIndex ->
      val moveInstruction = inputList[inputListIndex]
      val parts = moveInstruction.split(" ")
      val numToMove = parts[1].toInt()
      val sourceIndex = parts[3].toInt() - 1
      val destinationIndex = parts[5].toInt() - 1

      val stacksToMove = (0 until numToMove).map { stacks[sourceIndex].removeFirst() }
      stackModifierFunction(stacksToMove).forEach { stacks[destinationIndex].addFirst(it) }
    }

    return stacks.map { it.first() }.joinToString("")
  }

  private fun generateStacks(inputList: List<String>): Pair<Int, Array<ArrayDeque<Char>>> {
    val stackCountRow = inputList.find { it[1] == '1' }!!
    val stackCountRowIndex = inputList.indexOf(stackCountRow)
    val stackCounts = stackCountRow.trim().split(" ").filterNot { it.isEmpty() }.size

    val stacks = Array(stackCounts) { ArrayDeque<Char>() }
    repeat(stackCountRowIndex) { row ->
      inputList[row].chunked(4).forEachIndexed { stackId, boxChunk ->
        boxChunk[1].takeUnless { it == ' ' }?.let { stacks[stackId].addLast(it) }
      }
    }

    return stackCountRowIndex to stacks
  }
}
