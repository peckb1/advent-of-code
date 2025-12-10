package me.peckb.aoc._2025.calendar.day10

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.min

class Day10 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::day10) { input ->
    input.sumOf { machine ->
       thing(".".repeat(machine.indicatorLights.length), machine.indicatorLights, machine.buttonSchematics)
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::day10) { input ->
    input.sumOf { machine ->
      4
    }
  }


  fun thing(current: String, goal: String, buttonSchematics: List<ButtonSchematic>): Long {
    val minPressesForState = mutableMapOf(current to 0L)
    while(!minPressesForState.containsKey(goal)) {
      minPressesForState.entries.toList().forEach { (light, cost) ->
        val nextStates = possibleLights(light, buttonSchematics)
        nextStates.forEach { state ->
          if (minPressesForState.containsKey(state)) {
            minPressesForState[state] = min(minPressesForState[state]!!, cost + 1)
          } else {
            minPressesForState[state] = cost + 1
          }
        }
      }
    }

    return minPressesForState[goal]!!
  }

  fun possibleLights(current: String, buttonSchematics: List<ButtonSchematic>): List<String> {
    return buttonSchematics.map { applyLightPress(current, it) }
  }

  private fun applyLightPress(current: String, buttonSchematic: ButtonSchematic): String {
    return current.mapIndexed { index, c ->
      if (buttonSchematic.lightsAffected.contains(index)) {
        if (c == '#') { '.' } else { '#' }
      } else { c }
    }.joinToString("")
  }

  private fun day10(line: String): Machine {
    // SAMPLE: [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
    val indicatorLights = line.substringBefore(']').drop(1)
    val buttonSchematics = line.substringAfter("] ").substringBefore(" {").split(" ").map { buttonList ->
      val buttonIndices = buttonList.drop(1).dropLast(1).split(",").map { it.toInt() }
      ButtonSchematic(buttonIndices)
    }
    val joltages = line.substringAfter('{').dropLast(1).split(",").map { it.toInt() }

    return Machine(indicatorLights, buttonSchematics, joltages)
  }
}

data class Machine(
  val indicatorLights: String,
  val buttonSchematics: List<ButtonSchematic>,
  val joltages: List<Int>,
)

/** zero indexed */
// TODO rename
data class ButtonSchematic(val lightsAffected: List<Int>)