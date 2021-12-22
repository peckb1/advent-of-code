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
    val everyOverlap = mutableListOf<Instruction>()
    data.forEach { mainInstruction ->
      val newOverlapInstructions = mutableListOf(mainInstruction)
      val (_, mainInstructionCube) = mainInstruction

      everyOverlap.forEach { (previousOverlapActivation, previousOverlapCube) ->
        val intersectionCube: Bounds3D? = previousOverlapCube.intersection(mainInstructionCube)
        intersectionCube?.let {
          newOverlapInstructions.add(Instruction(-previousOverlapActivation, intersectionCube))
        }
      }
      everyOverlap.addAll(newOverlapInstructions)
    }

    return everyOverlap.sumOf { it.cube.area() * it.activation }
  }

  enum class Activation(var sumModifier: Int) {
    ENABLE(1), DISABLE(0), OVERAGE_FIXER(-1);

    fun swap() = when (this) {
      ENABLE -> OVERAGE_FIXER
      DISABLE -> DISABLE
      OVERAGE_FIXER -> OVERAGE_FIXER
    }

    companion object {
      fun from(activationString: String): Activation = if (activationString == "on") ENABLE else DISABLE
    }
  }

  private fun Bounds3D.area() = (max.x - min.x + 1) * (max.y - min.y + 1) * (max.z - min.z + 1)

  data class Instruction(val activation: Int, val cube: Bounds3D)

  private fun instruction(line: String): Instruction {
    // val activation = Activation.from(line.split(" ").first())
    val activation = if (line.split(" ").first() == "on") 1 else 0

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
}
