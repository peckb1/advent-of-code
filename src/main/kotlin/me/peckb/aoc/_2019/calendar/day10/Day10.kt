package me.peckb.aoc._2019.calendar.day10

import com.fasterxml.jackson.annotation.JsonTypeInfo.As
import me.peckb.aoc._2019.calendar.day10.Day10.Slope.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import kotlin.math.abs

class Day10 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { area ->
    val asteroidMap: MutableMap<Asteroid, MutableMap<Slope, MutableList<Asteroid>>> = mutableMapOf<Asteroid, MutableMap<Slope, MutableList<Asteroid>>>()
      .also { setupAsteroid(it, area) }
    val bestAsteroid = bestAsteroid(asteroidMap)

    asteroidMap[bestAsteroid]?.size
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { area ->
    val asteroidMap: MutableMap<Asteroid, MutableMap<Slope, MutableList<Asteroid>>> = mutableMapOf<Asteroid, MutableMap<Slope, MutableList<Asteroid>>>()
      .also { setupAsteroid(it, area) }
    val bestAsteroid = bestAsteroid(asteroidMap)!!

//    val up = Vector2D(0.0, -1.0)
//    val p1 = Vector2D(-1.0, -12.0)
//    val p2 = Vector2D(1.0, -12.0)
//    val a1 = Vector2D.angle(up, p1)
//    val a2 = Vector2D.angle(up, p2)

    val orderOfZapping = asteroidMap[bestAsteroid]?.entries?.sortedBy { (slope, _) ->
      when (slope) {
        is Fraction -> {
          val v1 = Vector2D(slope.denominator.toDouble(), slope.numerator.toDouble())
          val up = Vector2D(0.0, -1.0)
          val angle = Vector2D.angle(up, v1)
          if (slope.denominator < 0) {
            (360.0 * 0.017453) - angle
          } else {
            angle
          }
        }
        NegativeInfinity -> 180.0 * 0.017453
        NegativeZero -> 270.0 * 0.017453
        PositiveInfinity -> 0.0 * 0.017453
        PositiveZero -> 90.0 * 0.017453
      }
    }

    var numZapped = 0
    var done = false
    var lastZapped: Asteroid = Asteroid(0, 0)
    while (!done) {
      done = true
      run zapLoop@{
        orderOfZapping?.forEach { (_, asteroidsAtAngle) ->
          if (asteroidsAtAngle.isNotEmpty()) {
            asteroidsAtAngle.minByOrNull {
              bestAsteroid!!.distanceTo(it)
            }?.let { asteroidToZap ->
              done = false
              asteroidsAtAngle.remove(asteroidToZap)
              lastZapped = asteroidToZap
              numZapped++
            }
          }

          if (numZapped == 200) {
            done = true
            return@zapLoop
          }
        }
      }
    }

    (lastZapped.x * 100) + lastZapped.y
  }

  data class Asteroid(val y: Int, val x: Int) {
    fun distanceTo(them: Asteroid): Int = abs(x - them.x) + abs(y - them.y)
  }

  sealed class Slope {
    object PositiveZero : Slope()
    object NegativeZero : Slope()
    object PositiveInfinity: Slope()
    object NegativeInfinity: Slope()
    data class Fraction(val numerator: Int, val denominator: Int) : Slope() {
      operator fun plus(other: Fraction): Fraction {
        return Fraction(
          numerator = this.numerator * other.denominator + this.denominator * other.numerator,
          denominator = this.denominator * other.denominator
        ).reduced()
      }

      operator fun times(other: Fraction): Fraction {
        return Fraction(
          numerator = this.numerator * other.numerator,
          denominator = this.denominator * other.denominator
        ).reduced()
      }

      fun reduced(): Fraction {
        val gcd = abs(gcd(numerator, denominator))
        return Fraction(numerator / gcd, denominator / gcd)
      }

      private tailrec fun gcd(n1: Int, n2: Int): Int {
        return if (n2 != 0) gcd(n2, n1 % n2) else n1
      }
    }
  }

  private fun setupAsteroid(
    asteroidMap: MutableMap<Asteroid, MutableMap<Slope, MutableList<Asteroid>>>,
    area: Sequence<String>
  ) {
    area.forEachIndexed { yIndex, line ->
      line.forEachIndexed { xIndex, locationChar ->
        if (locationChar == ASTEROID) {
          val asteroid = Asteroid(yIndex, xIndex)
          val myVisibleAsteroids by lazy { asteroidMap.getOrDefault(asteroid, mutableMapOf()) }

          asteroidMap.keys.filterNot { it == asteroid }.forEach { otherAsteroid ->
            val deltaX = asteroid.x - otherAsteroid.x
            val deltaY = asteroid.y - otherAsteroid.y

            val mySlope: Slope
            val theirSlope: Slope

            if (deltaX == 0) {
              // we're an infinity!
              if (deltaY < 0) {
                mySlope = NegativeInfinity
                theirSlope = PositiveInfinity
              } else {
                mySlope = PositiveInfinity
                theirSlope = NegativeInfinity
              }
            } else if (deltaY == 0) {
              // we're a zero slope
              if (deltaX < 0) {
                mySlope = PositiveZero
                theirSlope = NegativeZero
              } else {
                mySlope = NegativeZero
                theirSlope = PositiveZero
              }
            } else {
              // we're actually an angle
              mySlope = Fraction(-deltaY, -deltaX).reduced()
              theirSlope = Fraction(deltaY, deltaX).reduced()
            }

            // handle my slope to it
            val myAsteroidsAtSlope = myVisibleAsteroids.getOrDefault(mySlope, mutableListOf())
            myAsteroidsAtSlope.add(otherAsteroid)
            myVisibleAsteroids[mySlope] = myAsteroidsAtSlope

            // handle its slope to me
            val theirVisibleAsteroids = asteroidMap.getOrDefault(otherAsteroid, mutableMapOf())
            val theirAsteroidsAtSlope = theirVisibleAsteroids.getOrDefault(theirSlope, mutableListOf())
            theirAsteroidsAtSlope.add(asteroid)
            theirVisibleAsteroids[theirSlope] = theirAsteroidsAtSlope
            asteroidMap[otherAsteroid] = theirVisibleAsteroids
          }
          asteroidMap[asteroid] = myVisibleAsteroids
        }
      }
    }
  }

  private fun bestAsteroid(asteroidMap: MutableMap<Asteroid, MutableMap<Slope, MutableList<Asteroid>>>): Asteroid? =
    asteroidMap.maxByOrNull { (_, visibleData) ->
      visibleData.size
    }?.key

  companion object {
    const val ASTEROID = '#'
  }
}
