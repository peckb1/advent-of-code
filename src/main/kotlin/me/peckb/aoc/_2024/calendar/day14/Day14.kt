package me.peckb.aoc._2024.calendar.day14

import arrow.core.Tuple4
import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.io.File
import kotlin.math.sign

class Day14 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::day14) { input ->
    val timeToElapse = 100L
    // note data is [X, Y] format!
    // The robots outside the actual bathroom are in a space which is 101 tiles wide and 103 tiles tall
    val robotLocations = input.map { r ->
      var robotXAfter = (r.posX + (r.velX * timeToElapse)) % SPACE_WIDTH
      var robotYAfter = (r.posY + (r.velY * timeToElapse)) % SPACE_HEIGHT

      if (robotXAfter.sign < 0) robotXAfter += SPACE_WIDTH
      if (robotYAfter.sign < 0) robotYAfter += SPACE_HEIGHT

      robotXAfter to robotYAfter
    }.toList()

    val robotsByQuadrant = robotLocations.groupBy { robotLocation ->
      if (robotLocation.first < SPACE_WIDTH/2) {
        if (robotLocation.second < SPACE_HEIGHT/2) { 1 }
        else if (robotLocation.second > SPACE_HEIGHT/2) { 3 }
        else { 0 }
      } else if (robotLocation.first > SPACE_WIDTH/2) {
        if (robotLocation.second < SPACE_HEIGHT/2) { 2 }
        else if (robotLocation.second > SPACE_HEIGHT/2) { 4 }
        else { 0 }
      } else { 0 }
    }

    robotsByQuadrant.entries.fold(1L) { acc, entry ->
      if (entry.key != 0) {
        acc * entry.value.size
      } else {
        acc
      }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::day14) { input ->
    val space = Array(SPACE_HEIGHT) { Array(SPACE_WIDTH) { 0 } }
    val robots = input.toList()
    robots.forEach { space[it.posY][it.posX]++ }

    var counter = 0
    loop@ while(counter < 10_000) {
      counter++
      robots.forEach { robot ->
        space[robot.posY][robot.posX]--
        var newX = robot.posX + robot.velX
        var newY = robot.posY + robot.velY

        if      (newX < 0)             { newX += SPACE_WIDTH }
        else if (newX >= SPACE_WIDTH)  { newX -= SPACE_WIDTH }

        if      (newY < 0)             { newY += SPACE_HEIGHT }
        else if (newY >= SPACE_HEIGHT) { newY -= SPACE_HEIGHT }

        robot.posX = newX
        robot.posY = newY
        space[robot.posY][robot.posX]++
      }

      val foundTopBorder = space.withIndex().all { (index, row) ->
        val rowStr = row.map {
          when (it) {
            0 -> " "
            else -> "*"
          }
        }.joinToString("")

        Tree.IMAGE[index] == rowStr
      }

      if (foundTopBorder) break@loop
    }
    counter
  }

  private fun day14(line: String): Robot {
    return line.split(" ")
      .map { it.drop(2).split(",") }
      .let { (pos, vel) -> pos.map { it.toInt() } to vel.map { it.toInt() } }
      .let { (pos, vel) -> Robot(pos[0], pos[1], vel[0], vel[1]) }
  }

  companion object {
    private const val SPACE_WIDTH = 101
    private const val SPACE_HEIGHT = 103
  }
}

data class Robot(var posX: Int, var posY: Int, val velX: Int, val velY: Int)
