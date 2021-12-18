package me.peckb.aoc._2021.calendar.day18

import arrow.core.Either
import me.peckb.aoc._2021.calendar.day18.Day18.SnailFishPair
import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.max

typealias FishPair = Either<Int, SnailFishPair>

@Suppress("ControlFlowWithEmptyBody")
class Day18 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {

  fun findMagnitude(fileName: String) =
    generatorFactory.forFile(fileName).readAs(::snailFishPair) { input ->
      val data = input.toList()

      val added = data.reduce { acc, next ->
        acc.add(next).apply { while (this.explode() || this.split()) { } }
      }

      added.magnitude()
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
            .apply { while (this.explode() || this.split()) {} }
            .magnitude()

          largestMagnitude = max(largestMagnitude, getMagnitude(firstString, secondString))
          largestMagnitude = max(largestMagnitude, getMagnitude(secondString, firstString))
        }
      }

      largestMagnitude
    }

  private fun snailFishPair(line: String): SnailFishPair {
    return getPair(line, AtomicInteger(0), 0).orNull()!!
  }

  private fun getPair(line: String, index: AtomicInteger, depth: Int): FishPair {
    lateinit var left: FishPair
    lateinit var right: FishPair

    var indexInt = index.getAndIncrement()
    while (indexInt < line.length) {
      when (val nextChar = line[indexInt]) {
        '[' -> left = getPair(line, index, depth + 1)
        ',' -> right = getPair(line, index, depth + 1)
        ']' -> return Either.Right(SnailFishPair(null, left, right, depth).also { me ->
          me.left.map { it.parent = me }
          me.right.map { it.parent = me }
        })
        else -> return Either.Left(Character.getNumericValue(nextChar))
      }
      indexInt = index.getAndIncrement()
    }

    throw IllegalStateException("Mismatched Pairs")
  }

  data class SnailFishPair(var parent: SnailFishPair?, var left: FishPair, var right: FishPair, var depth: Int ) {
    private val needsExploding get() = depth >= 4
    private val needsLeftSplit get() = left.swap().orNull() ?: 0 > 9
    private val needsRightSplit get() = right.swap().orNull() ?: 0 > 9

    fun explode(): Boolean {
      return if (needsExploding && left.isLeft() && right.isLeft()) {
        parent?.addLeft(this, left.swap().orNull()!!)
        parent?.addRight(this, right.swap().orNull()!!)
        parent?.setZero(this)
        true
      } else {
        (left.map { it.explode() }.orNull() ?: false) || (right.map { it.explode() }.orNull()?: false)
      }
    }

    fun split(): Boolean {
      fun split(pair: Either<Int, SnailFishPair>): Either<Int, SnailFishPair> {
        val int = pair.swap().orNull()!!
        val left = Either.Left(int / 2)
        val right = Either.Left(ceil(int.toDouble() / 2.0).toInt())

        return Either.Right(SnailFishPair(this, left, right, depth + 1))
      }

      return when {
        needsLeftSplit                           -> { left = split(left); true; }
        left.map { it.split() }.orNull() == true -> true
        needsRightSplit                          -> { right = split(right); true; }
        else                                     -> (right.map { it.split() }.orNull() ?: false)
      }
    }

    private fun addLeft(pair: SnailFishPair, n: Int): SnailFishPair = this.also { _ ->
      when {
        // our right child told us to add to its left
        pair === right.orNull() -> left = left.bimap({ it + n }, { it.addRight(this, n) })
        // our left child told us to add to its left
        pair === left.orNull()  -> parent?.addLeft(this, n)
        // our parent told us to add to the left
        else                    -> left = left.bimap({ it + n }, { it.addLeft(this, n) })
      }
    }

    private fun addRight(pair: SnailFishPair, n: Int): SnailFishPair = this.also { _ ->
      when {
        // our left child just told us to add to its right
        pair === left.orNull()  -> right = right.bimap({ it + n }, { it.addLeft(this, n) })
        // our right child told us to add to its right
        pair === right.orNull() -> parent?.addRight(this, n)
        // our parent told us to add to the right
        else                    -> right = right.bimap({ it + n }, { it.addRight(this, n) })
      }
    }

    private fun setZero(child: SnailFishPair) {
      if (child === left.orNull()) {
        left = Either.Left(0)
      } else {
        right = Either.Left(0)
      }
    }

    fun add(other: SnailFishPair) = SnailFishPair(
      parent = null,
      left = Either.Right(this),
      right = Either.Right(other),
      depth = 0
    ).also { me ->
      me.left.becomeChildOf(me)
      me.right.becomeChildOf(me)
    }

    fun magnitude(): Int {
      val leftMagnitude = left.map {it.magnitude() }.get()
      val rightMagnitude = right.map {it.magnitude() }.get()

      return (3 * leftMagnitude) + (2 * rightMagnitude)
    }

    private fun incrementDepth() {
      depth++
      left.map { it.incrementDepth() }
      right.map { it.incrementDepth() }
    }

    private fun <T> Either<T, T>.get(): T = this.fold({ it }, { it })

    private fun Either<Int, SnailFishPair>.becomeChildOf(parent: SnailFishPair) = this.map {
      it.parent = parent
      it.incrementDepth()
    }
  }
}

