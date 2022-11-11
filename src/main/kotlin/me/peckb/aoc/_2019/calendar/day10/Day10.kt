package me.peckb.aoc._2019.calendar.day10

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

    val upVector = Vector2D(0.0, -1.0)

    val orderOfZapping = asteroidMap[bestAsteroid]?.entries?.sortedBy { (slope, _) ->
      when (slope) {
        is Direction -> {
          val vectorInDirection = Vector2D(slope.deltaX.toDouble(), slope.deltaY.toDouble())
          val angle = Vector2D.angle(upVector, vectorInDirection)
          if (slope.deltaX < 0) { FULL_CIRCLE_RAD - angle} else { angle }
        }
        PositiveInfinity -> UP_ANGLE_RAD
        PositiveZero -> RIGHT_ANGLE_RAD
        NegativeInfinity -> DOWN_ANGLE_RAD
        NegativeZero -> LEFT_ANGLE_RAD
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
              bestAsteroid.distanceTo(it)
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
    data class Direction (val deltaY: Int, val deltaX: Int) : Slope() {
      fun reduced(): Direction {
        val gcd = abs(gcd(deltaY, deltaX))
        return Direction(deltaY / gcd, deltaX / gcd)
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

            val (mySlope, theirSlope) = findSlopes(deltaX, deltaY)

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

  private fun findSlopes(deltaX: Int, deltaY: Int): Pair<Slope, Slope> {
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
      mySlope = Direction(-deltaY, -deltaX).reduced()
      theirSlope = Direction(deltaY, deltaX).reduced()
    }

    return mySlope to theirSlope
  }

  private fun bestAsteroid(asteroidMap: MutableMap<Asteroid, MutableMap<Slope, MutableList<Asteroid>>>): Asteroid? =
    asteroidMap.maxByOrNull { (_, visibleData) ->
      visibleData.size
    }?.key

  companion object {
    private const val ASTEROID = '#'

    private const val DEG_TO_RAD = 0.017453
    private const val UP_ANGLE_RAD = 0.0 * 0.017453
    private const val RIGHT_ANGLE_RAD = 90.0 * 0.017453
    private const val DOWN_ANGLE_RAD = 180.0 * 0.017453
    private const val LEFT_ANGLE_RAD = 270.0 * 0.017453
    private const val FULL_CIRCLE_RAD = 360 * DEG_TO_RAD
  }
}
