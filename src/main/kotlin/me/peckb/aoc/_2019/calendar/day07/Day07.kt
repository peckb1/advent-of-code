package me.peckb.aoc._2019.calendar.day07

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import me.peckb.aoc._2019.calendar.day05.Day05
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.generators.PermutationGenerator
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject

class Day07 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val phaseSettings = arrayOf(0L, 1L, 2L, 3L, 4L)
    val permutations = PermutationGenerator().generatePermutations(phaseSettings)
    val ampSoftware = operations(input)

    val computer = Day05.IntcodeComputer()

    runBlocking {
      permutations.maxOf { permutation ->
        val head = permutation.first()
        val tail = permutation.drop(1)
        val aOutput = computer.generateAmpOutputSingleRun(ampSoftware.toMutableList(), head, 0)
        tail.fold(aOutput) { previousOutput, phaseSetting ->
          computer.generateAmpOutputSingleRun(ampSoftware.toMutableList(),
            phaseSetting,
            previousOutput)
        }
      }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val phaseSettings = arrayOf(5L, 6L, 7L, 8L, 9L)
    val permutations = PermutationGenerator().generatePermutations(phaseSettings)
    val ampSoftware = operations(input)

    val computer = Day05.IntcodeComputer()

    permutations.maxOf { (aPhase, bPhase, cPhase, dPhase, ePhase) ->
      val aInput = LinkedBlockingQueue<Long>().apply { add(aPhase); add(0) }
      val bInput = LinkedBlockingQueue<Long>().apply { add(bPhase) }
      val cInput = LinkedBlockingQueue<Long>().apply { add(cPhase) }
      val dInput = LinkedBlockingQueue<Long>().apply { add(dPhase) }
      val eInput = LinkedBlockingQueue<Long>().apply { add(ePhase) }

      runBlocking {
        listOf(
          async { computer.runAmplification(ampSoftware.toMutableList(), aInput, bInput) },
          async { computer.runAmplification(ampSoftware.toMutableList(), bInput, cInput) },
          async { computer.runAmplification(ampSoftware.toMutableList(), cInput, dInput) },
          async { computer.runAmplification(ampSoftware.toMutableList(), dInput, eInput) },
          async { computer.runAmplification(ampSoftware.toMutableList(), eInput, aInput) },
        ).awaitAll().last()
      }
    }
  }

  private suspend fun Day05.IntcodeComputer.generateAmpOutputSingleRun(
    operations: MutableList<String>,
    phaseSetting: Long,
    input: Long,
  ): Long {
    var counter = 0
    var amplifierAOutput = 0L
    runProgram(operations,
      {
        if (counter == 0) {
          counter++; phaseSetting
        } else input
      },
      { amplifierAOutput = it }
    )
    return amplifierAOutput
  }

  private suspend fun Day05.IntcodeComputer.runAmplification(
    ampSoftware: MutableList<String>,
    input: LinkedBlockingQueue<Long>,
    output: LinkedBlockingQueue<Long>,
  ): Long {
    var lastOutput = 0L
    runProgram(
      ampSoftware,
      { withContext(Dispatchers.IO) { input.take() } },
      { output.add(it); lastOutput = it }
    )
    return lastOutput
  }

  private fun operations(line: String) = line.split(",")
}
