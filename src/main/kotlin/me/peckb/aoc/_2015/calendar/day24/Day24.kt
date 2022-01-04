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
      val pa = packages[a]
      (a + 1 until packages.size).forEach { b ->
        val pb = packages[b]
        (b + 1 until packages.size).forEach { c ->
          val pc = packages[c]
          (c + 1 until packages.size).forEach { d ->
            val pd = packages[d]
            (d + 1 until packages.size).forEach { e ->
              val pe = packages[e]
              (e + 1 until packages.size).forEach { f ->
                val pf = packages[f]
                val sixSum = pa + pb + pc + pd + pe + pf
                if (sixSum == evenThreePoint) {
                  bestSixSum = min(bestSixSum, pa * pb * pc * pd * pe * pf)
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
      val pa = packages[a]
      (a + 1 until packages.size).forEach { b ->
        val pb = packages[b]
        (b + 1 until packages.size).forEach { c ->
          val pc = packages[c]
          (c + 1 until packages.size).forEach { d ->
            val pd = packages[d]
            (d + 1 until packages.size).forEach { e ->
              val pe = packages[e]
              val fiveSum = pa + pb + pc + pd + pe
              if (fiveSum == evenFourPoint) {
                bestFiveSum = min(bestFiveSum, pa * pb * pc * pd * pe)
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
