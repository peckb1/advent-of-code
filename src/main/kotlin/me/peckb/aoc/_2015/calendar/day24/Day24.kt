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
      packages.indices.forEach loopB@ { b ->
        if (b == a) return@loopB
        packages.indices.forEach loopC@ { c ->
          if (c == a || c == b) return@loopC
          packages.indices.forEach loopD@ { d ->
            if (d == a || d == b || d == c) return@loopD
            packages.indices.forEach loopE@ { e ->
              if (e == a || e == b || e == c || e == d) return@loopE
              packages.indices.forEach loopF@ { f ->
                if (f == a || f == b || f == c || f == d || f == e) return@loopF
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
      packages.indices.forEach loopB@ { b ->
        if (b == a) return@loopB
        packages.indices.forEach loopC@ { c ->
          if (c == a || c == b) return@loopC
          packages.indices.forEach loopD@ { d ->
            if (d == a || d == b || d == c) return@loopD
            packages.indices.forEach loopE@ { e ->
              if (e == a || e == b || e == c || e == d) return@loopE
              val sixSum = packages[a] + packages[b] + packages[c] + packages[d] + packages[e]
              if (sixSum == evenFourPoint) {
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
