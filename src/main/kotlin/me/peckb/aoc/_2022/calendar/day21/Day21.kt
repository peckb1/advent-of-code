package me.peckb.aoc._2022.calendar.day21

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalArgumentException
import java.math.BigDecimal

class Day21 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::monkeyJob) { input ->
    val monkeyJobs = mutableMapOf<String, MonkeyJob>()
    input.forEach { monkeyJobs[it.name] = it }

    monkeyJobs["root"]?.getData(monkeyJobs)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::monkeyJob) { input ->
    val monkeyJobs = mutableMapOf<String, MonkeyJob>()
    input.forEach {
      monkeyJobs[it.name] = it
    }

    val root = (monkeyJobs["root"] as MonkeyJob.ResultOperation).let {
      MonkeyJob.ResultOperation("root", it.first, it.second) { a, b ->
        a.compareTo(b).toBigDecimal()
      }
    }.also { monkeyJobs["root"] = it }

    var max = Long.MAX_VALUE
    var min = 0L

    var answer: List<Any?>? = null
    while (answer == null) {
      println("$min -> $max")
      val mid = max - (max - min) / 2

      val results = listOf(min, mid, max).map { exp ->
        monkeyJobs["humn"] = MonkeyJob.YellNumber("humn", exp.toBigDecimal())
        monkeyJobs["root"]!!.let { root ->
          (monkeyJobs["root"]!! as MonkeyJob.ResultOperation).let { resultOp ->
            val xx = monkeyJobs[resultOp.first]?.getData(monkeyJobs)!!
            val yy = monkeyJobs[resultOp.second]?.getData(monkeyJobs)!!
            listOf<Any>((xx - yy).abs(), exp, xx, yy, xx.compareTo(yy))
          }
        }
      }.sortedBy { it.first() as BigDecimal }

      val maybeResult = results.firstOrNull() { it.last() == 0 }
      if (maybeResult != null) {
        answer = maybeResult
      } else {
        val bestTwo = results.take(2)
        min = kotlin.math.min(bestTwo.first()[1] as Long, bestTwo.last()[1] as Long)
        max = kotlin.math.max(bestTwo.first()[1] as Long, bestTwo.last()[1] as Long)
      }
    }

    answer[1]
  }

  private fun monkeyJob(line: String): MonkeyJob {
    // root: pppw + sjmn
    // dbpl: 5
    val mainParts = line.split(": ")
    val monkeyName = mainParts[0]
    val remainingParts = mainParts[1].split(" ")
    if (remainingParts.size == 3) {
      val firstMonkey = remainingParts[0]
      val secondMonkey = remainingParts[2]
      val operation: (BigDecimal, BigDecimal) -> BigDecimal = when (remainingParts[1]) {
        "+" -> BigDecimal::plus // { a, b -> a + b }
        "*" -> BigDecimal::times // { a, b -> a * b }
        "-" -> BigDecimal::minus // { a, b -> a - b }
        "/" -> BigDecimal::div // { a, b -> a / b }
        else -> throw IllegalArgumentException("Unknown operation ${remainingParts[1]}")
      }

      return MonkeyJob.ResultOperation(monkeyName, firstMonkey, secondMonkey, operation)
    } else {
      return MonkeyJob.YellNumber(monkeyName, remainingParts[0].toBigDecimal())
    }
  }

  sealed class MonkeyJob(val name: String) {
    abstract fun getData(monkeyJobs: MutableMap<String, MonkeyJob>): BigDecimal

    class YellNumber(name: String, val number: BigDecimal) : MonkeyJob(name) {
      override fun getData(monkeyJobs: MutableMap<String, MonkeyJob>): BigDecimal {
        return number
      }

      override fun toString(): String {
        return "$name yelling $number"
      }
    }

    class ResultOperation(
      name: String,
      val first: String,
      val second: String,
      val operation: (BigDecimal, BigDecimal) -> BigDecimal
    ) : MonkeyJob(name) {
      override fun getData(monkeyJobs: MutableMap<String, MonkeyJob>): BigDecimal {
        val a = monkeyJobs[first]!!.getData(monkeyJobs)
        val b = monkeyJobs[second]!!.getData(monkeyJobs)
        return operation(a, b)
      }

      override fun toString(): String {
        return "$name yelling $first $operation $second"
      }
    }
  }
}


