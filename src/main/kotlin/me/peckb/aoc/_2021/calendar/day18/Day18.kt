package me.peckb.aoc._2021.calendar.day18

import arrow.core.Either
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.math.max

typealias FishPair = Either<Int, SnailFishPair>

@Suppress("ControlFlowWithEmptyBody")
class Day18 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {

  fun findMagnitude(fileName: String) =
    generatorFactory.forFile(fileName).readAs(::snailFishPair) { input ->
      input.reduce { acc, next ->
        acc.add(next).apply { while (this.explode() || this.split()); }
      }.magnitude()
    }

  fun findBestTwoPartMagnitude(fileName: String) =
    generatorFactory.forFile(fileName).read { input ->
      val data = input.toList()

      var largestMagnitude = Int.MIN_VALUE

      (data.indices).forEach { a ->
        ((a + 1) until data.size).forEach { b ->
          val firstString = data[a]
          val secondString = data[b]

          fun getMagnitude(a: String, b: String) = snailFishPair(a).add(snailFishPair(b))
            .apply { while (this.explode() || this.split()); }
            .magnitude()

          largestMagnitude = max(largestMagnitude, getMagnitude(firstString, secondString))
          largestMagnitude = max(largestMagnitude, getMagnitude(secondString, firstString))
        }
      }

      largestMagnitude
    }

  private fun snailFishPair(line: String): SnailFishPair {
    return getPair(line, AtomicInteger(0), 0).getOrNull()!!
  }

  private fun getPair(line: String, index: AtomicInteger, depth: Int): FishPair {
    lateinit var newLeft: FishPair
    lateinit var newRight: FishPair

    var indexInt = index.getAndIncrement()
    while (indexInt < line.length) {
      when (val nextChar = line[indexInt]) {
        '[' -> newLeft = getPair(line, index, depth + 1)
        ',' -> newRight = getPair(line, index, depth + 1)
        ']' -> return SnailFishPair(null, newLeft, newRight, depth).apply {
          left.map { it.parent = this }
          right.map { it.parent = this }
        }.toEither()
        else -> return Character.getNumericValue(nextChar).toEither()
      }
      indexInt = index.getAndIncrement()
    }

    throw IllegalStateException("Mismatched Pairs")
  }
}
