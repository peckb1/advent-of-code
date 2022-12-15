package me.peckb.aoc._2022.calendar.day15

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day15 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::day15) { input ->
    var minX = Long.MAX_VALUE
    var minY = Long.MAX_VALUE
    var maxX = Long.MIN_VALUE
    var maxY = Long.MIN_VALUE

    val sensors = input.map { (sensor, beacon) ->
      minX = min(min(minX, beacon.x), sensor.x)
      minY = min(min(minY, beacon.y), sensor.y)
      maxX = max(max(maxX, beacon.x), sensor.x)
      maxY = max(max(maxY, beacon.y), sensor.y)

      sensor
    }.toList()

    val interestY = 2000000L
//    val interestY = 10L

    var counter = 0

    val midX = maxX - minX
    val range = (minX - midX)..(maxX + midX)
    (range).forEach { x ->
      if (sensors.all { sensor -> distanceBetween(x, interestY, sensor.x, sensor.y) > sensor.closestBeaconDistance }) {
//        println("beacon can be at $x")
        counter++
      }
    }

    (range.last - range.first) - counter
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::day15) { input ->
    val data = input.toList()

    fun rowCoverageRange(yIndexOfPossibleGrid: Long): List<Pair<Long, Long>> {
      return data.mapNotNull { (sensor, _) ->
        // what's the distance to our closest beacon?
        val distance = sensor.closestBeaconDistance
        // what's the distance between `y` value of our sensor, or the y index of our grid
        val yDistanceOffset = abs(sensor.y - yIndexOfPossibleGrid)

        // if distance from sensor to the row we're at is larger than the closest beacon
        // then this row will not have any "coverage" inside the area of the sensor,
        // so we care about when there will be part of this row that fall within
        // the same range of the sensor to its closest beacon
        if (yDistanceOffset > distance) {
          null
        } else {
          // the only part of the row that has coverage are also items that fit within the
          // sensor distance so the Sensor at x=13, y=2 on row 0 would have coverage
          // from x = 12 to x = 14
          val startXDistanceOffset = sensor.x - (distance - yDistanceOffset)
          val endXDistanceOffset = sensor.x + (distance - yDistanceOffset)
          startXDistanceOffset to endXDistanceOffset
        }
      }
    }

    fun getMissingFrequency(maxWidth: Long): Long {
      (0..maxWidth).forEach { row ->
        // this will be the current maximum x value for a given row that is covered by a sensor
        // and within the distance do its closest beacon
        var maxCoverageXIndex = 0L
        // this gives us a list xValue ranges along the row that sensors
        // would consider within the distance from them to their closest beacon
        val coverageRanges = rowCoverageRange(row)
          .sortedBy { it.first } // sorted to start from the front of the row first

        // we iterate over that range and see if we can find a "gap"
        // any gap between ranges indicates that in a given row there is a point surrounded
        // by coverage, but not covered itself.
        // This would be the single point that is surrounded by Sensors, but not close enough
        // to be the close beacon, and therefore it is our point!
        coverageRanges.forEach { (startX, endX) ->
          // if the start of our x value is one past (as our ranges are inclusive) the previous maximum
          // then we have just jumped passed the gap and the previous X for this row is our hidden beacon!
          if (startX > maxCoverageXIndex + 1) {
            return (startX - 1) * 4_000_000 + row
          }

          // if this range isn't a gap, then ensure that our current upper X index
          // is the largest X index that we have seen so far
          maxCoverageXIndex = max(maxCoverageXIndex, endX)
        }
      }
      return -1
    }

    getMissingFrequency(4_000_000)
  }

  private fun day15(line: String): Pair<Sensor, Beacon> {
    val (sensorDescription, beaconDescription) = line.split(":")

    val beacon = beaconDescription.split(" at ")[1].split(", ").let { (xData, yData) ->
      Beacon(
        x = xData.split("=")[1].toLong(),
        y = yData.split("=")[1].toLong()
      )
    }
    val sensor = sensorDescription.split(" at ")[1].split(", ").let { (xData, yData) ->
      val x = xData.split("=")[1].toLong()
      val y = yData.split("=")[1].toLong()
      Sensor(
        x = x,
        y = y,
        closestBeaconDistance = distanceBetween(x, y, beacon.x, beacon.y)
      )
    }
    return sensor to beacon
  }

  private fun distanceBetween(x1: Long, y1: Long, x2: Long, y2: Long) = abs(x1 - x2) + abs(y1 - y2)

  data class Sensor(val x: Long, val y: Long, val closestBeaconDistance: Long)
  data class Beacon(val x: Long, val y: Long)
}
