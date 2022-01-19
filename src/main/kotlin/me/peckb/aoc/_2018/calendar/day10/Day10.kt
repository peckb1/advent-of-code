package me.peckb.aoc._2018.calendar.day10

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.Int.Companion.MAX_VALUE
import kotlin.Int.Companion.MIN_VALUE
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day10 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::day10) { input ->
    val points = input.toList()
    val (area, _) = findWord(points)
    area.joinToString("\n") { it.joinToString("") }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::day10) { input ->
    val points = input.toList()
    val (_, time) = findWord(points)
    time
  }

  private fun day10(line: String): Point {
    // position=< 9,  1> velocity=< 0,  2>
    val (x, y) = line.substringBefore("> velocity")
      .substringAfter("<")
      .split(",")
      .map { it.trim() }
      .map { it.toInt() }
    val (xVel, yVel) = line.substringAfter("velocity=<")
      .dropLast(1)
      .split(",")
      .map { it.trim() }
      .map { it.toInt() }

    return Point(x, y, xVel, yVel)
  }

  private fun findWord(points: List<Point>): Pair<Array<Array<Char>>, Int> {
    var currentDistanceSum = points.findDistance()
    var lastDistanceSum = currentDistanceSum + 1
    var time = 0

    while(currentDistanceSum < lastDistanceSum) {
      points.forEach { it.tick() }
      lastDistanceSum = currentDistanceSum
      currentDistanceSum = points.findDistance()
      time++
    }

    var maxX = MIN_VALUE
    var maxY = MIN_VALUE
    var minY = MAX_VALUE
    var minX = MAX_VALUE

    points.forEach {
      it.tickBackwards()
      maxX = max(maxX, it.x)
      maxY = max(maxY, it.y)
      minX = min(minX, it.x)
      minY = min(minY, it.y)
    }

    val area = Array(maxY - minY + 1) { Array (maxX - minX + 1) { '.' } }
    points.forEach { area[it.y - minY][it.x - minX] = '#' }

    return area to (time - 1)
  }

  private fun List<Point>.findDistance(): Long {
    return sumOf { p1 ->
      val distanceSums = sumOf { p2 -> p1.distanceFrom(p2) }
      val average = distanceSums / size
      average
    }
  }

  data class Point(var x: Int, var y: Int, val xVel: Int, val yVel: Int) {
    fun tick() {
      x += xVel
      y += yVel
    }

    fun tickBackwards() {
      x -= xVel
      y -= yVel
    }

    fun distanceFrom(point: Point): Long {
      return (abs(x - point.x) + abs(y - point.y)).toLong()
    }
  }
}
