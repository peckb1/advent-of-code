package me.peckb.aoc._2022.calendar.day21

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

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
        monkeyJobs[HUMAN_NAME] = MonkeyJob.YellNumber(HUMAN_NAME, yellValue.toBigDecimal())
        (monkeyJobs[ROOT_NAME] as MonkeyJob.ResultOperation).let { resultOp ->
          val xx = monkeyJobs[resultOp.first]?.getData(monkeyJobs)!!
          val yy = monkeyJobs[resultOp.second]?.getData(monkeyJobs)!!
          PartTwoResult(yellValue, (xx - yy).abs())
        }
      }.sortedBy { it.distanceFromAnswer }

      val maybeResult = results.firstOrNull { it.distanceFromAnswer == ZERO }
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
      val operation: (BigDecimal, BigDecimal) -> BigDecimal = when (remainingParts[1]) {
        "+" -> BigDecimal::plus
        "*" -> BigDecimal::times
        "-" -> BigDecimal::minus
        "/" -> BigDecimal::div
        else -> throw IllegalArgumentException("Unknown operation ${remainingParts[1]}")
      }

      MonkeyJob.ResultOperation(monkeyName, firstMonkey, secondMonkey, operation)
    } else {
      MonkeyJob.YellNumber(monkeyName, remainingParts[0].toBigDecimal())
    }
  }

  sealed class MonkeyJob(val name: String) {
    abstract fun getData(monkeyJobs: MutableMap<String, MonkeyJob>): BigDecimal

    class YellNumber(name: String, val number: BigDecimal) : MonkeyJob(name) {
      override fun getData(monkeyJobs: MutableMap<String, MonkeyJob>): BigDecimal = number
    }

    class ResultOperation(
      name: String,
      val first: String,
      val second: String,
      val operation: (BigDecimal, BigDecimal) -> BigDecimal
    ) : MonkeyJob(name) {
      override fun getData(monkeyJobs: MutableMap<String, MonkeyJob>): BigDecimal = operation(
        monkeyJobs[first]!!.getData(monkeyJobs),
        monkeyJobs[second]!!.getData(monkeyJobs)
      )
    }
  }

  data class PartTwoResult(val yellNumber: Long, val distanceFromAnswer: BigDecimal)

  companion object {
    private const val HUMAN_NAME = "humn"
    private const val ROOT_NAME = "root"
  }
}


