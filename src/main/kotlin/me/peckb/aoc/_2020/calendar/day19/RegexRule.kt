package me.peckb.aoc._2020.calendar.day19

class RegexRule(
  val id: Int,
  private val part: Int,
  private val rules: Map<Int, RegexRule>,
  private val ruleData: String,
) {
  val regex: String by lazy {
    if (ruleData.contains('"')) {
      ruleData.substringAfter('"').substringBeforeLast('"')
    } else {
      if (part == 2 && id == 8) { // swap for 42 | 42 8
        // since 42 keeps happening forever, we can regex that with a `+` at the end
        "${rules[42]?.regex}+"
      } else if (part == 2 && id == 11) { // swap 42 31 | 42 11 31
        val r42 = rules[42]?.regex!!
        val r31 = rules[31]?.regex!!
        // I don't know how to regex infinitely from the center so ... just hope five
        // times is enough!
        val inner = (1..5).joinToString("|") { n ->
          "(${r42.repeat(n)}${r31.repeat(n)})"
        }
        "($inner)"
      } else {
        val ruleOptions = ruleData.split(" | ")
        val ruleOptionIds = ruleOptions.map { it.split(" ").map { id -> id.toInt() } }

        val ruleValues = ruleOptionIds.map { requiredRuleIds ->
          requiredRuleIds.joinToString("") { rules.getOrDefault(it, EMPTY_RULE).regex }
        }

        "(${ruleValues.joinToString("|")})"
      }
    }
  }

  companion object {
    private val EMPTY_RULE = RegexRule(-1, 0, emptyMap(), """ "" """)
  }
}
