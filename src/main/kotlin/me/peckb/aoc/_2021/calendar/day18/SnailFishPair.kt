package me.peckb.aoc._2021.calendar.day18

import arrow.core.Either
import kotlin.math.ceil

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
