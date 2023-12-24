package me.peckb.aoc._2023.calendar.day24

import com.microsoft.z3.*
import me.peckb.aoc.generators.CombinationsGenerator
import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import org.apache.commons.geometry.euclidean.threed.Vector3D
import org.apache.commons.geometry.euclidean.twod.*
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
    val ctx = Context()
    val solver = ctx.mkSolver()

    val bv_type = ctx.mkBitVecSort(64);

    fun variableOf(name: String) = ctx.mkConst(name, bv_type)

    fun valueOf(value: Long) = ctx.mkNumeral(value, bv_type)

    operator fun Expr<BitVecSort>.times(t: Expr<BitVecSort>) = ctx.mkBVMul(this, t)

    operator fun Expr<BitVecSort>.plus(t: Expr<BitVecSort>) = ctx.mkBVAdd(this, t)

    val zero = valueOf(0)

    val x = variableOf("x")
    val y = variableOf("y")
    val z = variableOf("z")
    val dx = variableOf("dx")
    val dy = variableOf("dy")
    val dz = variableOf("dz")

    input.take(3).forEachIndexed { index, hail ->
      val t = variableOf("t_$index")

      val posX = valueOf(hail.position.x.toLong())
      val posY = valueOf(hail.position.y.toLong())
      val posZ = valueOf(hail.position.z.toLong())

      val velX = valueOf(hail.velocity.x.toLong())
      val velY = valueOf(hail.velocity.y.toLong())
      val velZ = valueOf(hail.velocity.z.toLong())

      solver.add(ctx.mkBVSGT(t, zero))
      solver.add(ctx.mkEq(x + dx * t, posX + velX * t))
      solver.add(ctx.mkEq(y + dy * t, posY + velY * t))
      solver.add(ctx.mkEq(z + dz * t, posZ + velZ * t))
    }

    solver.check()
    val model = solver.model

    val xEval = model.eval(x, true)
    val yEval = model.eval(y, true)
    val zEval = model.eval(z, true)

    (xEval as BitVecNum).long + (yEval as BitVecNum).long + (zEval as BitVecNum).long
  }

  private fun hailstone(line: String) : Hailstone {
    return line.split(" @ ").let { (pos, vel) ->
      val (x, y, z) = pos.split(", ").map { it.trim().toDouble() }
      val (dx, dy, dz) = vel.split(", ").map { it.trim().toDouble() }

      Hailstone(Vector3D.of(x, y, z), Vector3D.of(dx, dy, dz))
    }
  }

  data class Hailstone(val position: Vector3D, val velocity: Vector3D) {
    fun intersect2D(hailB: Hailstone): Vector2D? {
      return line2D.intersection(hailB.line2D)
    }

    private val line2D by lazy {
      Lines.fromPointAndDirection(
        Vector2D.of(position.x, position.y),
        Vector2D.of(velocity.x, velocity.y),
        Precision.doubleEquivalenceOfEpsilon(1e-14)
      )
    }

    fun isFutureValue(x: Double): Boolean {
      return !(velocity.x < 0.0 && x > position.x || velocity.x > 0.0 && x < position.x)
    }
  }
}