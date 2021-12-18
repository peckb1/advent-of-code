package me.peckb.aoc._2021.calendar.day18

import kotlin.math.ceil
import kotlin.math.floor

data class SnailFishPair(var parent: SnailFishPair?, var left: FishPair, var right: FishPair, var depth: Int) {
  private val needsExploding get() = depth >= 4
  private val needsLeftSplit get() = left.leftOr(0) > 9
  private val needsRightSplit get() = right.leftOr(0) > 9

  fun explode(): Boolean {
    return if (needsExploding && left.isLiteral() && right.isLiteral()) {
      parent?.addLeft(this, left.leftOr(0))
      parent?.addRight(this, right.leftOr(0))
      parent?.setZero(this)
      true
    } else {
      (left.map { it.explode() }.orNull() ?: false) || (right.map { it.explode() }.orNull()?: false)
    }
  }

  fun split(): Boolean {
    fun split(pair: FishPair): FishPair {
      val half = pair.leftOr(0).toDouble() / 2.0
      val left = floor(half).toInt().toEither()
      val right = ceil(half).toInt().toEither()

      return SnailFishPair(this, left, right, depth + 1).toEither()
    }

    when {
      needsLeftSplit                           -> { left = split(left) }
      left.map { it.split() }.orNull() == true -> return true
      needsRightSplit                          -> { right = split(right) }
      else                                     -> return (right.map { it.split() }.orNull() ?: false)
    }

    return true
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
      left = 0.toEither()
    } else {
      right = 0.toEither()
    }
  }

  fun add(other: SnailFishPair) = SnailFishPair(
    parent = null,
    left = this.toEither(),
    right = other.toEither(),
    depth = 0
  ).also { newParent ->
    newParent.left.becomeChildOf(newParent)
    newParent.right.becomeChildOf(newParent)
  }

  fun magnitude(): Int {
    val leftMagnitude = left.map {it.magnitude() }.get()
    val rightMagnitude = right.map {it.magnitude() }.get()

    return (3 * leftMagnitude) + (2 * rightMagnitude)
  }

  fun incrementDepth() {
    depth++
    left.map { it.incrementDepth() }
    right.map { it.incrementDepth() }
  }
}
