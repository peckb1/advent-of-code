package me.peckb.aoc.generators.skeleton

import java.nio.file.Files
import java.nio.file.Paths

object Component {
  fun alterTestDayComponent(year: String, day: String) {
    val path = Paths.get("${SkeletonGenerator.TEST_DIRECTORY}/kotlin/me/peckb/aoc/_$year/TestDayComponent.kt")
    val content = String(Files.readAllBytes(path))
      .replace("}".toRegex(), "  fun inject(day${day}Test: Day${day}Test)\n}")
      .replace("import javax.inject.Singleton".toRegex(), "import me.peckb.aoc._$year.calendar.day$day.Day${day}Test\nimport javax.inject.Singleton")
    Files.write(path, content.toByteArray())
  }
}
