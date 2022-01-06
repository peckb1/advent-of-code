package me.peckb.aoc._2016.calendar.day10

import arrow.core.Either
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day10 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val robots = loadRobots(input)
    robots.values.firstOrNull { it.lowValue == 17 && it.highValue == 61 }?.id
  }


  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val robots = loadRobots(input)

    var output0Value: Int? = null
    var output1Value: Int? = null
    var output2Value: Int? = null
    robots.values.forEach { robot ->
      robot.low?.tapLeft { if (it == 0) output0Value = robot.lowValue }
      robot.high?.tapLeft { if (it == 0) output0Value = robot.highValue }

      robot.low?.tapLeft { if (it == 1) output1Value = robot.lowValue }
      robot.high?.tapLeft { if (it == 1) output1Value = robot.highValue }

      robot.low?.tapLeft { if (it == 2) output2Value = robot.lowValue }
      robot.high?.tapLeft { if (it == 2) output2Value = robot.highValue }
    }

    output0Value!! * output1Value!! * output2Value!!
  }

  private fun loadRobots(input: Sequence<String>) : MutableMap<Int, Robot> {
    val robots = mutableMapOf<Int, Robot>()

    input.forEach {
      val parts = it.split(" ")
      when (parts[0]) {
        "value" -> handleInputFromValue(robots, parts)
        "bot" -> handleInputFromBot(robots, parts)
      }
    }

    return robots
  }

  /**
   * value n goes to bot x
   */
  private fun handleInputFromValue(robots: MutableMap<Int, Robot>, parts: List<String>) {
    val id: RobotId = parts[5].toInt()
    val value = parts[1].toInt()
    robots[id] = (robots[id] ?: Robot(id)).also { it.giveValue(value, robots) }
  }

  /**
   * bot x gives low to [bot|output] y and high to [bot|output] z
   */
  private fun handleInputFromBot(robots: MutableMap<Int, Robot>, parts: List<String>) {
    val id: RobotId = parts[1].toInt()
    val lowDestinationType = parts[5]
    val lowDestinationId = parts[6].toInt()
    val highDestinationType = parts[10]
    val highDestinationId = parts[11].toInt()

    val low: Either<Output, RobotId> = when (lowDestinationType) {
      "bot" -> Either.Right(lowDestinationId)
      else -> Either.Left(lowDestinationId)
    }

    val high: Either<Output, RobotId> = when (highDestinationType) {
      "bot" -> Either.Right(highDestinationId)
      else -> Either.Left(highDestinationId)
    }

    robots[id] = (robots[id] ?: Robot(id)).also {
      it.giveLow(low, robots)
      it.giveHigh(high, robots)
    }
  }
}

