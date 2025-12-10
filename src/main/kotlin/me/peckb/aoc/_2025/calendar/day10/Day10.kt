package me.peckb.aoc._2025.calendar.day10

import com.microsoft.z3.ArithExpr
import com.microsoft.z3.Context
import com.microsoft.z3.IntExpr
import com.microsoft.z3.IntNum
import com.microsoft.z3.IntSort
import com.microsoft.z3.Status
import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.math.min

class Day10 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::day10) { input ->
    input.sumOf { machine -> flipLights(machine.indicatorLights, machine.buttonSchematics) }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::day10) { input ->
    input.sumOf { machine -> enableJoltages(machine) }
  }

  private fun enableJoltages(machine: Machine) : Int {
    return Context().use { ctx ->
      val opt = ctx.mkOptimize()

      fun intVariableOf(name: String) = ctx.mkIntConst(name)

      fun intValueOf(value: Int) = ctx.mkInt(value)

      infix fun IntExpr.gte(t: IntExpr) = ctx.mkGe(this, t)

      operator fun ArithExpr<IntSort>.plus(t: IntExpr) = ctx.mkAdd(this, t)

      infix fun ArithExpr<IntSort>.equalTo(t: IntExpr) = ctx.mkEq(this, t)

      fun List<IntExpr>.sum() = ctx.mkAdd(*this.toTypedArray())

      val ZERO = intValueOf(0)

      val affectedJoltages = mutableMapOf<Int, MutableList<IntExpr>>()
      val buttonVariables = machine.buttonSchematics.mapIndexed { index, schematic ->
        // create button variable
        intVariableOf(index.toString()).also { buttonVariable ->
          // track the joltages if affects
          schematic.affectedIndices.forEach { affectedIndex ->
            affectedJoltages.computeIfAbsent(affectedIndex) { _ -> mutableListOf() }.add(buttonVariable)
          }
          // add it to the optimizer
          opt.Add(buttonVariable gte ZERO)
        }
      }

      affectedJoltages.forEach { (joltageIndex, buttonsAffecting) ->
        val targetValue = intValueOf(machine.joltages[joltageIndex])
        val sumOfButtonPresses = buttonsAffecting.sum()
        opt.Add(sumOfButtonPresses equalTo targetValue)
      }

      opt.MkMinimize(buttonVariables.sum())

      val status = opt.Check()

      if (status == Status.SATISFIABLE) {
        val model = opt.getModel()
        ((model.evaluate(buttonVariables.sum(), true) as IntNum).int)
      } else {
        throw IllegalStateException("No Solution Found")
      }
    }
  }

  fun flipLights(goal: String, buttonSchematics: List<ButtonSchematic>): Long {
    val minPressesForState = mutableMapOf(".".repeat(goal.length) to 0L)
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
      if (buttonSchematic.affectedIndices.contains(index)) {
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

data class ButtonSchematic(val affectedIndices: List<Int>)