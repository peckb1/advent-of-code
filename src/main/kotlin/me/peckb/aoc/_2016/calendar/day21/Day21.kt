package me.peckb.aoc._2016.calendar.day21

import me.peckb.aoc._2016.calendar.day21.Day21.Instruction.Move
import me.peckb.aoc._2016.calendar.day21.Day21.Instruction.Reverse
import me.peckb.aoc._2016.calendar.day21.Day21.Instruction.RotateFromLetter
import me.peckb.aoc._2016.calendar.day21.Day21.Instruction.RotateLeft
import me.peckb.aoc._2016.calendar.day21.Day21.Instruction.RotateRight
import me.peckb.aoc._2016.calendar.day21.Day21.Instruction.SwapLetter
import me.peckb.aoc._2016.calendar.day21.Day21.Instruction.SwapPositions
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day21 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::instruction) { input ->
    val wordToChange = WORD.toMutableList()

    input.forEach { instruction ->
      when (instruction) {
        is Move -> wordToChange.move(instruction)
        is Reverse -> wordToChange.reverse(instruction)
        is RotateFromLetter -> wordToChange.rotateFromLetterRight(instruction)
        is RotateLeft -> wordToChange.rotateLeft(instruction)
        is RotateRight -> wordToChange.rotateRight(instruction)
        is SwapLetter -> wordToChange.swapLetter(instruction)
        is SwapPositions -> wordToChange.swapPositions(instruction)
      }
    }

    wordToChange.joinToString("")
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::instruction) { input ->
    val wordToChange = PASSWORD.toMutableList()

    input.toList().reversed().forEach { instruction ->
      when (instruction) {
        is Move -> wordToChange.move(Move(instruction.destinationIndex, instruction.sourceIndex))
        is Reverse -> wordToChange.reverse(instruction)
        is RotateFromLetter -> wordToChange.rotateFromLetterLeft(instruction)
        is RotateLeft -> wordToChange.rotateRight(RotateRight(instruction.steps))
        is RotateRight -> wordToChange.rotateLeft(RotateLeft(instruction.steps))
        is SwapLetter -> wordToChange.swapLetter(instruction)
        is SwapPositions -> wordToChange.swapPositions(instruction)
      }
    }

    wordToChange.joinToString("")
  }

  private fun instruction(line: String): Instruction {
    val parts = line.split(" ")
    when (parts[0]) {
      "swap" -> {
        // swap position X with position Y
        // swap letter X with letter Y
        when (parts[1]) {
          "position" -> return SwapPositions(parts[2].toInt(), parts[5].toInt())
          "letter" -> return SwapLetter(parts[2][0], parts[5][0])
        }
      }
      "rotate" -> {
        // rotate left/right X steps
        // rotate based on position of letter X
        when (parts[1]) {
          "left" -> return RotateLeft(parts[2].toInt())
          "right" -> return RotateRight(parts[2].toInt())
          "based" -> return RotateFromLetter(parts[6][0])
        }
      }
      "reverse" -> {
        // reverse positions X through Y
        return Reverse(parts[2].toInt(), parts[4].toInt())
      }

      "move" -> {
        // move position X to position Y
        return Move(parts[2].toInt(), parts[5].toInt())
      }
    }

    throw IllegalArgumentException("Unknown Instruction: $line")
  }

  sealed class Instruction {
    data class SwapPositions(val indexOne: Int, val indexTwo: Int) : Instruction()
    data class SwapLetter(val letterOne: Char, val letterTwo: Char) : Instruction()
    data class RotateLeft(val steps: Int) : Instruction()
    data class RotateRight(val steps: Int) : Instruction()
    data class RotateFromLetter(val letter: Char) : Instruction()
    data class Reverse(val start: Int, val end: Int) : Instruction()
    data class Move(val sourceIndex: Int, val destinationIndex: Int) : Instruction()
  }

  private fun MutableList<Char>.swapPositions(instruction: SwapPositions) {
    val temp = this[instruction.indexOne]
    this[instruction.indexOne] = this[instruction.indexTwo]
    this[instruction.indexTwo] = temp
  }

  private fun MutableList<Char>.swapLetter(instruction: SwapLetter) {
    val indexOne = this.indexOf(instruction.letterOne)
    val indexTwo = this.indexOf(instruction.letterTwo)
    swapPositions(SwapPositions(indexOne, indexTwo))
  }

  // "borrowed" from 2016 day 08
  private fun MutableList<Char>.rotateRight(instruction: RotateRight) {
    val newRow = Array(size) { ' ' }
    repeat(size) { x ->
      val itemToRotate = this[x]
      val newPos = (x + instruction.steps) % size
      newRow[newPos] = itemToRotate
    }
    newRow.indices.forEach { x -> this[x] = newRow[x] }
  }

  // "borrowed" from 2016 day 08
  private fun MutableList<Char>.rotateLeft(instruction: RotateLeft) {
    val newRow = Array(size) { ' ' }
    repeat(size) { x ->
      val itemToRotate = this[x]
      var newPos = (x - instruction.steps)
      while(newPos < 0) { newPos += size }
      newRow[newPos] = itemToRotate
    }
    newRow.indices.forEach { x -> this[x] = newRow[x] }
  }

  private fun MutableList<Char>.rotateFromLetterLeft(instruction: RotateFromLetter) {
    val index = indexOf(instruction.letter)

    val rotations = (0 until size).first { possibleRotation ->
      var originalIndex = index - possibleRotation
      while(originalIndex < 0) { originalIndex += size }
      val originalRotations = 1 + originalIndex + (if (originalIndex >= 4) 1 else 0)
      (originalIndex + originalRotations) % size == index
    }

    rotateLeft(RotateLeft(rotations))
  }

  private fun MutableList<Char>.rotateFromLetterRight(instruction: RotateFromLetter) {
    val index = indexOf(instruction.letter)
    val rotations = 1 + (if (index >= 4) index + 1 else index)

    rotateRight(RotateRight(rotations))
  }

  private fun MutableList<Char>.reverse(instruction: Reverse) {
    val reversedData = this.slice(instruction.start..instruction.end).reversed()
    (instruction.start..instruction.end).forEachIndexed { reversedIndex, thisIndex ->
      this[thisIndex] = reversedData[reversedIndex]
    }
  }

  private fun MutableList<Char>.move(instruction: Move) {
    val letter = this.removeAt(instruction.sourceIndex)
    this.add(instruction.destinationIndex, letter)
  }

  companion object {
    const val WORD = "abcdefgh"
    const val PASSWORD = "fbgdceah"
  }
}
