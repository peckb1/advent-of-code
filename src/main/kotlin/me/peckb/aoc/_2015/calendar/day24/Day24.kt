package me.peckb.aoc._2015.calendar.day24

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.min

class Day24 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  /**
   * There are sums that hit our `evenThreePoint` using only five numbers, so find the smallest six
   */
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::`package`) { input ->
    val packages = input.toList()
    val evenThreePoint = packages.sum() / 3

    var bestSixSum = Long.MAX_VALUE

    packages.indices.forEach { a ->
      (a + 1 until packages.size).forEach { b ->
        (b + 1 until packages.size).forEach { c ->
          (c + 1 until packages.size).forEach { d ->
            (d +1 until packages.size).forEach { e ->
              (e + 1 until packages.size).forEach { f ->
                val sixSum = packages[a] + packages[b] + packages[c] + packages[d] + packages[e] + packages[f]
                if (sixSum == evenThreePoint) {
                  bestSixSum = min(bestSixSum, packages[a] * packages[b] * packages[c] * packages[d] * packages[e] * packages[f])
                }
              }
            }
          }
        }
      }
    }

    bestSixSum
  }

  /**
   * There are sums that hit our `evenFourPoint` using only four numbers, so find the smallest five
   */
  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::`package`) { input ->
    val packages = input.toList()
    val evenFourPoint = packages.sum() / 4

    var bestFiveSum = Long.MAX_VALUE

    packages.indices.forEach { a ->
      (a + 1 until packages.size).forEach { b ->
        (b + 1 until packages.size).forEach { c ->
          (c + 1 until packages.size).forEach { d ->
            (d + 1 until packages.size).forEach { e ->
              val fiveSum = packages[a] + packages[b] + packages[c] + packages[d] + packages[e]
              if (fiveSum == evenFourPoint) {
                bestFiveSum = min(bestFiveSum, packages[a] * packages[b] * packages[c] * packages[d] * packages[e])
              }
            }
          }
        }
      }
    }

    bestFiveSum
  }

  private fun `package`(line: String) = line.toLong()
}
