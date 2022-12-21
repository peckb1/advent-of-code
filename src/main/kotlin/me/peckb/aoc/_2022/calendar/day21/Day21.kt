package me.peckb.aoc._2022.calendar.day21

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalArgumentException
import kotlin.math.abs

class Day21 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::monkeyJob) { input ->
    val monkeyJobs = mutableMapOf<String, MonkeyJob>()
    input.forEach { monkeyJobs[it.name] = it }

    monkeyJobs[ROOT_NAME]?.getData(monkeyJobs)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::monkeyJob) { input ->
    val monkeyJobs = mutableMapOf<String, MonkeyJob>()
    input.forEach { monkeyJobs[it.name] = it }

    var max = Long.MAX_VALUE
    var min = 0L
    var answer: PartTwoResult? = null

    while (answer == null) {
      val mid = max - (max - min) / 2

      val results = listOf(min, mid, max).map { yellValue ->
        monkeyJobs[HUMAN_NAME] = MonkeyJob.YellNumber(HUMAN_NAME, yellValue.toDouble())
        (monkeyJobs[ROOT_NAME] as MonkeyJob.ResultOperation).let { resultOp ->
          val xx = monkeyJobs[resultOp.first]!!.getData(monkeyJobs)
          val yy = monkeyJobs[resultOp.second]!!.getData(monkeyJobs)
          PartTwoResult(yellValue, abs(xx - yy))
        }
      }.sortedBy { it.distanceFromAnswer }

      val maybeResult = results.firstOrNull { it.distanceFromAnswer == 0.0 }
      if (maybeResult != null) {
        answer = maybeResult
      } else {
        val bestTwo = results.take(2)
        min = kotlin.math.min(bestTwo.first().yellNumber, bestTwo.last().yellNumber)
        max = kotlin.math.max(bestTwo.first().yellNumber, bestTwo.last().yellNumber)
      }
    }

    answer.yellNumber
  }

  private fun monkeyJob(line: String): MonkeyJob {
    val mainParts = line.split(": ")
    val monkeyName = mainParts[0]
    val remainingParts = mainParts[1].split(" ")
    return if (remainingParts.size == 3) {
      val firstMonkey = remainingParts[0]
      val secondMonkey = remainingParts[2]
      val operation: (Double, Double) -> Double = when (remainingParts[1]) {
        "+" -> Double::plus
        "*" -> Double::times
        "-" -> Double::minus
        "/" -> Double::div
        else -> throw IllegalArgumentException("Unknown operation ${remainingParts[1]}")
      }

      MonkeyJob.ResultOperation(monkeyName, firstMonkey, secondMonkey, operation)
    } else {
      MonkeyJob.YellNumber(monkeyName, remainingParts[0].toDouble())
    }
  }

  sealed class MonkeyJob(val name: String) {
    abstract fun getData(monkeyJobs: MutableMap<String, MonkeyJob>): Double

    class YellNumber(name: String, val number: Double) : MonkeyJob(name) {
      override fun getData(monkeyJobs: MutableMap<String, MonkeyJob>): Double = number
    }

    class ResultOperation(
      name: String,
      val first: String,
      val second: String,
      val operation: (Double, Double) -> Double
    ) : MonkeyJob(name) {
      override fun getData(monkeyJobs: MutableMap<String, MonkeyJob>): Double = operation(
        monkeyJobs[first]!!.getData(monkeyJobs),
        monkeyJobs[second]!!.getData(monkeyJobs)
      )
    }
  }

  data class PartTwoResult(val yellNumber: Long, val distanceFromAnswer: Double)

  companion object {
    private const val HUMAN_NAME = "humn"
    private const val ROOT_NAME = "root"
  }
}
