package me.peckb.aoc._2024.calendar.day02

import me.peckb.aoc._2024.calendar.day02.Day02.Report.Direction.*
import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.sign

class Day02 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::report) { reports ->
    reports.count { it.isSafe }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::report) { reports ->
    reports.count { it.isActuallySafe() }
  }

  private fun report(line: String) = line
    .split(" ")
    .map { it.toInt() }
    .let(::Report)

  data class Report(val levels: List<Int>) {
    companion object {
      val ALLOWED_RANGE = 1..3
    }

    val direction: Direction = when ((levels[0] - levels[1]).sign) {
      -1   -> INCREASING
       1   -> DECREASING
      else -> NEITHER
    }

    val isSafe get() = levels.windowed(2).all { (a, b) ->
        when (direction) {
          INCREASING -> (b - a) in ALLOWED_RANGE
          DECREASING -> (a - b) in ALLOWED_RANGE
          NEITHER    -> false
        }
      }

    fun isActuallySafe(): Boolean {
      if (isSafe) return true // only need to double-check ones that failed the initial isSafe check

      val mutLevels = levels.toMutableList()

      return levels.indices.any { i -> mutLevels.without(i) { Report(it).isSafe } }
    }

    enum class Direction {
      INCREASING, DECREASING, NEITHER
    }
  }
}

private fun <T> MutableList<Int>.without(index: Int, check: (MutableList<Int>) -> T): T {
  val item = removeAt(index)
  val result = check(this)
  add(index, item)

  return result
}
