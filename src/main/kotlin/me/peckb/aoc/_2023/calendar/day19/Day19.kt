package me.peckb.aoc._2023.calendar.day19

import me.peckb.aoc._2023.calendar.day19.Day19.Rule.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max
import kotlin.math.min

class Day19 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (partData, workflowData) = input.toList().partition { it.startsWith('{') }

    val workflows = workflowData.dropLast(1).map(::workflow).associateBy { it.name }
    val parts = partData.map(::xmas)

    println(ValidXmas().allowed())

    parts.filter { workflows.accept(it) }.sumOf { it.value }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (_, workflowData) = input.toList().partition { it.startsWith('{') }

    val workflows = workflowData.dropLast(1).map(::workflow).associateBy { it.name }

    val nodes: MutableMap<String, WorkflowNode> = mutableMapOf<String, WorkflowNode>().apply {
      put("A", WorkflowNode(Workflow("A", emptyList())))
      put("R", WorkflowNode(Workflow("R", emptyList())))
    }

    workflows.values.forEach { nodes[it.name] = WorkflowNode(it) }
    workflows.values.forEach { workflow ->
      workflow.rules.forEach { rule ->
        when (rule) {
          is GreaterThan -> nodes[rule.ifTrue]?.addParent(workflow) ?: throw IllegalStateException("Unknown child ${rule.ifTrue}")
          is LessThan    -> nodes[rule.ifTrue]?.addParent(workflow) ?: throw IllegalStateException("Unknown child ${rule.ifTrue}")
          is Static      -> nodes[rule.result]?.addParent(workflow) ?: throw IllegalStateException("Unknown child ${rule.result}")
        }
      }
    }

    countData(nodes)
  }

  private fun countData(nodes: MutableMap<String, WorkflowNode>): Long {
    val acceptedNode = nodes["A"]!!

    val parents = acceptedNode.getParents()

    val validXmas = ValidXmas()

    // 169536000000000
    // 167409079868000

    val results = parents.map { countData(nodes, acceptedNode, it, validXmas) }

    return combine(results).allowed()
  }

  private fun countData(
    nodes: MutableMap<String, WorkflowNode>,
    childNode: WorkflowNode,
    parent: Workflow,
    validXmas: ValidXmas,
  ) : RangedValidXmas {

//    println("Taking into account ${childNode.workflow.name} being called from ${parent.name}")

    val myValidData = parent.rules.firstOrNull { rule ->
      when (rule) {
        is GreaterThan -> rule.ifTrue == childNode.workflow.name//validXmas.adjustGreaterThan(rule.variableName, rule.value)
        is LessThan    -> rule.ifTrue == childNode.workflow.name//validXmas.adjustLessThan(rule.variableName, rule.value)
        is Static      -> rule.result == childNode.workflow.name//validXmas
      }
    }?.let { rule ->
      when (rule) {
        is GreaterThan -> validXmas.adjustGreaterThan(rule.variableName, rule.value)
        is LessThan    -> validXmas.adjustLessThan(rule.variableName, rule.value)
        is Static      -> validXmas.copy()
      }
    } ?: validXmas

    return if (parent.name == "in") {
      RangedValidXmas.from(myValidData)
    } else {
      val parentWorkflowNode = nodes[parent.name]!!
      val grandParents = parentWorkflowNode.getParents()

      val results = grandParents.map { countData(nodes, parentWorkflowNode, it, myValidData) }

      combine(results)
//      results.fold(validXmas) { acc, next ->
//        ValidXmas(
//          xMin = max(acc.xMin, next.xMin),
//          xMax = min(acc.xMax, next.xMax),
//          mMin = max(acc.mMin, next.mMin),
//          mMax = min(acc.mMax, next.mMax),
//          aMin = max(acc.aMin, next.aMin),
//          aMax = min(acc.aMax, next.aMax),
//          sMin = max(acc.sMin, next.sMin),
//          sMax = min(acc.sMax, next.sMax),
//        )
//      }
    }
  }

  private fun combine(results: List<RangedValidXmas>): RangedValidXmas {
    return results.fold(RangedValidXmas()) { acc, next ->
      acc.and(next)
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
    val (x, m, a, s) = line.substring(1, line.length - 1).split(',').map {
      it.drop(2).toInt()
    }

    return XMAS(x, m, a, s)
  }

  data class XMAS(val x: Int, val m: Int, val a: Int, val s: Int) {
    val value = (x + m + a + s).toLong()

    fun valueOf(variableName: Char): Int {
      return when (variableName) {
        'x' -> x
        'm' -> m
        'a' -> a
        's' -> s
        else -> throw IllegalArgumentException("Unknown Variable Name: $variableName")
      }
    }
  }

  data class ValidXmas(
    val xMin: Int = 0,
    val xMax: Int = 4001,
    val mMin: Int = 0,
    val mMax: Int = 4001,
    val aMin: Int = 0,
    val aMax: Int = 4001,
    val sMin: Int = 0,
    val sMax: Int = 4001,
  ) {
    fun adjustGreaterThan(variableName: Char, value: Int): ValidXmas {
      return when (variableName) {
        'x' -> copy(xMin = max(xMin, value))
        'm' -> copy(mMin = max(mMin, value))
        'a' -> copy(aMin = max(aMin, value))
        's' -> copy(sMin = max(sMin, value))
        else -> throw IllegalArgumentException("Unknown variable: $variableName")
      }
    }

    fun adjustLessThan(variableName: Char, value: Int): ValidXmas {
      return when (variableName) {
        'x' -> copy(xMax = min(xMax, value))
        'm' -> copy(mMax = min(mMax, value))
        'a' -> copy(aMax = min(aMax, value))
        's' -> copy(sMax = min(sMax, value))
        else -> throw IllegalArgumentException("Unknown variable: $variableName")
      }
    }

    fun allowed(): Long {
      return ((xMin + 1) until xMax).count().toLong() *
        ((mMin + 1) until mMax).count().toLong() *
        ((aMin + 1) until aMax).count().toLong() *
        ((sMin + 1) until sMax).count().toLong()
    }
  }

  data class RangedValidXmas(
    val xRanges: List<IntRange> = emptyList(),
    val mRanges: List<IntRange> = emptyList(),
    val aRanges: List<IntRange> = emptyList(),
    val sRanges: List<IntRange> = emptyList(),
  ) {
    fun allowed(): Long {
      TODO("Not yet implemented")
    }

    fun and(other: RangedValidXmas): RangedValidXmas {
      val newXRanges = combine(xRanges, other.xRanges)
      val newMRanges = combine(mRanges, other.mRanges)
      val newARanges = combine(aRanges, other.aRanges)
      val newSRanges = combine(sRanges, other.sRanges)

      return RangedValidXmas(newXRanges, newMRanges, newARanges, newSRanges)
    }

    private fun combine(a: List<IntRange>, b: List<IntRange>): List<IntRange> {
      val newRanges = a.plus(b).toMutableList()
      do {
        val madeACombination = newRanges.windowed(2).any { (range1, range2) ->
          val minInside = range1.contains(range2.first)
          val maxInside = range1.contains(range2.last)

          if (minInside && maxInside) {
            newRanges.remove(range2)
            true
          } else if (minInside) {
            newRanges.remove(range1)
            newRanges.remove(range2)
            newRanges.add(range1.first .. range2.last)
            true
          } else if (maxInside) {
            newRanges.remove(range1)
            newRanges.remove(range2)
            newRanges.add(range2.first .. range1.last)
            true
          } else {
            false
          }
        }
      } while(madeACombination)

      return newRanges
    }

    companion object {
      fun from(validXmas: ValidXmas): RangedValidXmas {
        return RangedValidXmas(
          listOf(validXmas.xMin..validXmas.xMax),
          listOf(validXmas.mMin..validXmas.mMax),
          listOf(validXmas.aMin..validXmas.aMax),
          listOf(validXmas.sMin..validXmas.sMax),
        )
      }
    }
  }

  sealed interface Rule {
    fun apply(xmas: XMAS): String?

    class GreaterThan(val variableName: Char, val value: Int, val ifTrue: String) : Rule {
      override fun apply(xmas: XMAS) = ifTrue.takeIf { xmas.valueOf(variableName) > value }
    }

    class LessThan(val variableName: Char, val value: Int, val ifTrue: String) : Rule {
      override fun apply(xmas: XMAS) = ifTrue.takeIf { xmas.valueOf(variableName) < value }
    }

    class Static(val result: String) : Rule {
      override fun apply(xmas: XMAS) = result
    }
  }

  data class Workflow(val name: String, val rules: List<Rule>)

  data class WorkflowNode(val workflow: Workflow) {
    private val parents = mutableListOf<Workflow>()

    fun addParent(workflow: Workflow) {
      parents.add(workflow)
    }

    fun getParents(): List<Workflow> {
      return parents
    }
  }

  private fun Map<String, Workflow>.accept(part: XMAS): Boolean {
    var nextWorkflow = "in"
    while (nextWorkflow != "A" && nextWorkflow != "R") {
      nextWorkflow = get(nextWorkflow)!!.rules.firstNotNullOf { it.apply(part) }
    }

    return nextWorkflow == "A"
  }
}

