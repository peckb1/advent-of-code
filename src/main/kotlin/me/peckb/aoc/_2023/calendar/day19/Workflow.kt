package me.peckb.aoc._2023.calendar.day19

data class Workflow(val name: String, val rules: List<Rule>)

internal fun Map<String, Workflow>.accept(part: XMAS): Boolean {
  var nextWorkflow = "in"
  while (nextWorkflow != "A" && nextWorkflow != "R") {
    nextWorkflow = get(nextWorkflow)!!.rules.firstNotNullOf { it.apply(part) }
  }

  return nextWorkflow == "A"
}