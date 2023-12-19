package me.peckb.aoc._2023.calendar.day19

import me.peckb.aoc._2023.calendar.day19.Rule.*
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

    val workflows = workflowData.dropLast(1)
      .plus(listOf("A{}", "R{}"))
      .map(::workflow)
      .associateBy { it.name }

    countAccepted(workflows, workflows["in"]!!)
  }

  private fun countAccepted(
    workflows: Map<String, Workflow>,
    workflow: Workflow,
    validXmas: ValidXmas = ValidXmas(),
  ): Long {
    return when (workflow.name) {
      "A" -> validXmas.allowed()
      "R" -> 0
      else -> {
        var usableXmasSpace = validXmas

        return workflow.rules.sumOf { rule ->
          when (rule) {
            is GreaterThan -> {
              val trueValid = usableXmasSpace.adjustGreaterThan(rule.variableName, rule.value + 1)
              usableXmasSpace = usableXmasSpace.adjustLessThan(rule.variableName, rule.value)

              countAccepted(workflows, workflows[rule.ifTrue]!!, trueValid)
            }

            is LessThan -> {
              val trueValid = usableXmasSpace.adjustLessThan(rule.variableName, rule.value - 1)
              usableXmasSpace = usableXmasSpace.adjustGreaterThan(rule.variableName, rule.value)

              countAccepted(workflows, workflows[rule.ifTrue]!!, trueValid)
            }

            is Static -> {
              countAccepted(workflows, workflows[rule.result]!!, usableXmasSpace)
            }
          }
        }
      }
    }
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
            '>' -> GreaterThan(check[0], check.substring(2).toInt(), ifTrue)
            '<' -> LessThan(check[0], check.substring(2).toInt(), ifTrue)
            else -> throw IllegalArgumentException("Unknown operation $operation")
          }
        } else {
          // we just have an answer
          Static(ruleString)
        }
      }

    return Workflow(name, rules)
  }

  private fun xmas(line: String): XMAS {
    // {x=787,m=2655,a=1222,s=2876}
    val (x, m, a, s) = line.substring(1, line.length - 1)
      .split(',')
      .map { it.drop(2).toInt() }

    return XMAS(x, m, a, s)
  }
}
