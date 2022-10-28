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
    val phaseSettings = arrayOf(0, 1, 2, 3, 4)
    val permutations = PermutationGenerator().generatePermutations(phaseSettings)
    val ampSoftware = operations(input)

    val computer = Day05.IntcodeComputer()

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

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val phaseSettings = arrayOf(5L, 6L, 7L, 8L, 9L)
    val permutations = PermutationGenerator().generatePermutations(phaseSettings)
    val ampSoftware = operations(input)

    val computer = Day05.IntcodeComputer()

    permutations.maxOf { (aPhase, bPhase, cPhase, dPhase, ePhase) ->
      val ampA = ampSoftware.toMutableList()
      val ampB = ampSoftware.toMutableList()
      val ampC = ampSoftware.toMutableList()
      val ampD = ampSoftware.toMutableList()
      val ampE = ampSoftware.toMutableList()

      val aInput = LinkedBlockingQueue<Long>().apply { add(aPhase); add(0) }
      val bInput = LinkedBlockingQueue<Long>().apply { add(bPhase) }
      val cInput = LinkedBlockingQueue<Long>().apply { add(cPhase) }
      val dInput = LinkedBlockingQueue<Long>().apply { add(dPhase) }
      val eInput = LinkedBlockingQueue<Long>().apply { add(ePhase) }

      runBlocking {
        var lastEOutput = 0L
        listOf(
          async {
            computer.runProgramAsync(
              ampA,
              { withContext(Dispatchers.IO) { aInput.take().also { logIn("A", it) } } },
              { bInput.add(it); logOut("A", it) }
            )
          },
          async {
            computer.runProgramAsync(
              ampB,
              { withContext(Dispatchers.IO) { bInput.take().also { logIn("B", it) } } },
              { cInput.add(it); logOut("B", it) }
            )
          },
          async {
            computer.runProgramAsync(
              ampC,
              { withContext(Dispatchers.IO) { cInput.take().also { logIn("C", it) } } },
              { dInput.add(it); logOut("C", it) }
            )
          },
          async {
            computer.runProgramAsync(
              ampD,
              { withContext(Dispatchers.IO) { dInput.take().also { logIn("D", it) } } },
              { eInput.add(it); logOut("D", it) }
            )
          },
          async {
            computer.runProgramAsync(
              ampE,
              { withContext(Dispatchers.IO) { eInput.take().also { logIn("E", it) } } },
              { lastEOutput = it; logOut("E", it); aInput.add(it) }
            )
          }
        ).awaitAll()
        lastEOutput
      }
    }
  }

  private fun logIn(ampId: String, input: Long) {
    // println("$ampId getting $input for processing")
  }

  private fun logOut(ampId: String, output: Long) {
    // println("$ampId pushing out $output")
  }

  private fun Day05.IntcodeComputer.generateAmpOutputSingleRun(
    operations: MutableList<String>,
    phaseSetting: Int,
    input: Int,
  ): Int {
    var counter = 0
    var amplifierAOutput = 0
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

  private fun operations(line: String) = line.split(",")
}
