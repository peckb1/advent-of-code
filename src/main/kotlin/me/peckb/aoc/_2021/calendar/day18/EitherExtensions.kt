package me.peckb.aoc._2021.calendar.day18

import arrow.core.Either

fun SnailFishPair.toEither(): Either<Int, SnailFishPair> = Either.Right(this)

fun Int.toEither(): Either<Int, SnailFishPair> = Either.Left(this)

fun <T> Either<T, T>.get(): T = this.fold({ it }, { it })

fun <A, B> Either<A, B>.leftOr(default: A): A = this.fold({ it }, { default })

fun Either<Int, SnailFishPair>.isLiteral() = this.isLeft()

fun <T> Either<T, SnailFishPair>.becomeChildOf(parent: SnailFishPair) = this.map {
  it.parent = parent
  it.incrementDepth()
}