package me.peckb.aoc._2021.calendar.day22

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import org.apache.commons.geometry.euclidean.threed.Bounds3D
import org.apache.commons.geometry.euclidean.threed.Vector3D
import javax.inject.Inject

class Day22 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::instruction) { input ->
    maths(input.take(20)).toLong()
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::instruction) { input ->
    maths(input).toLong()
  }

  private fun maths(data: Sequence<Instruction>): Double {
    val everyOverlapInstruction = mutableListOf<Instruction>()
    data.forEach { mainInstruction ->
      val newOverlapInstruction = mutableListOf(mainInstruction)
      val (_, newCube) = mainInstruction

      everyOverlapInstruction.forEach { (overlapActivation, overlapCube) ->
        val intersectionCube: Bounds3D? = newCube.intersection(overlapCube)
        intersectionCube?.let {
          newOverlapInstruction.add(Instruction(overlapActivation.invert(), intersectionCube))
        }
      }
      everyOverlapInstruction.addAll(newOverlapInstruction)
    }

    return everyOverlapInstruction.sumOf { it.cube.area() * it.activation.areaModifier }
  }

  private fun Bounds3D.area() = (max.x - min.x + 1) * (max.y - min.y + 1) * (max.z - min.z + 1)

  private fun instruction(line: String): Instruction {
    val activation = Activation.from(line.split(" ").first())

    val (xData, yData, zData) =
      line.split(",")
        .map { data ->
          data.split("=")
            .last()
            .split("..")
            .map { it.toDouble() }
        }

    val minVector = Vector3D.of(xData.first(), yData.first(), zData.first())
    val maxVector = Vector3D.of(xData.last(), yData.last(), zData.last())

    return Instruction(activation, Bounds3D.from(minVector, maxVector))
  }

  data class Instruction(val activation: Activation, val cube: Bounds3D)

  enum class Activation(var areaModifier: Int) {
    ENABLE(1),
    DISABLE(0),
    OVERAGE_FIXER(-1);

    fun invert() = when (this) {
      ENABLE -> OVERAGE_FIXER
      DISABLE -> DISABLE
      OVERAGE_FIXER -> ENABLE
    }

    companion object {
      fun from(activationString: String) = if (activationString == "on") ENABLE else DISABLE
    }
  }
}
