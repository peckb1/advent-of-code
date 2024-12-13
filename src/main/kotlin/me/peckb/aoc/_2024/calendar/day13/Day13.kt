package me.peckb.aoc._2024.calendar.day13

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.roundToLong

class Day13 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    input.chunked(4)
      .mapNotNull { (a, b, p, _) -> solve(a, b, p, 0) }
      .sum()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    input.chunked(4)
      .mapNotNull { (a, b, p, _) -> solve(a, b, p, BUFFER) }
      .sum()
  }

  private fun parse(data: String): Pair<Long, Long> {
    return data.split(", ").map{ it.drop(2).toLong() }.let { (x, y) -> x to y }
  }

  private fun solve(a: String, b: String, p: String, buffer: Long): Long? {
    val aSpeed = parse(a.split("A: ").last()).let{ (x, y) -> Speed(x, y) }
    val bSpeed = parse(b.split("B: ").last()).let{ (x, y) -> Speed(x, y) }
    val prize = parse(p.split(": ").last()).let{ (x, y) -> x + buffer to y + buffer }

    return findCost(aSpeed, bSpeed, prize)
  }

  private fun findCost(aSpeed: Speed, bSpeed: Speed, prize: Pair<Long, Long>): Long? {
    val py = prize.second.toDouble()
    val px = prize.first.toDouble()
    val ax = aSpeed.x.toDouble()
    val ay = aSpeed.y.toDouble()
    val bx = bSpeed.x.toDouble()
    val by = bSpeed.y.toDouble()

    // ax*a + bx*b = px (solve for a)
    // ... a = px/ax - bx/ax * b
    // ay*a + by*b = py (replace a with above)
    // ay(px/ax - bx-ax) + by*b = py (solve for b)
    // ... b = (py - ay*(px/ax)) / (by - (ay*bx)/ax)
    val b = ((py - ay * (px/ax)) / (by - (ay*bx)/ax)).roundToLong()
    // replace b in original a = ...
    val a = (px/ax - bx/ax * b).roundToLong()

    // grab our new location
    val loc = (aSpeed.x * a) + (bSpeed.x * b) to (aSpeed.y * a) + (bSpeed.y * b)

    // if we're there, the system is solved!
    return if (loc == prize) { a * A_COST + b * B_COST } else { null }
  }

  companion object {
    private const val BUFFER = 10000000000000

    private const val A_COST = 3
    private const val B_COST = 1
  }
}

data class Speed(val x: Long, val y: Long)
