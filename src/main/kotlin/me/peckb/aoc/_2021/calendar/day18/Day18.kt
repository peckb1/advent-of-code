package me.peckb.aoc._2021.calendar.day18

import arrow.core.Either
import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.max

class Day18 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {

  fun partOne(fileName: String) =
    generatorFactory.forFile(fileName).readAs(::snailFishPair) { input ->
      val data = input.toList()
      val added = data.reduce { acc, next ->
        println("$acc + $next")
        val addedPair = acc.add(next)
        // println(addedPair)
        // addedPair.deepPrint()
        while(addedPair.explode() || addedPair.split()) {
          // println(addedPair)
        }
        println()
        addedPair
      }

      // while(added.explode() || added.split()) {
      //   println(added)
      // }

      println(added)

      added.magnitude()
    }

  fun partTwo(fileName: String) =
    generatorFactory.forFile(fileName).read { input ->
      val data = input.toList()

      var largestMagnitude = Int.MIN_VALUE

      repeat(data.size) { a ->
        ((a + 1) until data.size).forEach{ b ->
          println("($a, $b) Comparing ${data[a]} and ${data[b]}}")

          val firstString = data[a]
          val secondString = data[b]

          val aPlusB = snailFishPair(firstString).add(snailFishPair(secondString))
          while(aPlusB.explode() || aPlusB.split()) { /* */ }
          largestMagnitude = max(largestMagnitude, aPlusB.magnitude())

          val bPlusA = snailFishPair(secondString).add(snailFishPair(firstString))
          while(bPlusA.explode() || bPlusA.split()) { /* */ }
          largestMagnitude = max(largestMagnitude, bPlusA.magnitude())
        }
      }

      largestMagnitude
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

    fun explode(): Boolean {
      return if (needsExploding && left.isLeft() && right.isLeft()) {
        parent?.addLeft(this, left.swap().orNull()!!)
        parent?.addRight(this, right.swap().orNull()!!)
        parent?.setZero(this)
        // println("exploded!")
        // println()
        true
      } else {
        // println("checking kids for explosion")
        (left.map { it.explode() }.orNull() ?: false) || (right.map { it.explode() }.orNull() ?: false)
      }
    }

    fun split(): Boolean {
      return if (needsLeftSplit) {
        val leftInt = left.swap().orNull()!!
        val leftLeft = Either.Left(leftInt / 2)
        val leftRight = Either.Left(ceil(leftInt.toDouble() / 2.0).toInt())

        // println("maths: $leftInt -> [$leftLeft, $leftRight]")

        left = Either.Right(SnailFishPair(this, leftLeft, leftRight, depth + 1).also {
          it.parent = this
        })
        // left.map{ it.expl() }
        // println("split!")
        // println()
        true
      } else if (left.map { it.split() }.orNull() == true) {
        true
      } else if (needsRightSplit) {
        val rightInt = right.swap().orNull()!!
        val rightLeft = Either.Left(rightInt / 2)
        val rightRight = Either.Left(ceil(rightInt.toDouble() / 2.0).toInt())

        // println("maths: $rightInt -> [$rightLeft, $rightRight]")

        right = Either.Right(SnailFishPair(this, rightLeft, rightRight, depth + 1).also { it.parent = this })
        // right.map{ it.reduce() }
        // println("split!")
        // println()
        true
      } else {
        // println("checking kids for splits")
        (right.map { it.split() }.orNull() ?: false)
      }
    }

    fun addLeft(pair: SnailFishPair, n: Int): SnailFishPair {
      if (pair === right.orNull()) {
        // our right child told us to add to its left
        // println("Right Child $pair told me $this to add $n to its left, which should be $left")
        left = left.bimap(
          {
            // println("adding $n to $it")
            it + n
          },
          { it.addRight(this, n) }
        )
      } else if (pair === left.orNull()) {
        // our left child told us to add to its left
        // println("Left Child $pair told me $this to add $n to the left")
        parent?.addLeft(this, n)// ?: println("throwing away $n")
      } else {
        // our parent told us to add to the left
        // println("My parent $pair told me $this to add $n to my left")
        left = left.bimap(
          {
            // println("adding $n to $it")
            it + n
          },
          { it.addLeft(this, n) }
        )
      }
      return this
    }

    fun addRight(pair: SnailFishPair, n: Int): SnailFishPair {
      if (pair === left.orNull()) {
        // our left child just told us to add to its right
        // println("Left Child $pair told me $this to add $n to its right, which should be $right")
        right = right.bimap(
          {
            // println("adding $n to $it")
            it + n
          },
          { it.addLeft(this, n) }
        )
      } else if (pair === right.orNull()) {
        // our right child told us to add to its right
        // println("Right Child $pair told me $this to add $n to the right")
        parent?.addRight(this, n)// ?: println("throwing away $n")
      } else {
        // our parent told us to add to the right
        // println("My parent $pair told me $this to add $n to my right")
        right = right.bimap(
          {
            // println("adding $n to $it")
            it + n
          },
          { it.addRight(this, n) }
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

    fun add(other: SnailFishPair) : SnailFishPair {
      val xx = SnailFishPair(
        null,
        left = Either.Right(this.also { it.incrementDepth() }),
        right = Either.Right(other.also { it.incrementDepth() }),
        depth = 0
      ).also { me ->
        me.left.map { it.parent = me }
        me.right.map { it.parent = me }
      }

      return xx
    }

    fun magnitude(): Int {
      // The magnitude of a pair is 3 times the magnitude of its left element plus 2 times the magnitude of its right element.
      val leftMagnitude = left.bimap(
        { it },
        { it.magnitude()}
      ).fold({it},{it})
      val rightMagnitude = right.bimap(
        { it },
        { it.magnitude() }
      ).fold({it},{it})

      return (3 * leftMagnitude) + (2 * rightMagnitude)
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

    fun deepPrint() {
      println("me: ($this \n\tleft: $left \n\tright: $right \n\tdepth: $depth \n\tparent: ${parent}")
      left.map { it.deepPrint() }
      right.map { it.deepPrint() }
    }
  }
}
