package me.peckb.aoc._2019.calendar.day08

import arrow.core.flatten
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day08 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val fewestZerosLayer = pixelColours(input)
      .chunked(WIDTH)
      .chunked(HEIGHT)
      .minByOrNull { layer -> layer.flatten().count { it == BLACK } }

    val dataOccurrences = fewestZerosLayer!!.flatten().groupBy { it }

    dataOccurrences[WHITE]!!.count() * dataOccurrences[TRANSPARENT]!!.count()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    pixelColours(input)
      .chunked(WIDTH)
      .chunked(HEIGHT)
      .fold(ALL_TRANSPARENT_LAYER) { combinedLayer, nextLayer ->
        (0 until HEIGHT).map { h ->
          (0 until WIDTH).map { w ->
            if (combinedLayer[h][w] == TRANSPARENT) nextLayer[h][w] else combinedLayer[h][w]
          }
        }
      }.map { finalLayer ->
        finalLayer.joinToString("")
          .replace(BLACK.toString(), " ")
          .replace(WHITE.toString(), "#")
      }
  }

  private fun pixelColours(line: String) = line.toList().map { it.digitToInt() }

  companion object {
    private const val WIDTH = 25
    private const val HEIGHT = 6

    private const val BLACK = 0
    private const val WHITE = 1
    private const val TRANSPARENT = 2

    val ALL_TRANSPARENT_LAYER = (0 until HEIGHT).map {
      (0 until WIDTH).map {
        TRANSPARENT
      }
    }
  }
}
