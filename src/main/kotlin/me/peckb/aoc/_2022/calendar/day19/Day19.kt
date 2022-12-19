package me.peckb.aoc._2022.calendar.day19

import me.peckb.aoc._2022.calendar.day19.Day19.Robot.*
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject


class Day19 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::blueprint) { input ->
    input.toList().fold(0) { acc, blueprint ->
      acc + (maxGeode(blueprint, 24) * blueprint.id)
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::blueprint) { input ->
    input.toList().take(3).fold(1) { acc, blueprint ->
      acc * maxGeode(blueprint, 32)
    }
  }

  private fun maxGeode(blueprint: Blueprint, minutes: Int): Int {
    fun makeRobot(
      timeRemaining: Int, robotToMake: Robot,
      oreRobots: Int, clayRobots: Int, obsidianRobots: Int, geodeRobots: Int,
      ore: Int, clay: Int, obsidian: Int, geodes: Int
    ): Int {
      // we're trying to move forward by making `robotToMake`
      // but don't bother going forward if we
      //   (a) have too many (in the case of ore, clay, and obsidian)
      //   (b) will never make enough (in the case of clay and obsidian)
      when (robotToMake) {
        ORE -> if (oreRobots >= blueprint.maxOre) return 0
        CLAY -> if (clayRobots >= blueprint.obsidianRobotClayCost) return 0
        OBSIDIAN -> if (obsidianRobots >= blueprint.geodeRobotObsidianCost || clayRobots == 0) return 0
        GEODE -> if (obsidianRobots == 0) return 0
      }

      // since `eventually` we'll be able to build the robot we're after
      // we can keep track of how many minutes have passed as existing
      // robots mine up their resources
      var minutesPassed = 0
      while (timeRemaining - minutesPassed > 0) {
        val currentTime = timeRemaining - minutesPassed
        val currentOre = ore + (minutesPassed * oreRobots)
        val currentClay = clay + (minutesPassed * clayRobots)
        val currentObsidian = obsidian + (minutesPassed * obsidianRobots)
        val currentGeodes = geodes + (minutesPassed * geodeRobots)

        if (robotToMake == ORE && currentOre >= blueprint.oreRobotOreCost) {
          return Robot.values().maxOf {
            makeRobot(
              currentTime - 1,
              it,
              oreRobots + 1,
              clayRobots,
              obsidianRobots,
              geodeRobots,
              currentOre - blueprint.oreRobotOreCost + oreRobots,
              currentClay + clayRobots,
              currentObsidian + obsidianRobots,
              currentGeodes + geodeRobots
            )
          }
        } else if (robotToMake == CLAY && currentOre >= blueprint.clayRobotOreCost) {
          return Robot.values().maxOf {
            makeRobot(
              currentTime - 1,
              it,
              oreRobots,
              clayRobots + 1,
              obsidianRobots,
              geodeRobots,
              currentOre - blueprint.clayRobotOreCost + oreRobots,
              currentClay + clayRobots,
              currentObsidian + obsidianRobots,
              currentGeodes + geodeRobots
            )
          }
        } else if (robotToMake == OBSIDIAN && currentOre >= blueprint.obsidianRobotOreCost && currentClay >= blueprint.obsidianRobotClayCost) {
          return Robot.values().maxOf {
            makeRobot(
              currentTime - 1,
              it,
              oreRobots,
              clayRobots,
              obsidianRobots + 1,
              geodeRobots,
              currentOre - blueprint.obsidianRobotOreCost + oreRobots,
              currentClay - blueprint.obsidianRobotClayCost + clayRobots,
              currentObsidian + obsidianRobots,
              currentGeodes + geodeRobots
            )
          }
        } else if (robotToMake == GEODE && currentOre >= blueprint.geodeRobotOreCost && currentObsidian >= blueprint.geodeRobotObsidianCost) {
          return Robot.values().maxOf {
            makeRobot(
              currentTime - 1,
              it,
              oreRobots,
              clayRobots,
              obsidianRobots,
              geodeRobots + 1,
              currentOre - blueprint.geodeRobotOreCost + oreRobots,
              currentClay + clayRobots,
              currentObsidian - blueprint.geodeRobotObsidianCost + obsidianRobots,
              currentGeodes + geodeRobots
            )
          }
        }
        minutesPassed++
      }
      return geodes + (minutesPassed * geodeRobots)
    }

    return Robot.values().maxOf { robot ->
      makeRobot(minutes, robot, 1, 0, 0, 0, 0, 0, 0, 0)
    }
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

  enum class Robot { ORE, CLAY, OBSIDIAN, GEODE }
}

