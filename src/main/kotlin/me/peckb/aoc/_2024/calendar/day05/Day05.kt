package me.peckb.aoc._2024.calendar.day05

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day05 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (rules, updates) = input.parseInput()
    val (validUpdates, _) = validate(rules, updates)

    sumMiddlePages(validUpdates)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (rules, updates) = input.parseInput()
    val (_, invalidUpdates) = validate(rules, updates)

    sumMiddlePages(invalidUpdates.map { it.reSort(rules) })
  }

  private fun Sequence<String>.parseInput(): Pair<Map<Rule, Unit>, List<Update>> {
    val rules = mutableMapOf<Rule, Unit>()
    val updates = mutableListOf<Update>()

    var readingRules = true
    this.forEach { line ->
      if (line.isBlank()) { readingRules = false }
      else {
        if (readingRules) {
          rules[line.split("|").map { it.toInt() }.let{ (b, a) -> Rule(b, a) }] = Unit
        } else {
          updates.add(Update(line.split(",").map { it.toInt() }))
        }
      }
    }

    return rules to updates
  }

  private fun validate(rules: Map<Rule, Unit>, updates: List<Update>): Pair<List<Update>, List<Update>> {
    return updates.partition { update ->
      update.pages.windowed(2).all { (before, after) ->
        rules.containsKey(Rule(before, after))
      }
    }
  }

  private fun sumMiddlePages(updates: List<Update>) = updates.sumOf { it.pages[(it.pages.size / 2)] }
}

data class Rule(val before: Int, val after: Int)

data class Update(val pages: List<Int>) {
  fun reSort(rules: Map<Rule, Unit>) = pages.sortedWith { before, after ->
    if (rules.containsKey(Rule(before, after))) -1 else 1
  }.let(::Update)
}
