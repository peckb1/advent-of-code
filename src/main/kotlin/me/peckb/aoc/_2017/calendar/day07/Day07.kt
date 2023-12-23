package me.peckb.aoc._2017.calendar.day07

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.flatMap
import arrow.core.getOrElse
import me.peckb.aoc._2021.calendar.day18.leftOr
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day07 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::disk) { input ->
    findRoot(input).name
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::disk) { input ->
    val root = findRoot(input)
    root.findBadWeight().leftOr(null)
  }

  private fun findRoot(input: Sequence<Program>): Program {
    val programMap = mutableMapOf<String, Program>()
    val namesNeedingConversion = mutableMapOf<String, Program>()

    input.forEach { program ->
      programMap[program.name] = program
      program.programsBalanced.indices.forEach { index ->
        program.programsBalanced[index].onLeft { name ->
          programMap[name]?.also { programToReplace ->
            programToReplace.parent = program
            program.programsBalanced[index] = Right(programToReplace)
          } ?: run {
            namesNeedingConversion[name] = program
          }
        }
      }
      namesNeedingConversion[program.name]?.let { programNeedingFixing ->
        val itemToReplace = programNeedingFixing.programsBalanced.withIndex().first { it.value.leftOr("") == program.name }
        programNeedingFixing.programsBalanced[itemToReplace.index] = Right(program)
        program.parent = programNeedingFixing
        namesNeedingConversion.remove(program.name)
      }
    }

    var randomNode = programMap.firstNotNullOf { it }.value
    while(randomNode.parent != null) {
      randomNode = randomNode.parent!!
    }

    return randomNode
  }

  private fun disk(line: String): Program {
    val parts = line.split(" -> ")
    val (name, weight) = parts[0].split(" ").let {
      it.first() to it.last().drop(1).dropLast(1).toInt()
    }
    val programsBalanced: MutableList<Either<String, Program>> = if (parts.size > 1) {
      parts[1].split(", ").map(::Left).toMutableList()
    } else {
      mutableListOf()
    }

    return Program(name, weight, programsBalanced)
  }

  data class Program(
    val name: String,
    val weight: Int,
    val programsBalanced: MutableList<Either<String, Program>>,
    var parent: Program? = null
  ) {
    private var totalWeight: Int? = null

    private fun getTotalWeight(): Int {
      return totalWeight ?: run {
        val myTotalWeight = weight + programsBalanced.sumOf { program ->
          program.map { it.getTotalWeight() }.getOrElse { 0 }
        }
        myTotalWeight.also { totalWeight = it }
      }
    }

    fun findBadWeight(): Either<Int, Int> {
      val weights = programsBalanced
        .map { program -> program.map { it.getTotalWeight() }.getOrElse { 0 } }
        .withIndex()
        .groupBy { it.value }

      return if (weights.size == 1) {
        // all my kids are fine - I'm the bad weight!
        Right(weight)
      } else {
        val problematicChildIndex = weights.filter { it.value.size == 1 }.firstNotNullOf { it }.value.first().index
        val problematicChild = programsBalanced[problematicChildIndex]

        problematicChild.mapLeft { -1 }.flatMap { child ->
          val correctWeight = weights.values.find { it.size != 1 }!!.first().value
          val badTotalWeight = weights.values.find { it.size == 1 }!!.first().value

          child.findBadWeight().flatMap { badWeight ->
            // we're in the Right side of the either, we have the bad weight, so we need
            // to find how much it's off by and return the correct weight on the Left side so
            // future parents wont fall into this block
            val weightDifference = (badTotalWeight - correctWeight)
            Left(badWeight - weightDifference)
          }
        }
      }
    }
  }
}
