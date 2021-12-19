package me.peckb.aoc._2021.calendar.day19

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class Day19 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val iterator = input.iterator()

    // val scannerData: MutableMap<Int, MutableSet<Point>> = mutableMapOf()

    val scannerData: MutableMap<Int, Scanner> = mutableMapOf()

    while(iterator.hasNext()) {
      val scannerId = iterator.next().split(" ")[2].toInt()
      var doneWithScanner = false
      var beaconId = 0
      while(!doneWithScanner && iterator.hasNext()) {
        val pointLine = iterator.next()

        if (pointLine.isEmpty()) {
          doneWithScanner = true
        } else {
          val (x, y, z) = pointLine.split(",")
          scannerData.compute(scannerId) { _, scanner ->
            (scanner ?: Scanner(mutableListOf())).apply {
              this.beaconPoints.add(Point(x.toInt(), y.toInt(), z.toInt()).also { it.id = beaconId })
            }
          }
        }
        beaconId++
      }
    }

    val scannerIdToBeaconReferenceViews = scannerData.mapValues { (scannerId, scanner) ->
      scanner.beaconPoints.mapIndexed { beaconIndex, point ->
        val neighbors = (0 until scanner.beaconPoints.size).mapNotNull { newNeighborBeaconIndex ->
          if (newNeighborBeaconIndex == beaconIndex) {
            null
          } else {
            val neighborBeacon = scanner.beaconPoints[newNeighborBeaconIndex]
            neighborBeacon.subtract(point)
          }
        }
        Beacon(point.id, neighbors, point.reverseDirection(scannerId))
      }
    }

    scannerIdToBeaconReferenceViews.forEach { (myScannerId, myBeacons) ->
      println()
      println("Checking for an overlapping Scanner from Scanner ID: $myScannerId")
      println()
      scannerIdToBeaconReferenceViews.forEach name@ { (theirScannerId, theirBeacons) ->
        myBeacons.forEach { beacon ->
          val neighbors = beacon.neighbors
          val rotations = listOf(0, 90, 180, 270)
          theirBeacons.forEach { neighborBeacon ->
            if (!(neighborBeacon === beacon)) {
              rotations.forEach { rotationX ->
                val a = neighbors.map { rotateAroundX(rotationX, it) }
                rotations.forEach { rotationY ->
                  val b = a.map { rotateAroundY(rotationY, it) }
                  rotations.forEach { rotationZ ->
                    val x = b.map { rotateAroundZ(rotationZ, it) }

                    val matchingNeighborBeacons = neighborBeacon.neighbors.intersect(x)

                    if (matchingNeighborBeacons.size >= 11) {
                      println("Scanner $myScannerId's point ${beacon.id} matches Scanner $theirScannerId's point ${neighborBeacon.id} ")
                      matchingNeighborBeacons.forEach { matchingNeighbor ->
                        x.forEach { myNeighbor ->
                          if (myNeighbor == matchingNeighbor) {
                            println("Scanner $myScannerId's point ${myNeighbor.id} matches Scanner $theirScannerId's point ${matchingNeighbor.id} ")
                          }
                        }
                      }
                      return@name
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    -1
    /*

...B.
B....
....B
S....

...B..
B....S
....B.

...B..
B....S
....B.
S.....

     */

    // val fullData = scannerData.mapValues { (_, points) ->
    //   points.map { point ->
    //     (1..4).flatMap { rotationA ->
    //       val newPoints = mutableListOf<Point>()
    //       val initialOrientationPoint = rotateAroundX(rotationA, point)
    //       (1..4).forEach { rotationB ->
    //         // get four y axis rotations for the initial initialOrientationPoint
    //         newPoints.add(rotateAroundY(rotationB, initialOrientationPoint))
    //       }
    //       // also get a 90 degree around Z, and a 270 around Z
    //       newPoints.add(rotateAroundZ(90, initialOrientationPoint))
    //       newPoints.add(rotateAroundZ(270, initialOrientationPoint))
    //       // we should have 6 Points per rotation A (four rotationA) giving us all 24 rotations
    //       newPoints
    //     }//.toSet() // TODO: confirm set is ok here
    //   }
    // }
    //
    // val scanner0 = fullData[0]!!
    // val scanner1 = fullData[1]!!
    //
    // scanner0.forEach { scanner0OrientationPoints ->
    //   scanner1.forEach { scanner1OrientationPoints ->
    //     val union = scanner0OrientationPoints.intersect(scanner1OrientationPoints)
    //     if (union.size >= 1) {
    //       -1
    //     }
    //   }
    // }


    -1
  }

  private fun rotateAroundX(angle: Int, point: Point): Point {
    val (x, y, z) = point
    val theta = angle * (Math.PI / 180)

    val newX = x
    val newY = (y.toDouble() * cos(theta) - z.toDouble() * sin(theta)).roundToInt()
    val newZ = (y.toDouble() * sin(theta) + z.toDouble() * cos(theta)).roundToInt()

    return Point(newX, newY, newZ).also { it.id = point.id }
  }

  private fun rotateAroundY(angle: Int, point: Point): Point {
    val (x, y, z) = point
    val theta = angle * (Math.PI / 180)

    val newX = (x.toDouble()*cos(theta) + z.toDouble()*sin(theta)).roundToInt()
    val newY = y
    val newZ = (z.toDouble()*cos(theta) - x.toDouble()*sin(theta)).roundToInt()

    return Point(newX, newY, newZ).also { it.id = point.id }
  }

  private fun rotateAroundZ(angle: Int, point: Point): Point {
    val (x, y, z) = point
    val theta = angle * (Math.PI / 180)

    val newX = (x.toDouble()*cos(theta) - y.toDouble()*sin(theta)).roundToInt()
    val newY = (x.toDouble()*sin(theta) + y.toDouble()*cos(theta)).roundToInt()
    val newZ = z

    return Point(newX, newY, newZ).also { it.id = point.id}
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day19) { input ->
    -1
  }

  fun day19(line: String) = -4

  data class Beacon(val id: Int, val neighbors: List<Point>, val scannerPoint: Point)

  data class Scanner(val beaconPoints: MutableList<Point>)

  data class Point(val x: Int, val y: Int, val z: Int) {
    var id: Int = -1

    fun reverseDirection(scannerId: Int) = Point(-x, -y, -z).also { it.id = scannerId }

    fun subtract(other: Point) = Point(x-other.x, y-other.y, z-other.z).also { it.id = id }
  }
}
