package me.peckb.aoc._2021.calendar.day18

import arrow.core.Either
import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.round

class Day18 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {

  fun partOne(fileName: String) =
    generatorFactory.forFile(fileName).readAs(::snailFishPair) { input ->
      val data = input.toList()
      val added = data.reduce(SnailFishPair::add)
      println(added)
      var performedReduce = added.reduce()
      println(added)
      while (performedReduce) {
        performedReduce = added.reduce()
        println(added)
      }

      -1
    }

  fun partTwo(fileName: String) =
    generatorFactory.forFile(fileName).readAs(::snailFishPair) { input ->
      -1
    }

  private fun snailFishPair(line: String): SnailFishPair {
    return getPair(line, AtomicInteger(0), 0).orNull()!!
  }

  private fun getPair(line: String, index: AtomicInteger, depth: Int): Either<Int, SnailFishPair> {
    lateinit var left: Either<Int, SnailFishPair>
    lateinit var right: Either<Int, SnailFishPair>

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

  data class SnailFishPair(
    var parent: SnailFishPair?,
    var left: Either<Int, SnailFishPair>,
    var right: Either<Int, SnailFishPair>,
    var depth: Int
  ) {
    val needsExploding get() = depth >= 4

    val needsLeftSplit get() = left.swap().orNull() ?: 0 > 9
    val needsRightSplit get() = right.swap().orNull() ?: 0 > 9

    fun reduce(): Boolean {
      if (needsExploding) {
        if (left.isLeft() && right.isLeft()) {
          // we can explode, because both of our values are ints
          parent?.addLeft(this, left.swap().orNull()!!)
          parent?.addRight(this, right.swap().orNull()!!)
          parent?.setZero(this)
          return true
        }
      } else if (needsLeftSplit) {
        val leftInt = left.swap().orNull()!!
        val leftLeft = Either.Left(leftInt / 2)
        val leftRight = Either.Left(round(leftInt.toDouble() / 2.0).toInt())

        left = Either.Right(SnailFishPair(this, leftLeft, leftRight, depth + 1).also { it.parent = this })
        left.map{ it.reduce() }
        return true
      } else if (needsRightSplit) {
        val rightInt = right.swap().orNull()!!
        val rightLeft = Either.Left(rightInt / 2)
        val rightRight = Either.Left(ceil(rightInt.toDouble() / 2.0).toInt())

        right = Either.Right(SnailFishPair(this, rightLeft, rightRight, depth + 1).also { it.parent = this })
        right.map{ it.reduce() }
        return true
      } else {
        return (left.map { it.reduce() }.orNull() ?: false) || (right.map { it.reduce() }.orNull() ?: false)
      }
      return false
    }

    fun addLeft(pair: SnailFishPair, n: Int): SnailFishPair {
      if (pair === right.orNull()) {
        // our right child told us to add to its left
        left = left.bimap(
          { it + n },
          { it.addRight(this, n) }
        )
      } else if (pair === left.orNull()) {
        // our left child told us to add to its left
        parent?.addLeft(this, n)
      } else {
        // our parent told us to add to the left
        left = left.bimap(
          { it + n },
          { it.addRight(this, n) }
        )
      }
      return this
    }

    fun addRight(pair: SnailFishPair, n: Int): SnailFishPair {
      if (pair === left.orNull()) {
        // our left child just told us to add to its right
        right = right.bimap(
          { it + n },
          { it.addLeft(this, n) }
        )
      } else if (pair === right.orNull()) {
        // our right child told us to add to its right
        parent?.addRight(this, n)
      } else {
        // our parent told us to add to the right
        right = right.bimap(
          { it + n },
          { it.addLeft(this, n) }
        )
      }
      return this
    }

    private fun setZero(child: SnailFishPair) {
      if (child === left.orNull()) {
        left = Either.Left(0)
      } else {
        right = Either.Left(0)
      }
    }

    fun add(other: SnailFishPair) = SnailFishPair(
      null,
      left = Either.Right(this.also { it.incrementDepth() }),
      right = Either.Right(other.also { it.incrementDepth() }),
      depth = 0
    ).also { me ->
      me.left.map { it.parent = me }
      me.right.map { it.parent = me }
    }

    private fun incrementDepth() {
      depth++
      left.map { it.incrementDepth() }
      right.map { it.incrementDepth() }
    }

    override fun toString(): String {
      return "[${left.fold({ it.toString() }, { it.toString() })},${
        right.fold({ it.toString() },
          { it.toString() })
      }]"
    }
  }
}
