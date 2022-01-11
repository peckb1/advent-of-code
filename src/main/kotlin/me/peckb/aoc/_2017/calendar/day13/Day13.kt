package me.peckb.aoc._2017.calendar.day13

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day13 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::layer) { input ->
    val layers = input.toList()
    val fireWall = Array<Layer?>(layers.last().id + 1) { null }.apply {
      layers.forEach { layer -> this[layer.id] = layer }
    }

    findSeverity(layers, fireWall)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::layer) { input ->
    val data = input.toList()

    (0..Int.MAX_VALUE).first { timeStart ->
      data.map { layer ->  (layer.id + timeStart) % (layer.maxIndex * 2) }
        .none { indexAtTimeWeGetThere -> indexAtTimeWeGetThere == 0 }
    }
  }

  private fun findSeverity(layers: List<Layer>, fireWall: Array<Layer?>): Int? {
    var severity: Int? = null
    fireWall.indices.forEach { time ->
      // move player
      val currentLocation = fireWall[time]
      currentLocation?.let { layer ->
        if (layer.scannerIndex == 0) {
          val localSeverity = time * (layer.maxIndex + 1)
          severity = severity?.let { it + localSeverity } ?: localSeverity
        }
      }

      // move scanner
      moveScanner(layers)
    }

    return severity
  }

  private fun moveScanner(layers: List<Layer>) {
    layers.forEach {
      if (it.scanningDown) {
        if (it.scannerIndex < it.maxIndex) {
          it.scannerIndex += 1
        } else {
          it.scanningDown = false
          it.scannerIndex -= 1
        }
      } else {
        if (it.scannerIndex > 0) {
          it.scannerIndex -= 1
        } else {
          it.scanningDown = true
          it.scannerIndex += 1
        }
      }
    }
  }

  private fun layer(line: String): Layer {
    val (id, depth) = line.split(": ").map { it.toInt() }
    return Layer(id, depth - 1)
  }

  data class Layer(val id: Int, val maxIndex: Int, var scannerIndex: Int = 0, var scanningDown: Boolean = true)
}
