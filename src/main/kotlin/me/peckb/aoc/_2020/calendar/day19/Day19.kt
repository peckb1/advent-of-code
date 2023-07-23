package me.peckb.aoc._2020.calendar.day19

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day19 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    run(input, 1)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    run(input, 2)
  }

  private fun run(input: Sequence<String>, part: Int): Int {
    val data = input.toList()
    val rules = data.takeWhile { it.isNotBlank() }
    val messages = data.drop(rules.size + 1)

    val allRules = mutableMapOf<Int, RegexRule>()

    rules.forEach {
      val (id, ruleData) = it.split(": ")
      RegexRule(id.toInt(), part, allRules, ruleData)
        .also { newRule -> allRules[newRule.id] = newRule }
    }

    val idZeroRules = allRules[0]?.regex?.toRegex()!!

    return messages.count { idZeroRules.matches(it) }
  }
}
