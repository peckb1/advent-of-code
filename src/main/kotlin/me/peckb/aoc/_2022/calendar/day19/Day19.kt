package me.peckb.aoc._2022.calendar.day19

import me.peckb.aoc._2016.calendar.day03.Day03
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min


class Day19 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::blueprint) { input ->
    input.toList().sumOf {
      val max = maxGeode(it, 24, triangle24)
      max * it.id
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::blueprint) { input ->
    input.toList().take(3).fold(1) { acc, blueprint ->
      val max = maxGeode(blueprint, 32, triangle32)
      acc * max
    }
  }

  // o = [ ( t - 1 ) * t // 2 for t in range( 24 + 1 ) ]
  val triangle24 = listOf(0, 0, 1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 66, 78, 91, 105, 120, 136, 153, 171, 190, 210, 231, 253, 276)
  val triangle32 = listOf(0, 0, 1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 66, 78, 91, 105, 120, 136, 153, 171, 190, 210, 231, 253, 276, 300, 325, 351, 378, 406, 435, 465, 496)


  fun maxGeode(blueprint: Blueprint, minutes: Int, triangle: List<Int>): Int {
    var a = blueprint.oreRobotOreCost
    var b = blueprint.clayRobotOreCost
    var c = blueprint.obsidianRobotOreCost
    var d = blueprint.obsidianRobotClayCost
    var e = blueprint.geodeRobotOreCost
    var f = blueprint.geodeRobotObsidianCost

    var mi = maxOf(a, b, c, e)
    var mj = d
    var mk = f

    var m = 0

    fun dfs(
      timeRemaining: Int,
      robot: Robot,
      oreRobots: Int, // i
      clayRobots: Int, // j
      obsidianRobots: Int, // k
      geodeRobots: Int, // l
      ore: Int,
      clay: Int,
      obsidian: Int,
      geodes: Int
    ) {

      var t = timeRemaining
      var w = ore
      var x = clay
      var y = obsidian
      var z = geodes

      if (
        (robot == Robot.ORE && oreRobots >= mi) ||
        (robot == Robot.CLAY && clayRobots >= mj) ||
        (robot == Robot.OBSIDIAN && (obsidianRobots >= mk || clayRobots == 0 )) ||
        (robot == Robot.GEODE && obsidianRobots == 0) ||
        (z + geodeRobots * t + triangle[ t ] <= m)
      ) {
        return
      }

      while (t > 0) {
        if (robot == Robot.ORE && w >= a) {
          Robot.values().forEach {
            dfs(
              t-1,
              it,
              oreRobots + 1,
              clayRobots,
              obsidianRobots,
              geodeRobots,
              w - a + oreRobots,
              x + clayRobots,
              y + obsidianRobots,
              z + geodeRobots
            )
          }
          return
        } else if (robot == Robot.CLAY && w >= b) {
          Robot.values().forEach {
            dfs(
              t - 1,
              it,
              oreRobots,
              clayRobots + 1,
              obsidianRobots,
              geodeRobots,
              w - b + oreRobots,
              x + clayRobots,
              y + obsidianRobots,
              z + geodeRobots
            )
          }
          return
        } else if (robot == Robot.OBSIDIAN && w >= c && x >= d) {
          Robot.values().forEach {
            dfs(
              t - 1,
              it,
              oreRobots,
              clayRobots,
              obsidianRobots + 1,
              geodeRobots,
              w - c + oreRobots,
              x - d + clayRobots,
              y + obsidianRobots,
              z + geodeRobots
            )
          }
          return
        } else if (robot == Robot.GEODE && w >= e && y >= f) {
          Robot.values().forEach {
            dfs(
              t - 1,
              it,
              oreRobots,
              clayRobots,
              obsidianRobots,
              geodeRobots + 1,
              w - e + oreRobots,
              x + clayRobots,
              y -f + obsidianRobots,
              z + geodeRobots
            )
          }
          return
        }
        t = t - 1
        w = w + oreRobots
        x = x + clayRobots
        y = y + obsidianRobots
        z = z + geodeRobots
      }
      m = max(m, z)
    }

    Robot.values().forEach { robot ->
      dfs(minutes , robot, 1, 0, 0, 0, 0, 0, 0, 0 )
    }

    return m
  }


  private fun blueprint(line: String): Blueprint {
    val parts = line.split(" ")
    val id = parts[1].dropLast(1).toInt()
    val oreRobotOreCost = parts[6].toInt()
    val clayRobotOreCost = parts[12].toInt()
    val obsidianRobotOreCost = parts[18].toInt()
    val obsidianRobotClayCost = parts[21].toInt()
    val geodeRobotOreCost = parts[27].toInt()
    val geodeRobotObsidianCost = parts[30].toInt()

    return Blueprint(
      id,
      oreRobotOreCost,
      clayRobotOreCost,
      obsidianRobotOreCost,
      obsidianRobotClayCost,
      geodeRobotOreCost,
      geodeRobotObsidianCost
    )
  }

  data class Blueprint(
    val id: Int,
    val oreRobotOreCost: Int,
    val clayRobotOreCost: Int,
    val obsidianRobotOreCost: Int,
    val obsidianRobotClayCost: Int,
    val geodeRobotOreCost: Int,
    val geodeRobotObsidianCost: Int
  ) {
    val maxOre = maxOf(oreRobotOreCost, clayRobotOreCost, obsidianRobotOreCost, geodeRobotOreCost)
  }

  data class RobotChoices(val oreRobots: Int?, val clayRobots: Int?, val obsidianRobots: Int?, val geodeRobots: Int?)

  enum class Robot(robotIndex: Int) {
    ORE(0), CLAY(1), OBSIDIAN(2), GEODE(3)
  }
}

