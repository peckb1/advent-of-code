package me.peckb.aoc._2019.calendar.day01

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day01 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::moduleMass) { input ->
    input.map(::calculateFuelNeeded).sum()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::moduleMass) { input ->
    input.map { originalMass ->
      var totalFuelNeeded = 0L
      var fuelToAdd = calculateFuelNeeded(originalMass)

      while(fuelToAdd > 0) {
        totalFuelNeeded += fuelToAdd
        fuelToAdd = calculateFuelNeeded(fuelToAdd)
      }

      totalFuelNeeded
    }.sum()
  }

  private fun moduleMass(line: String) = line.toInt()

  private fun calculateFuelNeeded(mass: Int): Int = (mass / 3) - 2
}
