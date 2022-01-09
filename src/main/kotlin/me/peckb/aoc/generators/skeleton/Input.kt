package me.peckb.aoc.generators.skeleton

import java.nio.file.Files
import java.nio.file.Paths

object Input {
  fun generateInput(year: String, day: String) {
    val path = Paths.get("${SkeletonGenerator.TEST_DIRECTORY}/resources/$year/day$day.input")
    Files.write(path, "".toByteArray())
  }
}
