package me.peckb.aoc._2015.calendar.day02

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day02 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::box) { input ->
    input.sumOf { it.paperRequired }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::box) { input ->
    input.sumOf { it.wrapRibbon + it.bowRibbon }
  }

  private fun box(line: String) = line.split("x")
    .map { it.toInt() }
    .let { (h, w, l) -> Box(h, w, l) }

  data class Box(private val h: Int, private val w: Int, private val l: Int) {
    val area = (2*l*w) + (2*w*h) + (2*h*l)

    val paperRequired = area + minOf(l*w, w*h, l*h)

    val wrapRibbon = listOf(h, w, l).sorted().take(2).sumOf { 2 * it }

    val bowRibbon = h * w * l
  }
}
