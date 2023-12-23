package me.peckb.aoc._2022.calendar.day19

import me.peckb.aoc._2022.calendar.day19.Day19.Robot.*
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.math.max


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

  private fun maxGeode(bp: Blueprint, minutes: Int): Int {
    var maxGeodesSoFar = 0

    fun makeRobot(bs: BeachState): Int {
      // we're trying to move forward by making `robotToMake`
      // but don't bother going forward if we
      //   (a) have too many (in the case of ore, clay, and obsidian)
      //   (b) will never make enough (in the case of clay and obsidian)
      when (bs.robotToMake) {
        ORE -> if (bs.oreRobots >= bp.maxOre) return 0
        CLAY -> if (bs.clayRobots >= bp.obsidianRobotClayCost) return 0
        OBSIDIAN -> if (bs.obsidianRobots >= bp.geodeRobotObsidianCost || bs.clayRobots == 0) return 0
        GEODE -> if (bs.obsidianRobots == 0) return 0
      }

      // Reddit hyper-optimization
      // if we somehow managed to create nonstop geodes from our current recursion
      // would it even be possible to eclipse our current maximum?
      // if not - then this branch is dead
      if (bs.geodes + (bs.geodeRobots * bs.timeRemaining) + TRIANGLE_NUMBERS[bs.timeRemaining] <= maxGeodesSoFar) return 0

      // since `eventually` we'll be able to build the robot we're after
      // we can keep track of how many minutes have passed as existing
      // robots mine up their resources
      var minutesPassed = 0
      while (bs.currentTime(minutesPassed) > 0) {
        val currentOre = bs.currentOre(minutesPassed)
        val currentClay = bs.currentClay(minutesPassed)
        val currentObsidian = bs.currentObsidian(minutesPassed)

        when (bs.robotToMake) {
          ORE -> if (currentOre >= bp.oreRobotOreCost) {
            return entries.maxOf { robot ->
              makeRobot(
                bs.advanceState(
                  minutesPassed,
                  robot,
                  newOreRobots = bs.oreRobots + 1,
                  newOre = currentOre + bs.oreRobots - bp.oreRobotOreCost
                )
              )
            }.also { maxGeodesSoFar = max(maxGeodesSoFar, it) }
          }

          CLAY -> if (currentOre >= bp.clayRobotOreCost) {
            return entries.maxOf { robot ->
              makeRobot(
                bs.advanceState(
                  minutesPassed,
                  robot,
                  newClayRobots = bs.clayRobots + 1,
                  newOre = currentOre + bs.oreRobots - bp.clayRobotOreCost
                )
              )
            }.also { maxGeodesSoFar = max(maxGeodesSoFar, it) }
          }

          OBSIDIAN -> if (currentOre >= bp.obsidianRobotOreCost && currentClay >= bp.obsidianRobotClayCost) {
            return entries.maxOf { robot ->
              makeRobot(
                bs.advanceState(
                  minutesPassed,
                  robot,
                  newObsidianRobots = bs.obsidianRobots + 1,
                  newOre = currentOre + bs.oreRobots - bp.obsidianRobotOreCost,
                  newClay = currentClay + bs.clayRobots - bp.obsidianRobotClayCost
                )
              )
            }.also { maxGeodesSoFar = max(maxGeodesSoFar, it) }
          }

          GEODE -> if (currentOre >= bp.geodeRobotOreCost && currentObsidian >= bp.geodeRobotObsidianCost) {
            return entries.maxOf { robot ->
              makeRobot(
                bs.advanceState(
                  minutesPassed,
                  robot,
                  newGeodeRobots = bs.geodeRobots + 1,
                  newOre = currentOre + bs.oreRobots - bp.geodeRobotOreCost,
                  newObsidian = currentObsidian + bs.obsidianRobots - bp.geodeRobotObsidianCost
                )
              )
            }.also { maxGeodesSoFar = max(maxGeodesSoFar, it) }
          }
        }
        minutesPassed++
      }
      return bs.geodes + (minutesPassed * bs.geodeRobots)
    }

    return entries.maxOf { robot ->
      makeRobot(BeachState(minutes, robot, 1, 0, 0, 0, 0, 0, 0, 0))
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

  data class BeachState(
    val timeRemaining: Int,
    val robotToMake: Robot,
    val oreRobots: Int,
    val clayRobots: Int,
    val obsidianRobots: Int,
    val geodeRobots: Int,
    val ore: Int,
    val clay: Int,
    val obsidian: Int,
    val geodes: Int
  ) {
    fun advanceState(
      minutesSpent: Int,
      newRobotToMake: Robot,
      newOreRobots: Int = oreRobots,
      newClayRobots: Int = clayRobots,
      newObsidianRobots: Int = obsidianRobots,
      newGeodeRobots: Int = geodeRobots,
      newOre: Int = oreRobots + currentOre(minutesSpent),
      newClay: Int = clayRobots + currentClay(minutesSpent),
      newObsidian: Int = obsidianRobots + currentObsidian(minutesSpent),
      newGeodes: Int = geodeRobots + currentGeodes(minutesSpent),
    ) = BeachState(
      currentTime(minutesSpent) - 1,
      newRobotToMake,
      newOreRobots,
      newClayRobots,
      newObsidianRobots,
      newGeodeRobots,
      newOre,
      newClay,
      newObsidian,
      newGeodes,
    )

    fun currentTime(minutesPassed: Int) = timeRemaining - minutesPassed

    fun currentOre(minutesPassed: Int) = ore + (minutesPassed * oreRobots)

    fun currentClay(minutesPassed: Int) = clay + (minutesPassed * clayRobots)

    fun currentObsidian(minutesPassed: Int) = obsidian + (minutesPassed * obsidianRobots)

    fun currentGeodes(minutesPassed: Int) = geodes + (minutesPassed * geodeRobots)
  }

  companion object {
    private val TRIANGLE_NUMBERS = (0..32).map { previousTriangleNumber(it) }

    private fun previousTriangleNumber(n: Int) = ((n - 1) * n) / 2
  }
}
