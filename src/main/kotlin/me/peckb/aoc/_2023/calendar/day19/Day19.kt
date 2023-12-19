package me.peckb.aoc._2023.calendar.day19

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day19 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (partData, workflowData) = input.toList().partition { it.startsWith('{') }

    val workflows = workflowData.dropLast(1).map(::workflow).associateBy { it.name }
    val parts = partData.map(::xmas)

    parts.filter { workflows.accept(it) }.sumOf { it.value }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (_, workflowData) = input.toList().partition { it.startsWith('{') }

    val workflows = workflowData.dropLast(1).map(::workflow).associateBy { it.name }

  }

  private fun workflow(line: String): Workflow {
    // ex{x>10:one,m<20:two,a>30:R,A}
    val name = line.takeWhile { it != '{' }
    val rules = line.substring(name.length + 1, line.length - 1)
      .split(",")
      .map { ruleString ->
        if (ruleString.contains(':')) {
          // we have a condition then an "answer"
          val (check, ifTrue) = ruleString.split(':')

          when (val operation = check[1]) {
            '>' -> Rule.greaterThan(check[0], check.substring(2).toInt(), ifTrue)
            '<' -> Rule.lessThan(check[0], check.substring(2).toInt(), ifTrue)
            else -> throw IllegalArgumentException("Unknown operation $operation")
          }
        } else {
          // we just have an answer
          Rule.static(ruleString)
        }
      }

    return Workflow(name, rules)
  }

  private fun xmas(line: String): XMAS {
    val (x, m, a, s) = line.substring(1, line.length - 1).split(',').map {
      it.drop(2).toInt()
    }

    return XMAS(x, m, a, s)
  }

  data class XMAS(val x: Int, val m: Int, val a: Int, val s: Int) {
    val value = (x + m + a + s).toLong()

    fun valueOf(variableName: Char) : Int {
      return when (variableName) {
        'x' -> x
        'm' -> m
        'a' -> a
        's' -> s
        else -> throw IllegalArgumentException("Unknown Variable Name: $variableName")
      }
    }
  }

  interface Rule {
    fun apply(xmas: XMAS): String?

    companion object {
      fun greaterThan(variableName: Char, value: Int, ifTrue: String) = object : Rule {
        override fun apply(xmas: XMAS) = ifTrue.takeIf { xmas.valueOf(variableName) > value }
      }

      fun lessThan(variableName: Char, value: Int, ifTrue: String) = object : Rule {
        override fun apply(xmas: XMAS) = ifTrue.takeIf { xmas.valueOf(variableName) < value }
      }

      fun static(result: String) = object : Rule {
        override fun apply(xmas: XMAS) = result
      }
    }
  }

  data class Workflow(val name: String, val rules: List<Rule>)

  private fun Map<String, Workflow>.accept(part: XMAS): Boolean {
    var nextWorkflow = "in"
    while(nextWorkflow != "A" && nextWorkflow != "R") {
      nextWorkflow = get(nextWorkflow)!!.rules.firstNotNullOf { it.apply(part) }
    }

    return nextWorkflow == "A"
  }
}

