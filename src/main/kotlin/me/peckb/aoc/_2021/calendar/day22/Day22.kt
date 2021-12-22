package me.peckb.aoc._2021.calendar.day22

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import org.apache.commons.geometry.euclidean.threed.Bounds3D
import org.apache.commons.geometry.euclidean.threed.Vector3D
import javax.inject.Inject

class Day22 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {

  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs(::instruction) { input ->
    // data.indices.forEach { index ->
    //   println(maths(data.take(index + 1)).toLong())
    // }
    maths(input.take(20)).toLong()
  }

  /*

def solve(data, part1=False):
    visited = set()
    for step in data:
        cuboid = Cuboid(*step[1:], step[0])
        if part1 and not cuboid.is_small():
            continue
        new_cuboids = set([cuboid]) if cuboid.on else set()
        for c in visited:
            intersection = c.intersection(cuboid, -c.on)
            if intersection is not None:
                new_cuboids.add(intersection)
        visited |= new_cuboids
    return sum([c.on * c.size() for c in visited])

   */

  fun maths(data: Sequence<Instruction>): Double {
    // println(data)
    val overlaps = mutableListOf<Instruction>()
    data.forEach { mainInstruction ->
      val newOverlapInstructions = mutableListOf<Instruction>().apply {
        if (mainInstruction.activation == 1) this.add(mainInstruction)
      }
      overlaps.forEach { previouslyFoundOverlap ->
        previouslyFoundOverlap.cube.intersection(mainInstruction.cube)?.let { intersection ->

          newOverlapInstructions.add(Instruction(-previouslyFoundOverlap.activation, intersection))
        }
      }
      overlaps.addAll(newOverlapInstructions)
    }

    // println()
    // visited.forEach {
    //   println(it)
    // }
    // print("${visited.size} ")
    return overlaps.sumOf { it.cube.area() * it.activation }
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::instruction) { input ->
    maths(input).toLong()
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

  private fun Bounds3D.area() =
    (max.x - min.x + 1) * (max.y - min.y + 1) * (max.z - min.z + 1)

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
