package me.peckb.aoc._2025.calendar.day10

import com.microsoft.z3.BitVecNum
import com.microsoft.z3.BitVecSort
import com.microsoft.z3.Context
import com.microsoft.z3.Expr
import com.microsoft.z3.Optimize
import com.microsoft.z3.Status
import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import org.apache.commons.math3.stat.inference.TestUtils.g
import kotlin.math.min
import kotlin.use

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

      Context().use { ctx ->
        var bestFound = Int.MAX_VALUE

        val bits = 64 // the answer will fit inside a long

        fun valueOf(value: Int) = ctx.mkBV(value, bits)

        fun variableOf(name: String) = ctx.mkBVConst(name, bits)

        infix fun Expr<BitVecSort>.equalTo(t: Expr<BitVecSort>) = ctx.mkEq(this, t)

        infix fun Expr<BitVecSort>.notEqual(t: Expr<BitVecSort>) = ctx.mkNot(this equalTo t)

        operator fun Expr<BitVecSort>.plus(t: Expr<BitVecSort>) = ctx.mkBVAdd(this, t)

        operator fun Expr<BitVecSort>.times(t: Expr<BitVecSort>) = ctx.mkBVMul(this, t)

        infix fun Expr<BitVecSort>.lessThan(t: Expr<BitVecSort>) = ctx.mkBVSLT(this, t)

        infix fun Expr<BitVecSort>.gte(t: Expr<BitVecSort>) = ctx.mkBVSGE(this, t)

        val a = variableOf("a")
        val b = variableOf("b")
        val c = variableOf("c")
        val d = variableOf("d")
        val e = variableOf("e")
        val f = variableOf("f")

        val optimizer = ctx.mkOptimize()

        optimizer.Add(e + f equalTo valueOf(3))
        optimizer.Add(b + f equalTo valueOf(5))
        optimizer.Add(c + d + e equalTo valueOf(4))
        optimizer.Add(a + b + d equalTo valueOf(7))
//        optimizer.Add(a lessThan valueOf(10000))
//        optimizer.Add(b lessThan valueOf(10000))
//        optimizer.Add(c lessThan valueOf(10000))
//        optimizer.Add(d lessThan valueOf(10000))
//        optimizer.Add(e lessThan valueOf(10000))
//        optimizer.Add(f lessThan valueOf(10000))
        optimizer.Add(a gte valueOf(0))
        optimizer.Add(b gte valueOf(0))
        optimizer.Add(c gte valueOf(0))
        optimizer.Add(d gte valueOf(0))
        optimizer.Add(e gte valueOf(0))
        optimizer.Add(f gte valueOf(0))

        optimizer.MkMinimize(a + b + c + d + e + f)

        optimizer.Check()
//        while(optimizer.Check() == Status.SATISFIABLE) {
          val model = optimizer.model

          val aEval = (model.evaluate(a, true) as BitVecNum)
          val bEval = (model.evaluate(b, true) as BitVecNum)
          val cEval = (model.evaluate(c, true) as BitVecNum)
          val dEval = (model.evaluate(d, true) as BitVecNum)
          val eEval = (model.evaluate(e, true) as BitVecNum)
          val fEval = (model.evaluate(f, true) as BitVecNum)

          bestFound = min(bestFound, aEval.int + bEval.int + cEval.int + dEval.int + eEval.int + fEval.int)

//          optimizer.Add(
//            ctx.mkOr(
//            a notEqual aEval,
//              b notEqual bEval,
//              c notEqual cEval,
//              d notEqual dEval,
//              e notEqual eEval,
//              f notEqual fEval,
//            )
//          )
//        }
//        println(bestFound)
        bestFound
      }
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