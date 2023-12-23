package me.peckb.aoc._2017.calendar.day25

import arrow.core.fold
import arrow.core.foldLeft
import me.peckb.aoc._2017.calendar.day25.Day25.Direction.LEFT
import me.peckb.aoc._2017.calendar.day25.Day25.Direction.RIGHT
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day25 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val setup = input.toList()

    val stepsToRun = setup[1].substringAfterLast("after ").substringBefore(" steps").toInt()
    val states = setup.slice(2 until setup.size).chunked(10).map { instructions ->
      val id = instructions[1].substringAfterLast(" ").dropLast(1)[0]

      val whatToWriteIfZero = Character.getNumericValue(instructions[3].substringAfterLast(" ").dropLast(1)[0]).toLong()
      val directionToMoveIfZero = Direction.valueOf(instructions[4].substringAfterLast(" ").dropLast(1).uppercase())
      val stateToBeInIfZero = instructions[5].substringAfterLast(" ").dropLast(1)[0]

      val whatToWriteIfOne = Character.getNumericValue(instructions[7].substringAfterLast(" ").dropLast(1)[0]).toLong()
      val directionToMoveIfOne = Direction.valueOf(instructions[8].substringAfterLast(" ").dropLast(1).uppercase())
      val stateToBeInIfOne = instructions[9].substringAfterLast(" ").dropLast(1)[0]

      State(id, whatToWriteIfZero, directionToMoveIfZero, stateToBeInIfZero, whatToWriteIfOne, directionToMoveIfOne, stateToBeInIfOne)
    }.associateBy { it.id }

    val tape = mutableMapOf<Long, Long>().withDefault { 0L }

    var index = 0L
    var state: Char = setup[0].substringAfterLast(" ").dropLast(1)[0]
    repeat(stepsToRun) {
      val myValue = tape.getValue(index)
      val myState = states[state]!!

      val direction = if (myValue == 1L) {
        state = myState.stateToBeInIfOne
        tape[index] = myState.whatToWriteIfOne
        myState.directionToMoveIfOne
      } else {
        state = myState.stateToBeInIfZero
        tape[index] = myState.whatToWriteIfZero
        myState.directionToMoveIfZero
      }

      when (direction) {
        RIGHT -> index++
        LEFT -> index--
      }
    }

    tape.fold(0L) { i, entry -> i + entry.value }
  }

  enum class Direction { RIGHT, LEFT }

  data class State(
    val id: Char,
    val whatToWriteIfZero: Long,
    val directionToMoveIfZero: Direction,
    val stateToBeInIfZero: Char,
    val whatToWriteIfOne: Long,
    val directionToMoveIfOne: Direction,
    val stateToBeInIfOne: Char
  )
}
