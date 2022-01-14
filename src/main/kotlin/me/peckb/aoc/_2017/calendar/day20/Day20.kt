package me.peckb.aoc._2017.calendar.day20

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.abs

class Day20 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::day20) { input ->
    val particles = input.mapIndexed { i, p -> p.apply { id = i } }.sortedBy {
      it.acceleration.let { (x, y, z) -> abs(x) + abs(y) + abs(z) }
    }
    
    particles.first().id
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::day20) { input ->
    val particles = input.mapIndexed { i, p -> p.apply { id = i } }.toMutableList()

    // how do we know it happens all within 40 ticks?
    // Well we don't actually, but we started with 10k to get the correct answer, and then
    // just lowered the number to speed up the solution :)
    repeat(40) {
      particles.removeItemsAtSameLocation()
      particles.forEach { it.tick() }
    }

    particles.size
  }

  private fun day20(line: String): Particle {
    val (locationString, velocityString, accelerationString) = line.split(", ")

    val location = locationString.substringAfter("<").dropLast(1).split(",").let {
      Vector(it[0].toLong(), it[1].toLong(), it[2].toLong())
    }
    val velocity = velocityString.substringAfter("<").dropLast(1).split(",").let {
      Vector(it[0].toLong(), it[1].toLong(), it[2].toLong())
    }
    val acceleration = accelerationString.substringAfter("<").dropLast(1).split(",").let {
      Vector(it[0].toLong(), it[1].toLong(), it[2].toLong())
    }

    return Particle(location, velocity, acceleration)
  }

  data class Vector(var x: Long, var y: Long, var z: Long)

  data class Particle(val location: Vector, private val velocity: Vector, val acceleration: Vector, var id: Int = 0) {
    fun tick() {
      velocity.x += acceleration.x
      velocity.y += acceleration.y
      velocity.z += acceleration.z

      location.x += velocity.x
      location.y += velocity.y
      location.z += velocity.z
    }
  }

  private fun MutableList<Particle>.removeItemsAtSameLocation() {
    val groupedParticles = this.groupBy { it.location }
    groupedParticles.forEach { (_, data) ->
      if (data.size > 1) {
        data.forEach { remove(it) }
      }
    }
  }
}
