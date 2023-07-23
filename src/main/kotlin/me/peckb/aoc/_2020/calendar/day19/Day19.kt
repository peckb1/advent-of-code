package me.peckb.aoc._2020.calendar.day19

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day19 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  class Rule(
    val id: Int,
    private val rules: Map<Int, Rule>,
    private val ruleData: String,
  ) {
    val ruleValues: List<String> by lazy {
      if (ruleData.contains('"')) {
        listOf(
          ruleData.substringAfter('"').substringBeforeLast('"')
        )
      } else {
        val ruleOptions = ruleData.split(" | ")
        val ruleOptionIds = ruleOptions.map { it.split(" ").map { id -> id.toInt() } }
        val ruleValues = ruleOptionIds.flatMap { requiredRuleIds ->
          val childRules = requiredRuleIds.map {
            rules.getOrDefault(it, EMPTY_RULE).ruleValues
          }

          val childRuleCombinations = childRules.foldRight(listOf("")) { newList, accumulator ->
            newList.flatMap { head -> accumulator.map { "$head$it" } }
          }

          childRuleCombinations
        }
        ruleValues
      }
    }

    override fun equals(other: Any?): Boolean {
      return (other is Rule) && other.id == id
    }

    override fun hashCode(): Int {
      return id.hashCode()
    }

    companion object {
      private val EMPTY_RULE = Rule(-1, emptyMap(), """ "" """)
    }
  }

  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val data = input.toList()
    val rules = data.takeWhile { it.isNotBlank() }
    val messages = data.drop(rules.size + 1)

    val allRules = mutableMapOf<Int, Rule>()

    rules.forEach {
      val (id, ruleData) = it.split(": ")

      Rule(id.toInt(), allRules, ruleData)
        .also { newRule -> allRules[newRule.id] = newRule }
    }

    val idZeroRules = allRules[0]?.ruleValues ?: emptyList()

    messages.count { idZeroRules.contains(it) }
  }

  class RegexRule(
    val id: Int,
    private val rules: Map<Int, RegexRule>,
    private val ruleData: String,
  ) {
    val regex: String by lazy {
      if (ruleData.contains('"')) {
        ruleData.substringAfter('"').substringBeforeLast('"')
      } else {
        if (id == 8) { // swap for 42 | 42 8
          "${rules[42]?.regex}+"
        } else if (id == 11) { // swap 42 31 | 42 11 31
          val r42 = rules[42]?.regex!!
          val r31 = rules[31]?.regex!!
          val inner = (1..5).joinToString("|") { n ->
            "(${r42.repeat(n)}${r31.repeat(n)})"
          }
          "($inner)"
        } else {

          val ruleOptions = ruleData.split(" | ")
          val ruleOptionIds = ruleOptions.map { it.split(" ").map { id -> id.toInt() } }

          val ruleValues = ruleOptionIds.map { requiredRuleIds ->
            val childRules = requiredRuleIds.map {
              rules.getOrDefault(it, EMPTY_RULE).regex
            }

            val childRuleCombination = childRules.fold("") { acc, next -> acc + next }

            childRuleCombination
          }

          "(${ruleValues.joinToString("|")})"
        }
      }
    }

    override fun equals(other: Any?): Boolean {
      return (other is Rule) && other.id == id
    }

    override fun hashCode(): Int {
      return id.hashCode()
    }

    companion object {
      private val EMPTY_RULE = RegexRule(-1, emptyMap(), """ "" """)
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val data = input.toList()
    val rules = data.takeWhile { it.isNotBlank() }
    val messages = data.drop(rules.size + 1)

    val allRules = mutableMapOf<Int, RegexRule>()

    rules.forEach {
      val (id, ruleData) = it.split(": ")
      RegexRule(id.toInt(), allRules, ruleData)
        .also { newRule -> allRules[newRule.id] = newRule }
    }

    val idZeroRules = allRules[0]?.regex?.toRegex()!!

    messages.count { idZeroRules.matches(it) }
  }
}
