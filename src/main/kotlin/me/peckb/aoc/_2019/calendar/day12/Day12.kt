package me.peckb.aoc._2019.calendar.day12

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import org.apache.commons.math3.util.ArithmeticUtils.gcd
import kotlin.math.abs

class Day12 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::position) { input ->
    val moons = input.toList()

    repeat(1000) {
      tick(moons)
    }

    moons.sumOf { it.totalEnergy }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::position) { input ->
    val moons = input.toList()

    val startingMoons = moons.map { it.copy() }
    val startingX = startingMoons.map { it.x }
    val startingY = startingMoons.map { it.y }
    val startingZ = startingMoons.map { it.z }
    val startingXVel = startingMoons.map { it.xVel }
    val startingYVel = startingMoons.map { it.yVel }
    val startingZVel = startingMoons.map { it.zVel }

    var xLoop: Long? = null
    var yLoop: Long? = null
    var zLoop: Long? = null

    var tickCount = 0L
    while (xLoop == null || yLoop == null || zLoop == null) {
      tick(moons)
      tickCount++
      if (xLoop == null && moons.map { it.x } == startingX && moons.map { it.xVel } == startingXVel) {
        xLoop = tickCount
      }
      if (yLoop == null && moons.map { it.y } == startingY && moons.map { it.yVel } == startingYVel) {
        yLoop = tickCount
      }
      if (zLoop == null && moons.map { it.z } == startingZ && moons.map { it.zVel } == startingZVel) {
        zLoop = tickCount
      }
    }

    lcm(xLoop, yLoop, zLoop)
  }

  private fun lcm(a: Long, b: Long, c: Long): Long {
    val x = (a * b) / gcd(a, b)
    return (x * c) / gcd(x, c)
  }

  private fun position(line: String) = line
    .drop(1)
    .dropLast(1)
    .split(",")
    .let { (xData, yData, zData) ->
      Moon(
        x = xData.split("=").last().toInt(),
        y = yData.split("=").last().toInt(),
        z = zData.split("=").last().toInt()
      )
    }

  private fun tick(moons: List<Moon>) {
    // apply gravity to each moon
    moons.forEachIndexed { index, moonA ->
      ((index + 1) until moons.size).map { moons[it] }.map { moonB ->
        moonA.adjustVelocity(moonB)
        moonB.adjustVelocity(moonA)
      }
    }

    // update the position of each moon
    moons.forEach { it.move() }
  }

  data class Moon(
    var x: Int,
    var y: Int,
    var z: Int,
    var xVel: Int = 0,
    var yVel: Int = 0,
    var zVel: Int = 0,
  ) {
    val totalEnergy get() = potentialEnergy * kineticEnergy

    private val potentialEnergy get() = abs(x) + abs(y) + abs(z)

    private val kineticEnergy get() = abs(xVel) + abs(yVel) + abs(zVel)

    fun adjustVelocity(moon: Moon) {
      adjustXVel(moon.x.compareTo(x))
      adjustYVel(moon.y.compareTo(y))
      adjustZVel(moon.z.compareTo(z))
    }

    fun move() {
      x += xVel
      y += yVel
      z += zVel
    }

    private fun adjustXVel(deltaV: Int) { xVel += deltaV }

    private fun adjustYVel(deltaV: Int) { yVel += deltaV }

    private fun adjustZVel(deltaV: Int) { zVel += deltaV }
  }
}
