package me.peckb.aoc._2022.calendar.day15

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day15 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::sensors) { input ->
    val y = 2000000
    var minX = Int.MAX_VALUE
    var maxX = Int.MIN_VALUE

    val sensors = input.map { sensor->
      minX = min(minX, sensor.x)
      maxX = max(maxX, sensor.x)
      sensor
    }.toList()

    var inverseCoverage = 0

    // since we're looking for inverse coverage, give ourselves a little buffer
    val buffer = (maxX - minX) / 4
    val range = (minX - buffer)..(maxX + buffer)

    range.forEach { x ->
      // if none the beacons have coverage on this point increment our inverse coverage
      if (sensors.none { sensor -> distanceBetween(x, y, sensor.x, sensor.y) <= sensor.closestBeaconDistance }) {
        inverseCoverage++
      }
    }

    // then we can just subtract the inverse range from our total range to get
    // a counter of the actual coverage we have
    (range.last - range.first) - inverseCoverage
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::sensors) { input ->
    getMissingFrequency(input.toList())
  }

  // split out into a new function for doing early return shenanigans
  private fun getMissingFrequency(sensors: List<Sensor>): Long {
    (0..4_000_000).forEach { row ->
      // this will be the current maximum x value for a given row that is covered by a sensor
      // and within the distance do its closest beacon
      var maxCoverageXIndex = 0
      // this gives us a list xValue ranges aInt the row that sensors
      // would consider within the distance from them to their closest beacon
      val coverageRanges = rowCoverageRange(sensors, row)
        .sortedBy { it.first } // sorted to start from the front of the row first

      // we iterate over that range and see if we can find a "gap"
      // any gap between ranges indicates that in a given row there is a point surrounded
      // by coverage, but not covered itself.
      // This would be the single point that is surrounded by Sensors, but not close enough
      // to be the close beacon, and therefore it is our point!
      coverageRanges.forEach { (startX, endX) ->
        // if the start of our x value is one past (as our ranges are inclusive) the previous maximum
        // then we have just jumped passed the gap and the previous X for this row is our hidden beacon!
        if (startX > maxCoverageXIndex + 1) return (startX - 1) * 4_000_000L + row

        // if this range isn't a gap, then ensure that our current upper X index
        // is the largest X index that we have seen so far
        maxCoverageXIndex = max(maxCoverageXIndex, endX)
      }
    }
    return -1
  }

  private fun rowCoverageRange(sensors: List<Sensor>, yIndexOfPossibleGrid: Int): List<Pair<Int, Int>> {
    return sensors.mapNotNull { sensor ->
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

  private fun sensors(line: String): Sensor {
    val (sensorDescription, beaconDescription) = line.split(":")

    val (beaconX, beaconY) = beaconDescription.split(" at ")[1].split(", ").let { (xData, yData) ->
      asInt(xData) to asInt(yData)
    }

    return sensorDescription.split(" at ")[1].split(", ").let { (xData, yData) ->
      val x = asInt(xData)
      val y = asInt(yData)
      Sensor(x, y, distanceBetween(x, y, beaconX, beaconY))
    }
  }

  private fun asInt(input: String) = input.split("=")[1].toInt()

  private fun distanceBetween(x1: Int, y1: Int, x2: Int, y2: Int) = abs(x1 - x2) + abs(y1 - y2)

  data class Sensor(val x: Int, val y: Int, val closestBeaconDistance: Int)
}
