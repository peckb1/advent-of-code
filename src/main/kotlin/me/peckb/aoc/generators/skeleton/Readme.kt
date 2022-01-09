package me.peckb.aoc.generators.skeleton

import java.nio.file.Files
import java.nio.file.Paths

object Readme {
  fun generateREADME(year: String, day: String) {
    val path = Paths.get("${SkeletonGenerator.SRC_DIRECTORY}/me/peckb/aoc/_$year/calendar/day$day/README.md")
    val content = "## [](https://adventofcode.com/$year/day/${day.toInt()})"
    Files.write(path, content.toByteArray())
  }
}
