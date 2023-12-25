package me.peckb.aoc._2023.calendar.day24

import com.microsoft.z3.BitVecNum
import com.microsoft.z3.BitVecSort
import com.microsoft.z3.Context
import com.microsoft.z3.Expr
import me.peckb.aoc.generators.CombinationsGenerator
import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import org.apache.commons.geometry.euclidean.twod.Line
import org.apache.commons.geometry.euclidean.twod.Lines
import org.apache.commons.geometry.euclidean.twod.Vector2D
import org.apache.commons.numbers.core.Precision

class Day24 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::hailstone) { input ->
    val hail = input.toList().toTypedArray()

    val combinations = CombinationsGenerator.findCombinations(hail, 2)

    val min = 200000000000000.0
    val max = 400000000000000.0
    val range = min .. max

    combinations.count { (hailA, hailB) ->
      hailA.intersect2D(hailB)?.let {
        hailA.isFutureValue(it.x) && hailB.isFutureValue(it.x) && range.contains(it.x) && range.contains(it.y)
      } ?: false
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::hailstone) { input ->
    Context().use { ctx ->
      val solver = ctx.mkSolver()

      val longType = ctx.mkBitVecSort(64)

      fun variableOf(name: String) = ctx.mkConst(name, longType)

      fun valueOf(value: Long) = ctx.mkNumeral(value, longType) as BitVecNum

      operator fun Expr<BitVecSort>.times(t: Expr<BitVecSort>) = ctx.mkBVMul(this, t)

      operator fun Expr<BitVecSort>.plus(t: Expr<BitVecSort>) = ctx.mkBVAdd(this, t)

      infix fun Expr<BitVecSort>.equalTo(t: Expr<BitVecSort>) = ctx.mkEq(this, t)

      infix fun Expr<BitVecSort>.greaterThan(t: Expr<BitVecSort>) = ctx.mkBVSGT(this, t)

      val zero = valueOf(0)

      val x = variableOf("x")
      val y = variableOf("y")
      val z = variableOf("z")
      val dx = variableOf("dx")
      val dy = variableOf("dy")
      val dz = variableOf("dz")

      input.take(3).forEachIndexed { index, hail ->
        val t = variableOf("t_$index")

        val (posX, posY, posZ) = hail.position.toZ3Numerals(::valueOf)
        val (velX, velY, velZ) = hail.velocity.toZ3Numerals(::valueOf)

        solver.add(t greaterThan zero)
        solver.add(x + dx * t equalTo posX + velX * t)
        solver.add(y + dy * t equalTo posY + velY * t)
        solver.add(z + dz * t equalTo posZ + velZ * t)
      }

      solver.check()

      val model = solver.model

      val xEval = model.evaluate(x, true) as BitVecNum
      val yEval = model.evaluate(y, true) as BitVecNum
      val zEval = model.evaluate(z, true) as BitVecNum

      xEval.long + yEval.long + zEval.long
    }
  }

  private fun hailstone(line: String) : Hailstone {
    return line.split(" @ ").let { (pos, vel) ->
      val (x, y, z) = pos.split(", ").map { it.trim().toDouble() }
      val (dx, dy, dz) = vel.split(", ").map { it.trim().toDouble() }

      Hailstone(Triplet(x, y, z), Triplet(dx, dy, dz))
    }
  }

  data class Triplet(val x: Double, val y: Double, val z: Double) {
    fun toZ3Numerals(valueOf: (Long) -> BitVecNum): Triple<BitVecNum, BitVecNum, BitVecNum> {
      return Triple(valueOf(x.toLong()), valueOf(y.toLong()), valueOf(z.toLong()))
    }
  }

  data class Hailstone(val position: Triplet, val velocity: Triplet) {
    fun intersect2D(hailB: Hailstone): Vector2D? {
      return line2D.intersection(hailB.line2D)
    }

    fun isFutureValue(x: Double): Boolean {
      return !(velocity.x < 0.0 && x > position.x || velocity.x > 0.0 && x < position.x)
    }

    private val line2D: Line by lazy {
      Lines.fromPointAndDirection(
        Vector2D.of(position.x, position.y),
        Vector2D.of(velocity.x, velocity.y),
        Precision.doubleEquivalenceOfEpsilon(1e-14)
      )
    }
  }
}
