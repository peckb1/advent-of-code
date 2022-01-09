package me.peckb.aoc.generators.skeleton

class SkeletonGenerator(private val year: String, private val day: String) {
  companion object {
    const val SRC_DIRECTORY = "src/main/kotlin"
    const val TEST_DIRECTORY = "src/test"
  }

  fun generateAdventSkeleton() {
    DayClass.generateDayClass(year, day)
    Readme.generateREADME(year, day)
    TestClass.generateTestClass(year, day)
    Input.generateInput(year, day)
    Component.alterTestDayComponent(year, day)
  }
}
